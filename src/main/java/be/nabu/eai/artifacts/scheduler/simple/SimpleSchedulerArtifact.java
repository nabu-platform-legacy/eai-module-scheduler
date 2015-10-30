package be.nabu.eai.artifacts.scheduler.simple;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.nabu.eai.artifacts.scheduler.base.BaseSchedulerArtifact;
import be.nabu.libs.resources.api.ResourceContainer;

public class SimpleSchedulerArtifact extends BaseSchedulerArtifact<SimpleSchedulerConfiguration> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public SimpleSchedulerArtifact(String id, ResourceContainer<?> directory) {
		super(id, directory, "scheduler-simple.xml", SimpleSchedulerConfiguration.class);
	}

	@Override
	public Date getNextRun(Date fromTimestamp) {
		try {
			return new Date(fromTimestamp.getTime() + getConfiguration().getRepeatInterval());
		}
		catch (IOException e) {
			logger.error("Can not calculate next run", e);
			return null;
		}
	}

}