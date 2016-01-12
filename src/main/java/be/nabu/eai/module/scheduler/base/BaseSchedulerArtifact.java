package be.nabu.eai.module.scheduler.base;

import java.io.IOException;
import java.util.Date;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.artifacts.api.StartableArtifact;
import be.nabu.libs.artifacts.api.StoppableArtifact;
import be.nabu.libs.resources.api.ResourceContainer;

public abstract class BaseSchedulerArtifact<T extends BaseSchedulerConfiguration> extends JAXBArtifact<T> implements StartableArtifact, StoppableArtifact {
	
	private boolean started;
	
	public BaseSchedulerArtifact(String id, ResourceContainer<?> directory, String fileName, Class<T> configurationClazz, Repository repository) {
		super(id, directory, repository, fileName, configurationClazz);
	}

	abstract public Date getNextRun(Date fromTimestamp);
	
	public Date getInitialRun(Date now) {
		try {
			return getConfiguration().isStartImmediately() ? now : getNextRun(now);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() throws IOException {
		if (getConfiguration().getProvider() != null) {
			getConfiguration().getProvider().refresh();
		}
	}

	@Override
	public void start() throws IOException {
		if (getConfiguration().isEnabled() && getConfiguration().getProvider() != null) {
			if (!getConfiguration().getProvider().isStarted()) {
				getConfiguration().getProvider().start();
			}
			else {
				getConfiguration().getProvider().refresh();
			}
			started = true;
		}
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	
}
