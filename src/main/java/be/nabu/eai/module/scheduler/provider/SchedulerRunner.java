package be.nabu.eai.module.scheduler.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.nabu.eai.module.scheduler.base.BaseSchedulerArtifact;
import be.nabu.eai.repository.util.SystemPrincipal;
import be.nabu.libs.services.ServiceRuntime;
import be.nabu.libs.types.api.ComplexContent;

public class SchedulerRunner implements Runnable {

	private BaseSchedulerArtifact<?> scheduled;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public SchedulerRunner(BaseSchedulerArtifact<?> scheduled) {
		this.scheduled = scheduled;
	}

	@Override
	public void run() {
		try {
			ComplexContent content = scheduled.getConfiguration().getService().getServiceInterface().getInputDefinition().newInstance();
			for (String key : scheduled.getConfiguration().getProperties().keySet()) {
				content.set(key, scheduled.getConfiguration().getProperties().get(key));
			}
			ServiceRuntime runtime = new ServiceRuntime(scheduled.getConfiguration().getService(), scheduled.getRepository().newExecutionContext(SystemPrincipal.ROOT));
			runtime.run(content);
		}
		catch (Exception e) {
			logger.error("Could not run scheduler: " + scheduled.getId(), e);
		}
	}

}
