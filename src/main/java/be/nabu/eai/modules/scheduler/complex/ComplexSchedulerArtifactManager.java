package be.nabu.eai.modules.scheduler.complex;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.managers.base.JAXBArtifactManager;
import be.nabu.libs.resources.api.ResourceContainer;

public class ComplexSchedulerArtifactManager extends JAXBArtifactManager<ComplexSchedulerConfiguration, ComplexSchedulerArtifact> {

	public ComplexSchedulerArtifactManager() {
		super(ComplexSchedulerArtifact.class);
	}

	@Override
	protected ComplexSchedulerArtifact newInstance(String id, ResourceContainer<?> container, Repository repository) {
		return new ComplexSchedulerArtifact(id, container);
	}

}
