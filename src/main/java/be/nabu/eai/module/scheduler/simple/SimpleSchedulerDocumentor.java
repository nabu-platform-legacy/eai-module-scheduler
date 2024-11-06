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

import java.util.Map;

import be.nabu.eai.module.scheduler.base.BaseSchedulerArtifact;
import be.nabu.eai.repository.DocumentationManager.DocumentedImpl;
import be.nabu.eai.repository.api.Documented;
import be.nabu.eai.repository.api.Documentor;
import be.nabu.eai.repository.api.Repository;

public class SimpleSchedulerDocumentor implements Documentor<SimpleSchedulerArtifact> {

	@Override
	public Class<SimpleSchedulerArtifact> getDocumentedClass() {
		return SimpleSchedulerArtifact.class;
	}

	@Override
	public Documented getDocumentation(Repository repository, SimpleSchedulerArtifact scheduler) {
		StringBuilder builder = new StringBuilder();
		builder.append("<section class='scheduler simple-scheduler'><ul>");
		document(scheduler, builder);
		builder.append("<li><span class='key'>Interval</span><span class='value'>" + scheduler.getConfig().getRepeatInterval() + "ms</span></li>");
		documentProperties(scheduler, builder);
		builder.append("</ul></section>");
		DocumentedImpl documentation = new DocumentedImpl("text/html");
		documentation.setDescription(builder.toString());
		documentation.setTitle(scheduler.getId());
		return documentation;
	}

	public static void document(BaseSchedulerArtifact<?> scheduler, StringBuilder builder) {
		builder.append("<li><span class='key'>Service</span><span class='value'>" + scheduler.getConfig().getService().getId() + "</span></li>");
		if (scheduler.getConfig().getAmountOfTimes() != null) {
			builder.append("<li><span class='key'>Amount of times</span><span class='value'>" + scheduler.getConfig().getAmountOfTimes() + "</span></li>");
		}
	}

	public static void documentProperties(BaseSchedulerArtifact<?> scheduler, StringBuilder builder) {
		Map<String, String> properties = scheduler.getConfig().getProperties();
		StringBuilder propertyBuilder = new StringBuilder();
		if (properties != null) {
			for (String key : properties.keySet()) {
				if (properties.get(key) != null) {
					propertyBuilder.append("<li><span class='key'>" + key + "</span><span class='value'>" + properties.get(key) + "</span></li>");
				}
			}
		}
		if (!propertyBuilder.toString().isEmpty()) {
			builder.append("<li><span class='key'>Properties</span></li><ul>");
			builder.append(propertyBuilder);
			builder.append("</ul>");
		}
	}
}
