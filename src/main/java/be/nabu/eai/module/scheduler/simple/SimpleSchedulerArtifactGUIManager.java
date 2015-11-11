package be.nabu.eai.module.scheduler.simple;

import java.io.IOException;
import java.util.List;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.module.scheduler.base.BaseSchedulerGUIManager;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;

public class SimpleSchedulerArtifactGUIManager extends BaseSchedulerGUIManager<SimpleSchedulerConfiguration, SimpleSchedulerArtifact> {

	public SimpleSchedulerArtifactGUIManager() {
		super("Simple Scheduler", SimpleSchedulerArtifact.class, new SimpleSchedulerArtifactManager(), SimpleSchedulerConfiguration.class);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		return null;
	}

	@Override
	protected SimpleSchedulerArtifact newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		return new SimpleSchedulerArtifact(entry.getId(), entry.getContainer(), entry.getRepository());
	}

	@Override
	public String getCategory() {
		return "Scheduling";
	}
}
