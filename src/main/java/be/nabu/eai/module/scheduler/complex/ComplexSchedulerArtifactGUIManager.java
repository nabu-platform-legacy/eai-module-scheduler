package be.nabu.eai.module.scheduler.complex;

import java.io.IOException;
import java.util.List;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.module.scheduler.base.BaseSchedulerGUIManager;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;

public class ComplexSchedulerArtifactGUIManager extends BaseSchedulerGUIManager<ComplexSchedulerConfiguration, ComplexSchedulerArtifact> {

	public ComplexSchedulerArtifactGUIManager() {
		super("Complex Scheduler", ComplexSchedulerArtifact.class, new ComplexSchedulerArtifactManager(), ComplexSchedulerConfiguration.class);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		return null;
	}

	@Override
	protected ComplexSchedulerArtifact newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		return new ComplexSchedulerArtifact(entry.getId(), entry.getContainer(), entry.getRepository());
	}

	@Override
	public String getCategory() {
		return "Scheduling";
	}
}
