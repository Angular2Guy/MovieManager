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

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.moviemanager.adapter.config.KafkaConfig;
import ch.xxx.moviemanager.domain.model.dto.KafkaEventDto;

@Transactional
@Service
public class KafkaListenerDltHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public KafkaListenerDltHandler(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public boolean sendToDefaultDlt(KafkaEventDto dto) {
		try {
			ListenableFuture<SendResult<String, String>> listenableFuture = this.kafkaTemplate
					.send(KafkaConfig.DEFAULT_DLT_TOPIC, UUID.randomUUID().toString(), this.objectMapper.writeValueAsString(dto));
			listenableFuture.get(3, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException | JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		LOGGER.info("Message send to {}. {}", KafkaConfig.DEFAULT_DLT_TOPIC, dto.toString());
		return true;
	}

}
