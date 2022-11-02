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
package ch.xxx.moviemanager.adapter.config;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

import org.apache.kafka.clients.DefaultHostResolver;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
@EnableKafka
@Profile("kafka | prod-kafka")
public class KafkaConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfig.class);
	public static final String NEW_USER_TOPIC = "new-user-topic";
	public static final String DEFAULT_DLT_TOPIC = "default-dlt-topic";
	public static final String USER_LOGOUT_TOPIC = "user-logout-topic";
	// private static final String USER_LOGOUT_DLT_TOPIC = "user-logout-topic-retry";
	private static final String GZIP = "gzip";
	private static final String ZSTD = "zstd";

	private ProducerFactory<String, String> producerFactory;
	@Value("${kafka.server.name}")
	private String kafkaServerName;
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;
	@Value("${spring.kafka.producer.transaction-id-prefix}")
	private String transactionIdPrefix;
	@Value("${spring.kafka.producer.compression-type}")
	private String compressionType;

	public KafkaConfig(ProducerFactory<String, String> producerFactory) {
		this.producerFactory = producerFactory;
	}

	@PostConstruct
	public void init() {
		String bootstrap = this.bootstrapServers.split(":")[0].trim();
		if (bootstrap.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
			DefaultHostResolver.IP_ADDRESS = bootstrap;
		} else if(!bootstrap.isEmpty()) {
			DefaultHostResolver.KAFKA_SERVICE_NAME = bootstrap;
		}
		LOGGER.info("Kafka Servername: {} Kafka Servicename: {} Ip Address: {}", DefaultHostResolver.KAFKA_SERVER_NAME,
				DefaultHostResolver.KAFKA_SERVICE_NAME, DefaultHostResolver.IP_ADDRESS);
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(this.producerFactory);
		kafkaTemplate.setTransactionIdPrefix(this.transactionIdPrefix);
		return new KafkaTemplate<>(this.producerFactory);
	}

	@Bean("kafkaRetryTemplate")
	public KafkaTemplate<String, String> kafkaRetryTemplate() {
		KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(this.producerFactory);
		kafkaTemplate.setTransactionIdPrefix(this.transactionIdPrefix);
		kafkaTemplate.setAllowNonTransactional(true);
		return kafkaTemplate;
	}

	@Bean
	public KafkaTransactionManager<String, String> kafkaTransactionManager() {
		KafkaTransactionManager<String, String> manager = new KafkaTransactionManager<>(this.producerFactory);
		return manager;
	}

	@Bean
	public NewTopic newUserTopic() {
		return TopicBuilder.name(KafkaConfig.NEW_USER_TOPIC)
				.config(TopicConfig.COMPRESSION_TYPE_CONFIG, this.compressionType).compact().build();
	}

	@Bean
	public NewTopic userLogoutTopic() {
		return TopicBuilder.name(KafkaConfig.USER_LOGOUT_TOPIC)
				.config(TopicConfig.COMPRESSION_TYPE_CONFIG, this.compressionType).compact().build();
	}

	@Bean
	public NewTopic defaultDltTopic() {
		return TopicBuilder.name(KafkaConfig.DEFAULT_DLT_TOPIC)
				.config(TopicConfig.COMPRESSION_TYPE_CONFIG, this.compressionType).compact().build();
	}
	
	@Bean
	@Primary
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doOnStartup() {
		// this.newUserTopic();
		// this.userLogoutTopic();
	}
}
