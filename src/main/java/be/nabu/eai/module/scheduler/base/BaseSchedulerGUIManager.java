package be.nabu.eai.module.scheduler.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseJAXBGUIManager;
import be.nabu.eai.developer.managers.util.SimpleProperty;
import be.nabu.eai.developer.managers.util.SimplePropertyUpdater;
import be.nabu.eai.developer.util.Confirm;
import be.nabu.eai.developer.util.Confirm.ConfirmType;
import be.nabu.eai.developer.util.EAIDeveloperUtils;
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

	@SuppressWarnings("unchecked")
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
		else if (value instanceof Map) {
			getConfiguration(instance).getProperties().putAll(((Map<? extends String, ? extends String>) value));
		}
	}

	@Override
	protected void display(A instance, Pane pane) {
		AnchorPane child = new AnchorPane();
		super.display(instance, child);
		VBox vbox = new VBox();
		Button scheduler = new Button("Get Schedule");
		scheduler.addEventHandler(ActionEvent.ANY, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				final SimplePropertyUpdater updater = new SimplePropertyUpdater(true, new HashSet<Property<?>>(Arrays.asList(new SimpleProperty<Integer>("Amount of Runs", Integer.class, false))));
				EAIDeveloperUtils.buildPopup(MainController.getInstance(), updater, "Amount of Runs", new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent arg0) {
						Integer amount = updater.getValue("Amount of Runs");
						if (amount == null) {
							amount = 10;
						}
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
						List<Date> runs = new ArrayList<Date>();
						StringBuilder builder = new StringBuilder();
						Date date = new Date();
						for (int i = 0; i < amount; i++) {
							Date nextRun = instance.getNextRun(date);
							if (nextRun == null) {
								break;
							}
							runs.add(nextRun);
							if (i > 0) {
								builder.append("\n");
							}
							builder.append(formatter.format(nextRun));
							date = nextRun;
						}
						Confirm.confirm(ConfirmType.INFORMATION, "Next " + amount + " runs", builder.toString(), null);
					}
				}, false);
			}
		});
		vbox.getChildren().addAll(scheduler, child);
		pane.getChildren().add(vbox);
	}

}
