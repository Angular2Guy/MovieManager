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
package ch.xxx.moviemanager.adapter.cron;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.xxx.moviemanager.usecase.service.ActorService;
import ch.xxx.moviemanager.usecase.service.DataMigrationService;
import ch.xxx.moviemanager.usecase.service.MovieService;
import ch.xxx.moviemanager.usecase.service.UserDetailService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class CronJobs {
	private static final Logger LOG = LoggerFactory.getLogger(CronJobs.class);
	private static volatile boolean MIGRATIONS_DONE = false;
	private final ActorService actorService;
	private final MovieService movieService;
	private final UserDetailService userService;
	private final DataMigrationService dataMigrationService;
	private Environment environment;

	public CronJobs(ActorService actorService, MovieService movieService, UserDetailService userService,
			Environment environment, DataMigrationService dataMigrationService) {
		this.actorService = actorService;
		this.movieService = movieService;
		this.userService = userService;
		this.environment = environment;
		this.dataMigrationService = dataMigrationService;
	}

	@Scheduled(initialDelay = 2000, fixedDelay = 3600000)
	@SchedulerLock(name = "Migrations_scheduledTask", lockAtLeastFor = "PT2H", lockAtMostFor = "PT3H")
	public void startMigrations() {
		if (!MIGRATIONS_DONE) {
			this.dataMigrationService.encryptUserKeys().thenApply(result -> {
				LOG.info("Users migrated: {}", result);
				return result;
			});
			MIGRATIONS_DONE = true;
		}
	}

	@Scheduled(cron = "5 0 1 * * ?")
//	@Scheduled(fixedRate = 10000)
	@SchedulerLock(name = "CleanUp_scheduledTask", lockAtLeastFor = "PT2H", lockAtMostFor = "PT3H")
	public void dbCleanup() {
		LOG.info("Start cleanup Job");
		this.movieService.cleanup();
		this.actorService.cleanup();
		LOG.info("End cleanup Job");
	}

	@Scheduled(fixedRate = 90000)
	@Order(1)
	// @SchedulerLock(name = "LoggedOutUsers_scheduledTask", lockAtLeastFor =
	// "PT1M", lockAtMostFor = "PT80s")
	public void updateLoggedOutUsers() {
		if (Stream.of(this.environment.getActiveProfiles()).noneMatch(myProfile -> myProfile.contains("kafka"))) {
			LOG.info("Update logged out users.");
			this.userService.updateLoggedOutUsers();
		}
	}
}
