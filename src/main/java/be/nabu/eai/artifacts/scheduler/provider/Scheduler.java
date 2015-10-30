package be.nabu.eai.artifacts.scheduler.provider;

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

import be.nabu.eai.artifacts.scheduler.base.BaseSchedulerArtifact;
import be.nabu.eai.artifacts.scheduler.base.BaseSchedulerConfiguration;
import be.nabu.eai.repository.EAIResourceRepository;

public class Scheduler implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private SchedulerProviderArtifact schedulerProviderArtifact;
	private List<BaseSchedulerArtifact<?>> scheduledArtifacts;
	private Map<BaseSchedulerArtifact<?>, List<Future<?>>> futures = new HashMap<BaseSchedulerArtifact<?>, List<Future<?>>>();
	
	/**
	 * The timestamp we were aiming up when we woke up
	 */
	private Date scheduledTimestamp = new Date();

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
					Date nextRun = scheduler.getNextRun(scheduledTimestamp);
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
						// only run it if there is no overlap or it is allowed
						if (scheduler.getConfiguration().isAllowOverlap() || futures.get(scheduler).isEmpty()) {
							futures.get(scheduler).add(schedulerProviderArtifact.submit(new SchedulerRunner(scheduler)));
							nextRun = scheduler.getNextRun(nextRun);
						}
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
			scheduledTimestamp = newTimestamp;
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
					Thread.sleep(earliestAfter.getTime() - new Date().getTime());
				}
				// if interrupted, we stop
				catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	private List<BaseSchedulerArtifact<?>> getSchedulers() {
		if (scheduledArtifacts == null) {
			synchronized(this) {
				if (scheduledArtifacts == null) {
					List<BaseSchedulerArtifact<?>> scheduledArtifacts = new ArrayList<BaseSchedulerArtifact<?>>();
					for (BaseSchedulerArtifact<?> scheduler : EAIResourceRepository.getInstance().getArtifacts(BaseSchedulerArtifact.class)) {
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
					this.scheduledArtifacts = scheduledArtifacts;
				}
			}
		}
		return scheduledArtifacts;
	}
}