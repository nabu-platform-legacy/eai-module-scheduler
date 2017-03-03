package be.nabu.eai.module.scheduler.provider;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import nabu.misc.cluster.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.nabu.eai.module.cluster.ClusterArtifact;
import be.nabu.eai.module.cluster.api.MasterSwitcher;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.eai.repository.util.SystemPrincipal;
import be.nabu.libs.artifacts.api.StartableArtifact;
import be.nabu.libs.artifacts.api.StoppableArtifact;
import be.nabu.libs.resources.api.ResourceContainer;

public class SchedulerProviderArtifact extends JAXBArtifact<SchedulerProviderConfiguration> implements StartableArtifact, StoppableArtifact {

	private ExecutorService executors;
	private Thread schedulerThread;
	private Scheduler scheduler;
	private MasterSwitcher switcher;
	private ClusterArtifact ownCluster;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public SchedulerProviderArtifact(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "scheduler-provider.xml", SchedulerProviderConfiguration.class);
		switcher = new MasterSwitcher() {
			@Override
			public void switchMaster(String master, boolean amMaster) {
				// we don't want to do anything special during elections
				// if we were the master before, we stay the master of scheduling until a new master is appointed
				// the appointment is synchronous so there should be no (?) conflict, the new master will start up within a short timespan of this one going down
				if (master != null) {
					// if we are master now, it is possible that the provider artifact was shutdown due to lack of schedulers and needs to be restarted
					if (amMaster && !isStarted()) {
						try {
							logger.info("Became master, starting scheduler provider: " + getId());
							start();
						}
						catch (IOException e) {
							logger.error("Became master but could not enable scheduler: " + getId(), e);
						}
					}
				}
			}
		};
	}

	@Override
	public void stop() throws IOException {
		if (scheduler != null) {
			scheduler.stop();
			scheduler = null;
		}
		if (schedulerThread != null) {
			schedulerThread.interrupt();
			schedulerThread = null;
		}
		if (this.executors != null) {
			this.executors.shutdownNow();
			this.executors = null;
		}
	}

	@Override
	public void start() throws IOException {
		if (!isStarted() && getConfiguration().isEnabled()) {
			if (ownCluster == null) {
				ownCluster = Services.getOwnCluster(getRepository().newExecutionContext(SystemPrincipal.ROOT));
			}
			if (ownCluster != null) {
				ownCluster.addSwitcher(switcher);
			}
			int poolSize = getConfiguration().getPoolSize() == null || getConfiguration().getPoolSize() <= 0 ? 1 : getConfiguration().getPoolSize();
			this.executors = Executors.newFixedThreadPool(poolSize);
			scheduler = new Scheduler(this);
			this.schedulerThread = new Thread(scheduler);
			this.schedulerThread.start();
		}
	}

	@Override
	public boolean isStarted() {
		return this.executors != null && this.schedulerThread != null;
	}
	
	Future<?> submit(Runnable runnable) {
		if (executors != null) {
			return executors.submit(runnable);
		}
		else {
			throw new IllegalStateException("No thread pool available");
		}
	}
	
	public void refresh() {
		if (scheduler != null) {
			scheduler.refresh();
			wakeup();
		}
	}

	public void wakeup() {
		if (scheduler != null && scheduler.isSleeping() && schedulerThread != null) {
			schedulerThread.interrupt();
		}
	}
}
