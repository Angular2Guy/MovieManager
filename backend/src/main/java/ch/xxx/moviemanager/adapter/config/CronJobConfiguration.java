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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;

import ch.xxx.moviemanager.domain.model.entity.Actor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT3H")
public class CronJobConfiguration {
	private static final Logger LOG = LoggerFactory.getLogger(CronJobConfiguration.class);
	private final EntityManager entityManager;
	public volatile boolean indexDone = false;

	public CronJobConfiguration(final EntityManagerFactory entityManagerFactory) {
		this.entityManager = entityManagerFactory.createEntityManager();
	}

	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcTemplateLockProvider(dataSource);
	}

	@Async
	@EventListener(ApplicationReadyEvent.class)
	public void checkHibernateSearchIndexes() throws InterruptedException {
		int movieCount = this.entityManager.createNamedQuery("Movie.count", Long.class).getSingleResult().intValue();
		int actorCount = this.entityManager.createNamedQuery("Actor.count", Long.class).getSingleResult().intValue();
		SearchSession searchSession = Search.session( entityManager );
		long actorResults = searchSession.search(Actor.class).where(f -> f.matchAll()).fetchTotalHitCount();
		long movieResults = searchSession.search(Actor.class).where(f -> f.matchAll()).fetchTotalHitCount();
		LOG.info(String.format("DbMovies: %d, DbActors: %d, FtMovies: %d, FtActors: %d", movieCount, actorCount, movieResults, actorResults));
		if (actorResults == 0 || movieResults == 0 || actorResults != actorCount || movieResults != movieCount) {
			searchSession.schemaManager().dropAndCreate(); 
			searchSession.massIndexer().batchSizeToLoadObjects(100).startAndWait();
			this.indexDone = true;
			LOG.info("Hibernate Search Index ready.");
		} else {
			this.indexDone = true;
			LOG.info("Hibernate Search Index ready.");
		}
	}
}
