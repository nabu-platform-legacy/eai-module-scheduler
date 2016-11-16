package be.nabu.eai.module.scheduler.provider;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.api.EnvironmentSpecific;
import be.nabu.eai.api.InterfaceFilter;
import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.libs.services.api.DefinedService;

@XmlRootElement(name = "schedulerProvider")
public class SchedulerProviderConfiguration {
	
	private DefinedService runService;
	private Integer poolSize;
	private boolean enabled;

	@NotNull
	@InterfaceFilter(implement = "be.nabu.eai.module.scheduler.provider.api.SchedulerProvider.run")
	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	public DefinedService getRunService() {
		return runService;
	}
	public void setRunService(DefinedService runService) {
		this.runService = runService;
	}
	
	@EnvironmentSpecific
	public Integer getPoolSize() {
		return poolSize;
	}
	public void setPoolSize(Integer poolSize) {
		this.poolSize = poolSize;
	}
	
	@EnvironmentSpecific
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
