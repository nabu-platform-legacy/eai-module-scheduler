package be.nabu.eai.module.scheduler.provider;

import java.io.IOException;
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
import be.nabu.eai.repository.api.Node;

public class Scheduler implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private SchedulerProviderArtifact schedulerProviderArtifact;
	private List<BaseSchedulerArtifact<?>> scheduledArtifacts;
	private Map<BaseSchedulerArtifact<?>, List<Future<?>>> futures = new HashMap<BaseSchedulerArtifact<?>, List<Future<?>>>();
	
	/**
	 * The timestamps we were aiming for when we woke up
	 */
	private Map<String, Date> scheduledTimestamps = new HashMap<String, Date>();

	public Scheduler(SchedulerProviderArtifact schedulerProviderArtifact) {
		this.schedulerProviderArtifact = schedulerProviderArtifact;
	}

	@Override
	public void run() {
		// we stop on interrupt
		while(!Thread.interrupted()) {
			List<BaseSchedulerArtifact<?>> schedulers = getSchedulers();
			Date newTimestamp = new Date();
			Date earliestAfter = null;
			// start all schedulers between the old timestamp and the new one
			for (BaseSchedulerArtifact<?> scheduler : schedulers) {
				try {
					if (!scheduler.getConfiguration().isEnabled()) {
						continue;
					}
					if (!scheduledTimestamps.containsKey(scheduler.getId())) {
						scheduledTimestamps.put(scheduler.getId(), scheduler.getInitialRun(newTimestamp));
					}
					Date nextRun = scheduledTimestamps.get(scheduler.getId());
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
			// no more schedulers to run, shut it down
			if (earliestAfter == null) {
				try {
					schedulerProviderArtifact.stop();
				}
				catch (IOException e) {
					logger.error("Could not shut down scheduler provider", e);
				}
				break;
			}
			else {
				try {
					long millis = earliestAfter.getTime() - new Date().getTime();
					if (millis > 0) {
						Thread.sleep(millis);
					}
				}
				// if interrupted, we stop
				catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	public void refresh() {
		synchronized(this) {
			scheduledArtifacts = null;
		}
	}
	
	private List<BaseSchedulerArtifact<?>> getSchedulers() {
		if (scheduledArtifacts == null) {
			synchronized(this) {
				if (scheduledArtifacts == null) {
					List<BaseSchedulerArtifact<?>> scheduledArtifacts = new ArrayList<BaseSchedulerArtifact<?>>();
					for (Node node : schedulerProviderArtifact.getRepository().getNodes(BaseSchedulerArtifact.class)) {
						try {
							BaseSchedulerArtifact<?> scheduler = (BaseSchedulerArtifact<?>) node.getArtifact();
							try {
								BaseSchedulerConfiguration configuration = scheduler.getConfiguration();
								if (schedulerProviderArtifact.equals(configuration.getProvider())) {
									scheduledArtifacts.add(scheduler);
								}
							}
							catch (IOException e) {
								logger.error("Could not load scheduler: " + scheduler, e);
							}
						}
						catch (Exception e) {
							logger.error("Could not load scheduler", e);
						}
					}
					this.scheduledArtifacts = scheduledArtifacts;
				}
			}
		}
		return scheduledArtifacts;
	}
}
