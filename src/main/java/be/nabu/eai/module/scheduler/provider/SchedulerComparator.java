package be.nabu.eai.module.scheduler.provider;

import java.util.Comparator;
import java.util.Date;

import be.nabu.eai.module.scheduler.base.BaseSchedulerArtifact;
import be.nabu.eai.module.scheduler.base.BaseSchedulerConfiguration;

public class SchedulerComparator<T extends BaseSchedulerConfiguration> implements Comparator<BaseSchedulerArtifact<T>> {

	private Date timestamp;

	public SchedulerComparator(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public int compare(BaseSchedulerArtifact<T> o1, BaseSchedulerArtifact<T> o2) {
		return o1.getNextRun(timestamp).compareTo(o2.getNextRun(timestamp));
	}

}
