package be.nabu.eai.artifacts.scheduler.provider;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.artifacts.api.StartableArtifact;
import be.nabu.libs.artifacts.api.StoppableArtifact;
import be.nabu.libs.resources.api.ResourceContainer;

public class SchedulerProviderArtifact extends JAXBArtifact<SchedulerProviderConfiguration> implements StartableArtifact, StoppableArtifact {

	private ExecutorService executors;
	private Thread schedulerThread;
	
	public SchedulerProviderArtifact(String id, ResourceContainer<?> directory) {
		super(id, directory, "scheduler-provider.xml", SchedulerProviderConfiguration.class);
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
	}

	@Override
	public void start() throws IOException {
		if (getConfiguration().isEnabled()) {
			int poolSize = getConfiguration().getPoolSize() == null || getConfiguration().getPoolSize() <= 0 ? 1 : getConfiguration().getPoolSize();
			this.executors = Executors.newFixedThreadPool(poolSize);
			this.schedulerThread = new Thread(new Scheduler(this));
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
		return null;
	}

}
