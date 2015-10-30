package be.nabu.eai.modules.scheduler.provider;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.managers.base.JAXBArtifactManager;
import be.nabu.libs.resources.api.ResourceContainer;

public class SchedulerProviderManager extends JAXBArtifactManager<SchedulerProviderConfiguration, SchedulerProviderArtifact> {

	public SchedulerProviderManager() {
		super(SchedulerProviderArtifact.class);
	}

	@Override
	protected SchedulerProviderArtifact newInstance(String id, ResourceContainer<?> container, Repository repository) {
		return new SchedulerProviderArtifact(id, container);
	}

}
