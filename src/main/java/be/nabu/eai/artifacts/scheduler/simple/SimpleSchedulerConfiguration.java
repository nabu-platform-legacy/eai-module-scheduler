package be.nabu.eai.artifacts.scheduler.simple;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import be.nabu.eai.artifacts.scheduler.base.BaseSchedulerConfiguration;

@XmlRootElement(name = "simpleScheduler")
@XmlType(propOrder = { "repeatInterval" })
public class SimpleSchedulerConfiguration extends BaseSchedulerConfiguration {
	
	private long repeatInterval;

	public long getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
}
