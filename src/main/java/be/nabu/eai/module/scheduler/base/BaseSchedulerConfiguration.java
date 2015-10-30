package be.nabu.eai.module.scheduler.base;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.module.scheduler.provider.SchedulerProviderArtifact;
import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.eai.repository.util.KeyValueMapAdapter;
import be.nabu.libs.services.api.DefinedService;

@XmlType(propOrder = { "enabled", "service", "amountOfTimes", "provider", "allowOverlap", "parameters" })
public class BaseSchedulerConfiguration {
	
	private boolean enabled;
	private DefinedService service;
	private Map<String, String> parameters;
	private Long amountOfTimes;
	private SchedulerProviderArtifact provider;
	private boolean allowOverlap;

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	public DefinedService getService() {
		return service;
	}
	public void setService(DefinedService service) {
		this.service = service;
	}

	@XmlJavaTypeAdapter(value = KeyValueMapAdapter.class)
	public Map<String, String> getParameters() {
		if (parameters == null) {
			parameters = new LinkedHashMap<String, String>();
		}
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	public Long getAmountOfTimes() {
		return amountOfTimes;
	}
	public void setAmountOfTimes(Long amountOfTimes) {
		this.amountOfTimes = amountOfTimes;
	}
	
	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	public SchedulerProviderArtifact getProvider() {
		return provider;
	}
	public void setProvider(SchedulerProviderArtifact provider) {
		this.provider = provider;
	}
	
	public boolean isAllowOverlap() {
		return allowOverlap;
	}
	public void setAllowOverlap(boolean allowOverlap) {
		this.allowOverlap = allowOverlap;
	}
}
