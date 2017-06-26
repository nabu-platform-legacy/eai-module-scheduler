package be.nabu.eai.module.scheduler.complex;

import java.util.List;

import be.nabu.eai.module.scheduler.simple.SimpleSchedulerDocumentor;
import be.nabu.eai.repository.DocumentationManager.DocumentedImpl;
import be.nabu.eai.repository.api.Documented;
import be.nabu.eai.repository.api.Documentor;
import be.nabu.eai.repository.api.Repository;

public class ComplexSchedulerDocumentor implements Documentor<ComplexSchedulerArtifact> {

	@Override
	public Class<ComplexSchedulerArtifact> getDocumentedClass() {
		return ComplexSchedulerArtifact.class;
	}
	
	private static String prettify(List<?> elements) {
		String string = elements.toString();
		// strip brackets
		return string.substring(1, string.length() - 1);
	}

	@Override
	public Documented getDocumentation(Repository repository, ComplexSchedulerArtifact scheduler) {
		StringBuilder builder = new StringBuilder();
		builder.append("<section class='scheduler simple-scheduler'><ul>");
		SimpleSchedulerDocumentor.document(scheduler, builder);
		if (scheduler.getConfig().getYear() != null && !scheduler.getConfig().getYear().isEmpty()) {
			builder.append("<li><span class='key'>Year</span><span class='value'>" + prettify(scheduler.getConfig().getYear()) + "</span></li>");
		}
		if (scheduler.getConfig().getMonthOfYear() != null && !scheduler.getConfig().getMonthOfYear().isEmpty()) {
			builder.append("<li><span class='key'>Month of Year</span><span class='value'>" + prettify(scheduler.getConfig().getMonthOfYear()) + "</span></li>");
		}
		if (scheduler.getConfig().getWeekOfMonth() != null && !scheduler.getConfig().getWeekOfMonth().isEmpty()) {
			builder.append("<li><span class='key'>Week of Month</span><span class='value'>" + prettify(scheduler.getConfig().getWeekOfMonth()) + "</span></li>");
		}
		if (scheduler.getConfig().getDayOfWeek() != null && !scheduler.getConfig().getDayOfWeek().isEmpty()) {
			builder.append("<li><span class='key'>Day of Week</span><span class='value'>" + prettify(scheduler.getConfig().getDayOfWeek()) + "</span></li>");
		}
		if (scheduler.getConfig().getDayOfMonth() != null && !scheduler.getConfig().getDayOfMonth().isEmpty()) {
			builder.append("<li><span class='key'>Day of Month</span><span class='value'>" + prettify(scheduler.getConfig().getDayOfMonth()) + "</span></li>");
		}
		if (scheduler.getConfig().getHour() != null && !scheduler.getConfig().getHour().isEmpty()) {
			builder.append("<li><span class='key'>Hour</span><span class='value'>" + prettify(scheduler.getConfig().getHour()) + "</span></li>");
		}
		if (scheduler.getConfig().getMinute() != null && !scheduler.getConfig().getMinute().isEmpty()) {
			builder.append("<li><span class='key'>Minute</span><span class='value'>" + prettify(scheduler.getConfig().getMinute()) + "</span></li>");
		}
		if (scheduler.getConfig().getSecond() != null && !scheduler.getConfig().getSecond().isEmpty()) {
			builder.append("<li><span class='key'>Second</span><span class='value'>" + prettify(scheduler.getConfig().getSecond()) + "</span></li>");
		}
		SimpleSchedulerDocumentor.documentProperties(scheduler, builder);
		builder.append("</ul></section>");
		DocumentedImpl documentation = new DocumentedImpl("text/html");
		documentation.setDescription(builder.toString());
		documentation.setTitle(scheduler.getId());
		return documentation;
	}

}
