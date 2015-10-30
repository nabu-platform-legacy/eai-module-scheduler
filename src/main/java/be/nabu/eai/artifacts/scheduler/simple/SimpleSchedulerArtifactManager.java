package be.nabu.eai.artifacts.scheduler.simple;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.managers.base.JAXBArtifactManager;
import be.nabu.libs.resources.api.ResourceContainer;

public class SimpleSchedulerArtifactManager extends JAXBArtifactManager<SimpleSchedulerConfiguration, SimpleSchedulerArtifact> {

	public SimpleSchedulerArtifactManager() {
		super(SimpleSchedulerArtifact.class);
	}

	@Override
	protected SimpleSchedulerArtifact newInstance(String id, ResourceContainer<?> container, Repository repository) {
		return new SimpleSchedulerArtifact(id, container);
	}

}
