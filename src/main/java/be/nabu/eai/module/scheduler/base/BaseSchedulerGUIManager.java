package be.nabu.eai.module.scheduler.base;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import be.nabu.eai.developer.managers.base.BaseJAXBGUIManager;
import be.nabu.eai.repository.api.ArtifactManager;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.services.api.DefinedService;
import be.nabu.libs.types.TypeUtils;
import be.nabu.libs.types.api.Element;

abstract public class BaseSchedulerGUIManager<C extends BaseSchedulerConfiguration, A extends BaseSchedulerArtifact<C>> extends BaseJAXBGUIManager<C, A> {

	public BaseSchedulerGUIManager(String name, Class<A> artifactClass, ArtifactManager<A> artifactManager, Class<C> configurationClass) {
		super(name, artifactClass, artifactManager, configurationClass);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		return null;
	}

	public <V> void setValue(A instance, Property<V> property, V value) {
		if ("service".equals(property.getName())) {
			Map<String, String> properties = getConfiguration(instance).getProperties();
			if (properties == null) {
				properties = new LinkedHashMap<String, String>();
			}
			else {
				properties.clear();
			}
			if (value != null) {
				DefinedService service = (DefinedService) value;
				for (Element<?> element : TypeUtils.getAllChildren(service.getServiceInterface().getInputDefinition())) {
					properties.put(element.getName(), properties.get(element.getName()));
				}
			}
			getConfiguration(instance).setProperties(properties);
		}
		if (!"properties".equals(property.getName())) {
			super.setValue(instance, property, value);
		}
	}

}
