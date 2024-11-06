/*
* Copyright (C) 2015 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.eai.module.scheduler.simple;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.nabu.eai.module.scheduler.base.BaseSchedulerArtifact;
import be.nabu.eai.repository.api.Repository;
import be.nabu.libs.resources.api.ResourceContainer;

public class SimpleSchedulerArtifact extends BaseSchedulerArtifact<SimpleSchedulerConfiguration> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public SimpleSchedulerArtifact(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, "scheduler-simple.xml", SimpleSchedulerConfiguration.class, repository);
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
