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

package be.nabu.eai.module.scheduler.provider.api;

import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.validation.constraints.NotNull;

public interface SchedulerProvider {
	public void run(@NotNull @WebParam(name = "targets") List<String> targets, @NotNull @WebParam(name = "schedulerId") String schedulerId, @NotNull @WebParam(name = "serviceId") String serviceId, @WebParam(name = "serviceInput") Object properties, @NotNull @WebParam(name = "timestamp") Date timestamp, @WebParam(name = "maxAmountOfRuns") Long maxAmountOfRuns);
}
