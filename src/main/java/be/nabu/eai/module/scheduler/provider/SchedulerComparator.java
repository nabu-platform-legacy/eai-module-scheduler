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
