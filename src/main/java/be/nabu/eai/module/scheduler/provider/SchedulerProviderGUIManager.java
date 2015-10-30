package be.nabu.eai.module.scheduler.provider;

import java.io.IOException;
import java.util.List;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseJAXBGUIManager;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;

public class SchedulerProviderGUIManager extends BaseJAXBGUIManager<SchedulerProviderConfiguration, SchedulerProviderArtifact> {

	public SchedulerProviderGUIManager() {
		super("Scheduler Provider", SchedulerProviderArtifact.class, new SchedulerProviderManager(), SchedulerProviderConfiguration.class);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		return null;
	}

	@Override
	protected SchedulerProviderArtifact newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		return new SchedulerProviderArtifact(entry.getId(), entry.getContainer());
	}

}
