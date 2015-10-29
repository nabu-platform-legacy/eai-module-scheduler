package be.nabu.eai.artifacts.scheduler;

import java.io.IOException;
import java.util.List;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseJAXBGUIManager;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;

public class SchedulerArtifactGUIManager extends BaseJAXBGUIManager<SchedulerConfiguration, SchedulerArtifact> {

	public SchedulerArtifactGUIManager() {
		super("Scheduler", SchedulerArtifact.class, new SchedulerArtifactManager(), SchedulerConfiguration.class);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		return null;
	}

	@Override
	protected SchedulerArtifact newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		return new SchedulerArtifact(entry.getId(), entry.getContainer());
	}

}
