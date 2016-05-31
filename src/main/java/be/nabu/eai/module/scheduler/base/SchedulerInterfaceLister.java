package be.nabu.eai.module.scheduler.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.nabu.eai.developer.api.InterfaceLister;
import be.nabu.eai.developer.util.InterfaceDescriptionImpl;

public class SchedulerInterfaceLister implements InterfaceLister {

	private static Collection<InterfaceDescription> descriptions = null;
	
	@Override
	public Collection<InterfaceDescription> getInterfaces() {
		if (descriptions == null) {
			synchronized(SchedulerInterfaceLister.class) {
				if (descriptions == null) {
					List<InterfaceDescription> descriptions = new ArrayList<InterfaceDescription>();
					descriptions.add(new InterfaceDescriptionImpl("Scheduler", "Provider", "be.nabu.eai.module.scheduler.provider.api.SchedulerProvider.run"));
					SchedulerInterfaceLister.descriptions = descriptions;
				}
			}
		}
		return descriptions;
	}

}
