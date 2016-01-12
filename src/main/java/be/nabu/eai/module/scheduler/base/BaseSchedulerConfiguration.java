package be.nabu.eai.module.scheduler.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.api.Enumerator;
import be.nabu.eai.api.EnvironmentSpecific;
import be.nabu.eai.api.ValueEnumerator;
import be.nabu.eai.module.scheduler.provider.SchedulerProviderArtifact;
import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.eai.repository.util.KeyValueMapAdapter;
import be.nabu.libs.services.api.DefinedService;

@XmlType(propOrder = { "enabled", "startImmediately", "service", "amountOfTimes", "provider", "allowOverlap", "targets", "properties" })
public class BaseSchedulerConfiguration {
	
	private boolean enabled;
	private DefinedService service;
	private Map<String, String> properties;
	private Long amountOfTimes;
	private SchedulerProviderArtifact provider;
	private boolean allowOverlap;
	private List<String> targets;
	private boolean startImmediately;

	@EnvironmentSpecific
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
	public Map<String, String> getProperties() {
		if (properties == null) {
			properties = new LinkedHashMap<String, String>();
		}
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public Long getAmountOfTimes() {
		return amountOfTimes;
	}
	public void setAmountOfTimes(Long amountOfTimes) {
		this.amountOfTimes = amountOfTimes;
	}
	
	public boolean isStartImmediately() {
		return startImmediately;
	}
	public void setStartImmediately(boolean startImmediately) {
		this.startImmediately = startImmediately;
	}
	
	@EnvironmentSpecific
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
	
	@EnvironmentSpecific
	@ValueEnumerator(enumerator = TargetEnumerator.class)
	public List<String> getTargets() {
		return targets;
	}
	public void setTargets(List<String> target) {
		this.targets = target;
	}
	
	public static class TargetEnumerator implements Enumerator {

		@Override
		public List<?> enumerate() {
			List<String> targets = new ArrayList<String>();
			targets.add("$any");
			targets.add("$all");
			return targets;
		}
		
	}
}
