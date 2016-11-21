package be.nabu.eai.module.scheduler.complex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.nabu.eai.module.scheduler.base.BaseSchedulerArtifact;
import be.nabu.eai.module.scheduler.complex.ComplexSchedulerConfiguration.DayOfWeek;
import be.nabu.eai.module.scheduler.complex.ComplexSchedulerConfiguration.MonthOfYear;
import be.nabu.eai.repository.api.Repository;
import be.nabu.libs.resources.api.ResourceContainer;

/**
 * As a general note: calendar.set() is messed up. According to the spec recalculations are not done on set() but they _are_ done on add().
 * Note that you can supposedly trigger a calculation with getTime(). But consider this:
 * 
 * date = 2015-01-12
 * for (i in weeksOfMonth)
 * 		calendar.set(WEEK_OF_MONTH, i)
 * This ended up looping first in 2014 (!!!) then suddenly shifting to 2017. Even though the "reset date" at the top correctly printed out the reset date
 * I switch to this logic:
 * 
 * calendar.set(WEEK_OF_MONTH, 1)
 * for (i in weeksOfMonth)
 * 		calendar.add(WEEK_OF_MONTH, 1)
 * 
 * And this works like a charm
 */
public class ComplexSchedulerArtifact extends BaseSchedulerArtifact<ComplexSchedulerConfiguration> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public ComplexSchedulerArtifact(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, "scheduler-complex.xml", ComplexSchedulerConfiguration.class, repository);
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

	public enum Granularity {
		YEAR, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND
	}
	
	public static Granularity getGranularity(ComplexSchedulerConfiguration configuration) {
		if (!configuration.getSecond().isEmpty()) {
			return Granularity.SECOND;
		}
		else if (!configuration.getMinute().isEmpty()) {
			return Granularity.MINUTE;
		}
		else if (!configuration.getHour().isEmpty()) {
			return Granularity.HOUR;
		}
		else if (!configuration.getDayOfMonth().isEmpty() || !configuration.getDayOfWeek().isEmpty()) {
			return Granularity.DAY;
		}
		else if (!configuration.getWeekOfMonth().isEmpty()) {
			return Granularity.WEEK;
		}
		else if (!configuration.getMonthOfYear().isEmpty()) {
			return Granularity.MONTH;
		}
		return Granularity.YEAR;
	}
	
	private static Date findTimeMatch(ComplexSchedulerConfiguration configuration, Granularity granularity, Date fromTimestamp, Calendar calendar) {
		for (int hour = 0; hour < 24; hour++) {
			if (configuration.getHour().isEmpty() || configuration.getHour().contains(hour)) {
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				if (granularity == Granularity.HOUR) {
					if (calendar.getTime().after(fromTimestamp)) {
						return calendar.getTime();
					}
				}
				else {
					for (int minute = 0; minute < 60; minute++) {
						calendar.set(Calendar.MINUTE, minute);
						if (configuration.getMinute().isEmpty() || configuration.getMinute().contains(minute)) {
							if (granularity == Granularity.MINUTE) {
								if (calendar.getTime().after(fromTimestamp)) {
									return calendar.getTime();
								}
							}
							else {
								for (int second = 0; second < 60; second++) {
									calendar.set(Calendar.SECOND, second);
									if (configuration.getMinute().isEmpty() || configuration.getMinute().contains(second)) {
										if (granularity == Granularity.SECOND) {
											if (calendar.getTime().after(fromTimestamp)) {
												return calendar.getTime();
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public static Date calculateNextRun(ComplexSchedulerConfiguration configuration, Date fromTimestamp) throws IOException {
		Calendar calendar = Calendar.getInstance();
		
		Granularity granularity = getGranularity(configuration);
		List<Integer> years = new ArrayList<Integer>();
		if (!configuration.getYear().isEmpty()) {
			years.addAll(configuration.getYear());
			Collections.sort(years);
		}
		else {
			// it can only be in this year or the next
			years.addAll(Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1));
		}
		
		// the granularity defines whether we have a full listing of an empty field
		// for instance if you only pass in an hour, everything "above" hour (day, month, year) is "every" whereas everything "below" is the first: minute, second
		// this would mean that it would effectively run once every hour of every day of every month of every year
		List<MonthOfYear> monthsOfYear = new ArrayList<MonthOfYear>();
		if (!configuration.getMonthOfYear().isEmpty()) {
			monthsOfYear.addAll(configuration.getMonthOfYear());
			Collections.sort(monthsOfYear);
		}
		else {
			if (granularity.ordinal() > Granularity.MONTH.ordinal()) {
				monthsOfYear.addAll(Arrays.asList(MonthOfYear.values()));
			}
			else {
				monthsOfYear.add(MonthOfYear.JANUARY);
			}
		}
		
		// we might not need this but precalculate it in case we do
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		if (!configuration.getDayOfWeek().isEmpty()) {
			daysOfWeek.addAll(configuration.getDayOfWeek());
			Collections.sort(monthsOfYear);
		}
		else {
			if (granularity.ordinal() > Granularity.DAY.ordinal()) {
				daysOfWeek.addAll(Arrays.asList(DayOfWeek.values()));
			}
			else {
				daysOfWeek.add(DayOfWeek.MONDAY);
			}
		}
		
		for (int year : years) {
			// reset
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, 0);
			if (!configuration.getDayOfWeek().isEmpty() || !configuration.getWeekOfMonth().isEmpty()) {
				calendar.set(Calendar.WEEK_OF_MONTH, 1);
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			}
			else {
				calendar.set(Calendar.DAY_OF_MONTH, 1);
			}
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			// IMPORTANT: from the javadoc https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html:
			// "Any field values set in a Calendar will not be interpreted until it needs to calculate its time value (milliseconds from the Epoch) or values of the calendar fields. Calling the get, getTimeInMillis, getTime, add and roll involves such calculation." 
			// the calculation was way off before I added a System.out.println() to check the actual time set in the calendar
			// the act of calling getTime() triggers an update in the calendar which actually calculates the values
			// this is _required_ to get the correct results here, otherwise it is waaay off base
			// talk about hidden functionality
			calendar.getTime();
			if (Granularity.YEAR == granularity) {
				if (calendar.getTime().after(fromTimestamp)) {
					return calendar.getTime();
				}
			}
			else {
				for (MonthOfYear monthOfYear : monthsOfYear) {
					calendar.set(Calendar.MONTH, monthOfYear.ordinal());
					if (granularity == Granularity.MONTH) {
						if (calendar.getTime().after(fromTimestamp)) {
							return calendar.getTime();
						}
					}
					else {
						// working with day of week and/or week of month
						if (!configuration.getDayOfWeek().isEmpty() || !configuration.getWeekOfMonth().isEmpty()) {
							for (int i = 1; i <= calendar.getActualMaximum(Calendar.WEEK_OF_MONTH); i++) {
								calendar.set(Calendar.WEEK_OF_MONTH, i);
								if (configuration.getWeekOfMonth().isEmpty() || configuration.getWeekOfMonth().contains(i)) {
									calendar.getTime();
									if (granularity == Granularity.WEEK) {
										if (calendar.getTime().after(fromTimestamp)) {
											return calendar.getTime();
										}
									}	
									else {
										for (DayOfWeek dayOfWeek : daysOfWeek) {
											calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek.getCalendarField());
											calendar.getTime();
											if (granularity == Granularity.DAY) {
												if (calendar.getTime().after(fromTimestamp)) {
													return calendar.getTime();
												}
											}
											else {
												Date findTimeMatch = findTimeMatch(configuration, granularity, fromTimestamp, calendar);
												if (findTimeMatch != null) {
													return findTimeMatch;
												}
											}
										}
									}
								}
							}
						}
						// working with regular day of month
						else {
							for (int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
								calendar.set(Calendar.DAY_OF_MONTH, i);
								if (configuration.getDayOfMonth().isEmpty() || configuration.getDayOfMonth().contains(i)) {
									if (granularity == Granularity.DAY) {
										if (calendar.getTime().after(fromTimestamp)) {
											return calendar.getTime();
										}
									}
									else {
										Date findTimeMatch = findTimeMatch(configuration, granularity, fromTimestamp, calendar);
										if (findTimeMatch != null) {
											return findTimeMatch;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

}
