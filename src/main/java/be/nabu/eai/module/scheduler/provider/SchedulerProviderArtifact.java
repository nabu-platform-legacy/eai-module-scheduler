package be.nabu.eai.module.scheduler.provider;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.artifacts.api.StartableArtifact;
import be.nabu.libs.artifacts.api.StoppableArtifact;
import be.nabu.libs.resources.api.ResourceContainer;

public class SchedulerProviderArtifact extends JAXBArtifact<SchedulerProviderConfiguration> implements StartableArtifact, StoppableArtifact {

	private ExecutorService executors;
	private Thread schedulerThread;
	private Scheduler scheduler;
	
	public SchedulerProviderArtifact(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "scheduler-provider.xml", SchedulerProviderConfiguration.class);
	}

	@Override
	public void stop() throws IOException {
		if (this.executors != null) {
			this.executors.shutdownNow();
			this.executors = null;
		}
		if (schedulerThread != null) {
			schedulerThread.interrupt();
			schedulerThread = null;
		}
		scheduler = null;
	}

	@Override
	public void start() throws IOException {
		if (getConfiguration().isEnabled()) {
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
		}
	}
}
