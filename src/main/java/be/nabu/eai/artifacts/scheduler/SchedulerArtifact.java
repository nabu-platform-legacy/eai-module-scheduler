package be.nabu.eai.artifacts.scheduler;

import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.resources.api.ResourceContainer;

public class SchedulerArtifact extends JAXBArtifact<SchedulerConfiguration> {

	public SchedulerArtifact(String id, ResourceContainer<?> directory) {
		super(id, directory, "scheduler.xml", SchedulerConfiguration.class);
	}

}
