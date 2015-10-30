package be.nabu.eai.artifacts.scheduler.simple;

import javax.xml.bind.annotation.XmlRootElement;

import be.nabu.eai.artifacts.scheduler.base.BaseSchedulerConfiguration;

@XmlRootElement(name = "simpleScheduler")
public class SimpleSchedulerConfiguration extends BaseSchedulerConfiguration {
	
	private long repeatInterval;

	public long getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
}
