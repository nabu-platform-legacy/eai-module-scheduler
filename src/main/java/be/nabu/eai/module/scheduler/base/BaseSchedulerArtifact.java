package be.nabu.eai.module.scheduler.base;

import java.util.Date;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.resources.api.ResourceContainer;

public abstract class BaseSchedulerArtifact<T extends BaseSchedulerConfiguration> extends JAXBArtifact<T> {
	
	public BaseSchedulerArtifact(String id, ResourceContainer<?> directory, String fileName, Class<T> configurationClazz, Repository repository) {
		super(id, directory, repository, fileName, configurationClazz);
	}

	abstract public Date getNextRun(Date fromTimestamp);

}
