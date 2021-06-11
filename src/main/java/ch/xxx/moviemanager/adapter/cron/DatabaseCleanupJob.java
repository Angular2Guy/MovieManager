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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.xxx.moviemanager.usecase.service.ActorService;
import ch.xxx.moviemanager.usecase.service.MovieService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class DatabaseCleanupJob {
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseCleanupJob.class);
	private final ActorService actorService;
	private final MovieService movieService;
	
	public DatabaseCleanupJob(ActorService actorService, MovieService movieService) {
		this.actorService = actorService;
		this.movieService = movieService;
	}

	@Scheduled(cron = "5 0 * * * ?")
	@SchedulerLock(name = "CleanUp_scheduledTask", lockAtLeastFor = "PT2H", lockAtMostFor = "PT3H")
	public void dbCleanup() {
		LOG.info("Start cleanup Job");
		this.movieService.cleanup();
		this.actorService.cleanup();
		LOG.info("End cleanup Job");
	}
}
