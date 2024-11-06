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

package be.nabu.eai.module.scheduler.base;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nabu.misc.cluster.Services;
import be.nabu.eai.module.cluster.ClusterArtifact;
import be.nabu.eai.module.cluster.api.MasterSwitcher;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.eai.repository.util.SystemPrincipal;
import be.nabu.libs.artifacts.api.StartableArtifact;
import be.nabu.libs.artifacts.api.StoppableArtifact;
import be.nabu.libs.resources.api.ResourceContainer;

public abstract class BaseSchedulerArtifact<T extends BaseSchedulerConfiguration> extends JAXBArtifact<T> implements StartableArtifact, StoppableArtifact {
	
	private boolean started;
	private ClusterArtifact ownCluster;
	private MasterSwitcher switcher;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public BaseSchedulerArtifact(String id, ResourceContainer<?> directory, String fileName, Class<T> configurationClazz, Repository repository) {
		super(id, directory, repository, fileName, configurationClazz);
		switcher = new MasterSwitcher() {
			@Override
			public void switchMaster(String master, boolean amMaster) {
				// we don't want to do anything special during elections
				// if we were the master before, we stay the master of scheduling until a new master is appointed
				// the appointment is synchronous so there should be no (?) conflict, the new master will start up within a short timespan of this one going down
				if (master != null) {
					if (amMaster) {
						try {
							logger.info("Became master, starting scheduler: " + getId());
							start();
						}
						catch (IOException e) {
							logger.error("Became master but could not enable: " + getId(), e);
						}
					}
					else if (getConfig().getTargets() == null || !getConfig().getTargets().contains("$all")) {
						try {
							logger.info("Stopped being master, stopping scheduler: " + getId());
							stop();
						}
						catch (IOException e) {
							logger.error("Stopped being master but could not disable: " + getId(), e);
						}
					}
				}
			}
		};
	}

	abstract public Date getNextRun(Date fromTimestamp);
	
	public Date getInitialRun(Date now) {
		try {
			return getConfiguration().isStartImmediately() ? now : getNextRun(now);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	@Override
	public void save(ResourceContainer<?> directory) throws IOException {
		super.save(directory);
		// refresh the provider to pick up the scheduler if necessary
		if (getConfig().getProvider() != null) {
			getConfig().getProvider().refresh();
		}
	}

	@Override
	public void stop() throws IOException {
		started = false;
	}

	@Override
	public void start() throws IOException {
		if (getConfiguration().isEnabled()) {
			// if it should run on all servers, it should always start
			if (getConfig().getTargets() != null && getConfig().getTargets().contains("$all")) {
				started = true;
			}
			// otherwise we make it dependent on the cluster state
			else {
				if (ownCluster == null) {
					ownCluster = Services.getOwnCluster(getRepository().newExecutionContext(SystemPrincipal.ROOT));
				}
				if (ownCluster != null) {
					ownCluster.addSwitcher(switcher);
				}
				if (ownCluster == null || ownCluster.isMaster()) {
					started = true;
				}
			}
			if (started && getConfig().getProvider() != null) {
				// need to actually refresh all the schedulers when we start this one because if it is the consequence of a save and reload, we may have a new object
				// so the references stored by the provider are outdated
				getConfig().getProvider().refresh();
			}
		}
	}

	@Override
	public boolean isStarted() {
		return started;
	}

}
