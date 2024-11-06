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

package be.nabu.eai.module.scheduler.provider;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.api.EnvironmentSpecific;
import be.nabu.eai.api.InterfaceFilter;
import be.nabu.eai.repository.jaxb.ArtifactXMLAdapter;
import be.nabu.libs.services.api.DefinedService;

@XmlRootElement(name = "schedulerProvider")
public class SchedulerProviderConfiguration {
	
	private DefinedService runService;
	private Integer poolSize;
	private boolean enabled;

	@NotNull
	@InterfaceFilter(implement = "be.nabu.eai.module.scheduler.provider.api.SchedulerProvider.run")
	@XmlJavaTypeAdapter(value = ArtifactXMLAdapter.class)
	public DefinedService getRunService() {
		return runService;
	}
	public void setRunService(DefinedService runService) {
		this.runService = runService;
	}
	
	@EnvironmentSpecific
	public Integer getPoolSize() {
		return poolSize;
	}
	public void setPoolSize(Integer poolSize) {
		this.poolSize = poolSize;
	}
	
	@EnvironmentSpecific
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
