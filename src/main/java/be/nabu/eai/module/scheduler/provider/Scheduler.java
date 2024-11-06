/*
* Copyright (C) 2015 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.eai.module.scheduler.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.nabu.eai.module.scheduler.base.BaseSchedulerArtifact;
import be.nabu.eai.module.scheduler.base.BaseSchedulerConfiguration;

public class Scheduler implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private SchedulerProviderArtifact schedulerProviderArtifact;
	private List<BaseSchedulerArtifact<?>> scheduledArtifacts;
	private Map<BaseSchedulerArtifact<?>, List<Future<?>>> futures = new HashMap<BaseSchedulerArtifact<?>, List<Future<?>>>();
	private boolean stop;
	private boolean sleeping;
	
	/**
	 * The timestamps we were aiming for when we woke up
	 */
	private Map<String, Date> scheduledTimestamps = new HashMap<String, Date>();

	public Scheduler(SchedulerProviderArtifact schedulerProviderArtifact) {
		this.schedulerProviderArtifact = schedulerProviderArtifact;
	}

	@Override
	public void run() {
		stop = false;
		while (!stop) {
			try {
				List<BaseSchedulerArtifact<?>> schedulers = getSchedulers();
				Date newTimestamp = new Date();
				Date earliestAfter = null;
				List<String> timestampsToRemove = new ArrayList<String>(scheduledTimestamps.keySet());
				// start all schedulers between the old timestamp and the new one
				for (BaseSchedulerArtifact<?> scheduler : schedulers) {
					if (stop) {
						break;
					}
					try {
						if (!scheduler.isStarted()) {
							continue;
						}
						// don't remove the timestamp for this scheduler, we are running it
						timestampsToRemove.remove(scheduler.getId());
						if (!scheduledTimestamps.containsKey(scheduler.getId())) {
							scheduledTimestamps.put(scheduler.getId(), scheduler.getInitialRun(newTimestamp));
						}
						Date nextRun = scheduledTimestamps.get(scheduler.getId());
						logger.debug("Scheduler {} next run: {}", scheduler.getId(), nextRun);
						// if it is before or on the new timestamp, we need to run it
						if (nextRun != null && !nextRun.after(newTimestamp)) {
							if (!futures.containsKey(scheduler)) {
								futures.put(scheduler, new ArrayList<Future<?>>());
							}
							// remove futures that are completed
							Iterator<Future<?>> iterator = futures.get(scheduler).iterator();
							while (iterator.hasNext()) {
								if (iterator.next().isDone()) {
									iterator.remove();
								}
							}
							while(!nextRun.after(newTimestamp)) {
								// only run it if there is no overlap or it is allowed
								if (scheduler.getConfiguration().isAllowOverlap() || futures.get(scheduler).isEmpty()) {
									futures.get(scheduler).add(schedulerProviderArtifact.submit(new SchedulerRunner(scheduler, nextRun)));
								}
								nextRun = scheduler.getNextRun(nextRun);
							}
							scheduledTimestamps.put(scheduler.getId(), nextRun);
						}
						// note that even for those that are now being run, we calculate the next run, it could come before whatever other scheduler we have in mind
						if (nextRun != null && nextRun.after(newTimestamp) && (earliestAfter == null || nextRun.before(earliestAfter))) {
							earliestAfter = nextRun;
						}
					}
					catch (Exception e) {
						logger.error("Could not process scheduler: " + scheduler, e);
					}
				}
				if (stop) {
					break;
				}
				// remove the timestamps for all schedulers we don't actually run, this could lead to misleading "last" timestamps when we do run them again (if ever)
				for (String timestampToRemove : timestampsToRemove) {
					scheduledTimestamps.remove(timestampToRemove);
				}
				// if we were interrupted during our processing, we don't want to go into sleep, we need to recalculate everything
				if (!Thread.interrupted()) {
					// no more schedulers to run, sleep for a minute
					if (earliestAfter == null) {
						logger.debug("No active schedulers found for provider {}, going into sleep mode", schedulerProviderArtifact.getId());
						sleeping = true;
						Thread.sleep(60000);
						sleeping = false;
					}
					else {
						long millis = earliestAfter.getTime() - new Date().getTime();
						if (millis > 0) {
							sleeping = true;
							Thread.sleep(millis);
							sleeping = false;
						}
					}
				}
				else {
					logger.info("Skipping sleep mode due to interrupt");
				}
			}
			// if interrupted, we continue, use "stop" to actually stop
			catch (InterruptedException e) {
				logger.info("Scheduler " + schedulerProviderArtifact.getId() + " interrupted (was sleeping: " + sleeping + ")");
				sleeping = false;
				continue;
			}
		}
	}

	public void refresh() {
		synchronized(this) {
			scheduledArtifacts = null;
		}
	}
	
	public boolean isSleeping() {
		return sleeping;
	}
	
	public void stop() {
		this.stop = true;
	}

	private List<BaseSchedulerArtifact<?>> getSchedulers() {
		if (scheduledArtifacts == null) {
			synchronized(this) {
				if (scheduledArtifacts == null) {
					List<BaseSchedulerArtifact<?>> scheduledArtifacts = new ArrayList<BaseSchedulerArtifact<?>>();
					for (BaseSchedulerArtifact<?> scheduler : schedulerProviderArtifact.getRepository().getArtifacts(BaseSchedulerArtifact.class)) {
						try {
							BaseSchedulerConfiguration configuration = scheduler.getConfiguration();
							if (schedulerProviderArtifact.equals(configuration.getProvider())) {
								scheduledArtifacts.add(scheduler);
							}
						}
						catch (Exception e) {
							logger.error("Could not load scheduler: " + scheduler, e);
						}
					}
					this.scheduledArtifacts = scheduledArtifacts;
				}
				// if we wait for return out of synchronized block, it _can_ be set to null by the refresh(), this happened on average 10% of the time at startup
				return scheduledArtifacts;
			}
		}
		return scheduledArtifacts;
	}
}
