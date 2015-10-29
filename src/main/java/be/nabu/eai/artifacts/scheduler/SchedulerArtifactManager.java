package be.nabu.eai.artifacts.scheduler;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.managers.base.JAXBArtifactManager;
import be.nabu.libs.resources.api.ResourceContainer;

public class SchedulerArtifactManager extends JAXBArtifactManager<SchedulerConfiguration, SchedulerArtifact> {

	public SchedulerArtifactManager() {
		super(SchedulerArtifact.class);
	}

	@Override
	protected SchedulerArtifact newInstance(String id, ResourceContainer<?> container, Repository repository) {
		return new SchedulerArtifact(id, container);
	}

}
