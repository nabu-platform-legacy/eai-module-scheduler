package be.nabu.eai.artifacts.scheduler.provider.api;

import java.util.Date;

public interface SchedulerProvider {
	public void run(String serviceId, Object properties, Date timestamp, Long maxAmountOfRuns);
}
