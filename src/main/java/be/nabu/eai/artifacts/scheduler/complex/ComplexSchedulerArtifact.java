package be.nabu.eai.artifacts.scheduler.complex;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.nabu.eai.artifacts.scheduler.base.BaseSchedulerArtifact;
import be.nabu.eai.artifacts.scheduler.complex.ComplexSchedulerConfiguration.DayOfWeek;
import be.nabu.eai.artifacts.scheduler.complex.ComplexSchedulerConfiguration.MonthOfYear;
import be.nabu.libs.resources.api.ResourceContainer;

public class ComplexSchedulerArtifact extends BaseSchedulerArtifact<ComplexSchedulerConfiguration> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public ComplexSchedulerArtifact(String id, ResourceContainer<?> directory) {
		super(id, directory, "scheduler-complex.xml", ComplexSchedulerConfiguration.class);
	}

	@Override
	public Date getNextRun(Date fromTimestamp) {
		try {
			Date calculateNextRun = calculateNextRun(getConfiguration(), fromTimestamp);
			if (calculateNextRun == null) {
				getConfiguration().setEnabled(false);
				return null;
			}
			return calculateNextRun;
		}
		catch (IOException e) {
			logger.error("Can not calculate next run", e);
			return null;
		}
	}

	public static Date calculateNextRun(ComplexSchedulerConfiguration configuration, Date fromTimestamp) throws IOException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromTimestamp);
		// ----------------------------------- MATCH YEAR
		// if we have configured a year, first go to first year after the one we just hit
		boolean matchedAny = false;
		boolean matchFound = false;
		if (configuration.getYear() != null && !configuration.getYear().isEmpty()) {
			Collections.sort(configuration.getYear());
			for (Integer year : configuration.getYear()) {
				if (year > calendar.get(Calendar.YEAR)) {
					calendar.set(Calendar.YEAR, year);
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				configuration.setEnabled(false);
				return null;
			}
			else {
				matchFound = false;
				matchedAny = true;
			}
		}
		
		// ----------------------------------- MATCH MONTH
		// if we have configured a year, first go to first year after the one we just hit
		if (configuration.getMonthOfYear() != null && !configuration.getMonthOfYear().isEmpty()) {
			Collections.sort(configuration.getMonthOfYear());
			// calendar month is 0-based
			MonthOfYear currentMonth = MonthOfYear.values()[calendar.get(Calendar.MONTH)];
			for (MonthOfYear month : configuration.getMonthOfYear()) {
				if (month.ordinal() > currentMonth.ordinal()) {
					calendar.set(Calendar.MONTH, month.ordinal());
					matchFound = false;
					break;
				}
			}
			// if no next month this year was found, take the first month of next year
			if (!matchFound) {
				calendar.add(Calendar.YEAR, 1);
				calendar.set(Calendar.MONTH, configuration.getMonthOfYear().get(0).ordinal());
			}
			else {
				matchFound = false;
				matchedAny = true;
			}
		}
		
		// ----------------------------------- MATCH DAY
		if (configuration.getDayOfMonth() != null && !configuration.getDayOfMonth().isEmpty()) {
			Collections.sort(configuration.getDayOfMonth());
			// day_of_month is 1-based
			for (int dayOfMonth : configuration.getDayOfMonth()) {
				if (dayOfMonth > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
					dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				}
				if (dayOfMonth > calendar.get(Calendar.DAY_OF_MONTH)) {
					calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				calendar.add(Calendar.MONTH, 1);
				calendar.set(Calendar.DAY_OF_MONTH, configuration.getDayOfMonth().get(0));
			}
			else {
				matchFound = false;
				matchedAny = true;
			}
		}
		else if (configuration.getDayOfWeek() != null && !configuration.getDayOfWeek().isEmpty()) {
			Collections.sort(configuration.getDayOfWeek());
			int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
			// sunday is 1, however we use a _normal_ week where monday is first...
			if (currentDay == Calendar.SUNDAY) {
				currentDay = Calendar.SATURDAY;
			}
			else {
				currentDay--;
			}
			// because days are 1 based, let's substract 1 for ordinal comparison
			currentDay--;
			for (DayOfWeek dayOfWeek : configuration.getDayOfWeek()) {
				if (dayOfWeek.ordinal() > currentDay) {
					calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek.getCalendarField());
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				calendar.add(Calendar.WEEK_OF_MONTH, 1);
				calendar.set(Calendar.DAY_OF_WEEK, configuration.getDayOfWeek().get(0).getCalendarField());
			}
			else {
				matchFound = false;
				matchedAny = true;
			}
		}
		
		// ----------------------------------- MATCH HOUR
		if (configuration.getHour() != null && !configuration.getHour().isEmpty()) {
			Collections.sort(configuration.getHour());
			// HOUR_OF_DAY is 0-based and has 24 values
			int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
			for (int hour : configuration.getHour()) {
				if (hour > currentHour) {
					calendar.set(Calendar.HOUR_OF_DAY, hour);
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				calendar.add(Calendar.DAY_OF_WEEK, 1);
				calendar.set(Calendar.HOUR_OF_DAY, configuration.getHour().get(0));
			}
			else {
				matchFound = false;
				matchedAny = true;
			}
		}
		
		// ----------------------------------- MATCH MINUTES
		if (configuration.getMinute() != null && !configuration.getMinute().isEmpty()) {
			Collections.sort(configuration.getMinute());
			// MINUTE is 0-based
			int currentMinute = calendar.get(Calendar.MINUTE);
			for (int minute : configuration.getMinute()) {
				if (minute > currentMinute) {
					calendar.set(Calendar.MINUTE, minute);
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				calendar.add(Calendar.HOUR_OF_DAY, 1);
				calendar.set(Calendar.MINUTE, configuration.getMinute().get(0));
			}
			else {
				matchFound = false;
				matchedAny = true;
			}
		}
		
		// ----------------------------------- MATCH SECONDS
		if (configuration.getSecond() != null && !configuration.getSecond().isEmpty()) {
			Collections.sort(configuration.getSecond());
			// SECOND is 0-based
			int currentSecond = calendar.get(Calendar.SECOND);
			for (int second : configuration.getSecond()) {
				if (second > currentSecond) {
					calendar.set(Calendar.SECOND, second);
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				calendar.add(Calendar.MINUTE, 1);
				calendar.set(Calendar.SECOND, configuration.getSecond().get(0));
			}
			else {
				matchFound = false;
				matchedAny = true;
			}
		}
		
		return matchedAny ? calendar.getTime() : null;
	}

}
