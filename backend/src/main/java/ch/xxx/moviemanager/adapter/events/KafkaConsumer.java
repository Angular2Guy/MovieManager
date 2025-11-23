/**
 * Copyright 2019 Sven Loesekann
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.xxx.moviemanager.adapter.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import ch.xxx.moviemanager.adapter.config.KafkaConfig;
import ch.xxx.moviemanager.domain.model.dto.KafkaEventDto;
import ch.xxx.moviemanager.domain.model.dto.RevokedTokenDto;
import ch.xxx.moviemanager.domain.model.dto.UserDto;
import ch.xxx.moviemanager.usecase.service.UserDetailServiceEvents;
import tools.jackson.databind.json.JsonMapper;

@Service
@Profile("kafka | prod-kafka")
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private final JsonMapper jsonMapper;
    private final UserDetailServiceEvents appUserService;
    private final KafkaListenerDltHandler kafkaListenerDltHandler;

    public KafkaConsumer(JsonMapper jsonMapper, UserDetailServiceEvents appUserService, KafkaListenerDltHandler kafkaListenerDltHandler) {
        this.jsonMapper = jsonMapper;
        this.appUserService = appUserService;
        this.kafkaListenerDltHandler = kafkaListenerDltHandler;
    }

    @RetryableTopic(kafkaTemplate = "kafkaRetryTemplate", backOff = @BackOff(delay = 1000, multiplier = 2.0),
            attempts = "3", autoCreateTopics = "true", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = KafkaConfig.NEW_USER_TOPIC)
    public void consumerForNewUserTopic(String message) {
        LOGGER.info("consumerForNewUserTopic [{}]", message);
        try {
            UserDto dto = this.jsonMapper.readValue(message, UserDto.class);
            this.appUserService.signinMsg(dto);
        } catch (Exception e) {
            LOGGER.warn("send failed consumerForNewUserTopic [{}]", message);
            this.kafkaListenerDltHandler.sendToDefaultDlt(new KafkaEventDto(KafkaConfig.DEFAULT_DLT_TOPIC, message));
        }
    }

    @DltHandler
    public void dlt(String in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        LOGGER.info(in + " from " + topic);
    }

    @RetryableTopic(backOff = @BackOff(delay = 1000, multiplier = 2.0), attempts = "3", autoCreateTopics = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = KafkaConfig.USER_LOGOUT_TOPIC)
    public void consumerForUserLogoutsTopic(String message) {
        LOGGER.info("consumerForUserLogoutsTopic [{}]", message);
        try {
            RevokedTokenDto dto = this.jsonMapper.readValue(message, RevokedTokenDto.class);
            this.appUserService.logoutMsg(dto);
        } catch (Exception e) {
            LOGGER.warn("send failed consumerForUserLogoutsTopic [{}]", message);
            this.kafkaListenerDltHandler.sendToDefaultDlt(new KafkaEventDto(KafkaConfig.DEFAULT_DLT_TOPIC, message));
        }
    }
}
