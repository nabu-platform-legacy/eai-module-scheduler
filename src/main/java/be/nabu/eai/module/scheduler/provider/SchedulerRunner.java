package be.nabu.eai.module.scheduler.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.nabu.eai.module.scheduler.base.BaseSchedulerArtifact;
import be.nabu.eai.repository.Notification;
import be.nabu.eai.repository.util.SystemPrincipal;
import be.nabu.libs.services.ServiceRuntime;
import be.nabu.libs.services.ServiceUtils;
import be.nabu.libs.services.api.DefinedService;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

public class SchedulerRunner implements Runnable {

	private BaseSchedulerArtifact<?> scheduled;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Date timestamp;

	public SchedulerRunner(BaseSchedulerArtifact<?> scheduled, Date timestamp) {
		this.scheduled = scheduled;
		this.timestamp = timestamp;
	}

	@Override
	public void run() {
		try {
			// create the input for the target service
			ComplexContent content = scheduled.getConfiguration().getService().getServiceInterface().getInputDefinition().newInstance();
			for (String key : scheduled.getConfiguration().getProperties().keySet()) {
				content.set(key, scheduled.getConfiguration().getProperties().get(key));
			}
			// create the input for the framework service
			DefinedService frameworkService = scheduled.getConfiguration().getProvider().getConfiguration().getRunService();
			ComplexContent frameworkInput = frameworkService.getServiceInterface().getInputDefinition().newInstance();
			frameworkInput.set("targets", scheduled.getConfiguration().getTargets() == null || scheduled.getConfiguration().getTargets().isEmpty() ? new ArrayList<String>(Arrays.asList(new String [] { "$any" })) : scheduled.getConfiguration().getTargets());
			frameworkInput.set("schedulerId", scheduled.getId());
			frameworkInput.set("serviceId", scheduled.getConfiguration().getService().getId());
			frameworkInput.set("serviceInput", content);
			frameworkInput.set("timestamp", timestamp);
			frameworkInput.set("maxAmountOfRuns", scheduled.getConfiguration().getAmountOfTimes());
			ServiceRuntime runtime = new ServiceRuntime(frameworkService, scheduled.getRepository().newExecutionContext(SystemPrincipal.ROOT));
			ServiceUtils.setServiceContext(runtime, scheduled.getConfig().getService().getId());
			runtime.run(frameworkInput);
		}
		catch (Exception e) {
			logger.error("Could not run scheduler: " + scheduled.getId(), e);
			try {
				Notification notification = new Notification();
				notification.setContext(Arrays.asList(scheduled.getConfig().getService().getId(), scheduled.getId()));
				notification.setType("nabu.misc.scheduler.run");
				notification.setCode("SCHEDULER-0");
				notification.setMessage("Scheduled run at '" + timestamp + "' failed");
				notification.setDescription(Notification.format(e));
				notification.setSeverity(Severity.ERROR);
				scheduled.getRepository().getEventDispatcher().fire(notification, this);
			}
			catch (Exception f) {
				logger.error("Could not send notification", f);
			}
		}
	}

}
