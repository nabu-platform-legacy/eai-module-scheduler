package be.nabu.eai.modules.scheduler.provider;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.api.InterfaceFilter;
import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.libs.services.api.DefinedService;

@XmlRootElement(name = "schedulerProvider")
public class SchedulerProviderConfiguration {
	
	private DefinedService runService;
	private Integer poolSize;
	private boolean enabled;

	@InterfaceFilter(implement = "be.nabu.eai.modules.scheduler.provider.api.SchedulerProvider.run")
	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	public DefinedService getRunService() {
		return runService;
	}
	public void setRunService(DefinedService runService) {
		this.runService = runService;
	}
	public Integer getPoolSize() {
		return poolSize;
	}
	public void setPoolSize(Integer poolSize) {
		this.poolSize = poolSize;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
