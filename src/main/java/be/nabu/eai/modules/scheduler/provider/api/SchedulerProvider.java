package be.nabu.eai.modules.scheduler.provider.api;

import java.util.Date;

import javax.jws.WebParam;
import javax.validation.constraints.NotNull;

public interface SchedulerProvider {
	public void run(@NotNull @WebParam(name = "serviceId") String serviceId, @WebParam(name = "serviceInput") Object properties, @NotNull @WebParam(name = "timestamp") Date timestamp, @WebParam(name = "maxAmountOfRuns") Long maxAmountOfRuns);
}
