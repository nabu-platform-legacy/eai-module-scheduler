package be.nabu.eai.artifacts.scheduler.complex;

import java.io.IOException;
import java.util.List;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseJAXBGUIManager;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;

public class ComplexSchedulerArtifactGUIManager extends BaseJAXBGUIManager<ComplexSchedulerConfiguration, ComplexSchedulerArtifact> {

	public ComplexSchedulerArtifactGUIManager() {
		super("Scheduler", ComplexSchedulerArtifact.class, new ComplexSchedulerArtifactManager(), ComplexSchedulerConfiguration.class);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		return null;
	}

	@Override
	protected ComplexSchedulerArtifact newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		return new ComplexSchedulerArtifact(entry.getId(), entry.getContainer());
	}

}
