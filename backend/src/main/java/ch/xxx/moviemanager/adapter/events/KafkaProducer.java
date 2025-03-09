/**
 *    Copyright 2019 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.moviemanager.adapter.events;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.moviemanager.adapter.config.KafkaConfig;
import ch.xxx.moviemanager.domain.exceptions.AuthenticationException;
import ch.xxx.moviemanager.domain.model.dto.KafkaEventDto;
import ch.xxx.moviemanager.domain.model.dto.LogoutEvent;
import ch.xxx.moviemanager.domain.model.dto.RevokedTokenDto;
import ch.xxx.moviemanager.domain.model.dto.SigninEvent;
import ch.xxx.moviemanager.domain.model.dto.UserDto;
import ch.xxx.moviemanager.domain.producer.EventProducer;
import jakarta.transaction.Transactional;

@Service
@Profile("kafka | prod-kafka")
public class KafkaProducer implements EventProducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final AdminClient adminClient;

	public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
			AdminClient adminClient) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.adminClient = adminClient;
	}

	@Async
	@Transactional
	@TransactionalEventListener
	public void receiveLogoutEvent(LogoutEvent logoutEvent) {
		this.sendLogoutMsg(logoutEvent.revokedTokenDto());
	}	
	
	@Override
	public void sendLogoutMsg(RevokedTokenDto revokedTokenDto) {
		try {
			String msg = this.objectMapper.writeValueAsString(revokedTokenDto);
			CompletableFuture<SendResult<String, String>> listenableFuture = this.kafkaTemplate
					.send(KafkaConfig.USER_LOGOUT_TOPIC, revokedTokenDto.getUuid(), msg);
			listenableFuture.get(2, TimeUnit.SECONDS);
		} catch (Exception e) {
			LOGGER.error("sendLogoutMsg ", e);
			throw new AuthenticationException("Logout failed.");
		}
		LOGGER.info("send logout msg: {}", revokedTokenDto.toString());
	}

	@Async
	@Transactional
	@TransactionalEventListener
	public void receiveSigninEvent(SigninEvent signinEvent) {
		this.sendNewUserMsg(signinEvent.userDto());
	}
	
	@Override
	public void sendNewUserMsg(UserDto appUserDto) {
		try {
			String msg = this.objectMapper.writeValueAsString(appUserDto);
			CompletableFuture<SendResult<String, String>> listenableFuture = this.kafkaTemplate
					.send(KafkaConfig.NEW_USER_TOPIC, appUserDto.getUsername(), msg);
			listenableFuture.get(2, TimeUnit.SECONDS);
		} catch (Exception e) {
			LOGGER.error("SendNewUserMsg ", e);
			throw new AuthenticationException("User creation failed.");
		}
		LOGGER.info("send new user msg: {}", appUserDto.toString());
	}

	@Override
	public void sendKafkaEvent(KafkaEventDto kafkaEventDto) {
		try {
			Set<String> topicNames = this.adminClient.listTopics().names().get(2, TimeUnit.SECONDS);
			if (topicNames.stream()
					.anyMatch(topicName -> topicName.trim().equalsIgnoreCase(kafkaEventDto.getTopicName().trim()))) {
				CompletableFuture<SendResult<String, String>> listenableFuture = this.kafkaTemplate
						.send(kafkaEventDto.getTopicName(), kafkaEventDto.getTopicContent());
				listenableFuture.get(2, TimeUnit.SECONDS);
			} else {
				LOGGER.error("SendKafkaEven Topic {} not found.", kafkaEventDto.getTopicName());
				throw new AuthenticationException("Kafka Event failed.");
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			LOGGER.error("SendKafkaEvent ", e);
			throw new AuthenticationException("Kafka Event failed.");
		}
		LOGGER.info("send Kafka event to {}", kafkaEventDto.getTopicName());
	}
}
