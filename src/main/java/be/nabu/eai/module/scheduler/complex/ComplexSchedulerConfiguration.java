package be.nabu.eai.module.scheduler.complex;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import be.nabu.eai.api.Enumerator;
import be.nabu.eai.api.EnvironmentSpecific;
import be.nabu.eai.api.ValueEnumerator;
import be.nabu.eai.module.scheduler.base.BaseSchedulerConfiguration;

@XmlRootElement(name = "complexScheduler")
@XmlType(propOrder = { "year", "monthOfYear", "dayOfMonth", "weekOfMonth", "dayOfWeek", "hour", "minute", "second" })
public class ComplexSchedulerConfiguration extends BaseSchedulerConfiguration {
	
	private List<Integer> year;
	private List<MonthOfYear> monthOfYear;
	private List<Integer> dayOfMonth;
	private List<DayOfWeek> dayOfWeek;
	private List<Integer> weekOfMonth;
	private List<Integer> hour;
	private List<Integer> minute;
	private List<Integer> second;
	
	public enum MonthOfYear {
		JANUARY,
		FEBRUARY,
		MARCH,
		APRIL,
		MAY,
		JUNE,
		JULY,
		AUGUST,
		SEPTEMBER,
		OCTOBER,
		NOVEMBER,
		DECEMBER
	}
	public enum DayOfWeek {
		MONDAY(Calendar.MONDAY),
		TUESDAY(Calendar.TUESDAY),
		WEDNESDAY(Calendar.WEDNESDAY),
		THURSDAY(Calendar.THURSDAY),
		FRIDAY(Calendar.FRIDAY),
		SATURDAY(Calendar.SATURDAY),
		SUNDAY(Calendar.SUNDAY);
		
		private int calendarField;

		private DayOfWeek(int calendarField) {
			this.calendarField = calendarField;
		}

		public int getCalendarField() {
			return calendarField;
		}
	}
	
	@EnvironmentSpecific
	public List<DayOfWeek> getDayOfWeek() {
		if (dayOfWeek == null) {
			dayOfWeek = new ArrayList<DayOfWeek>();
		}
		return dayOfWeek;
	}
	public void setDayOfWeek(List<DayOfWeek> dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	@EnvironmentSpecific
	public List<MonthOfYear> getMonthOfYear() {
		if (monthOfYear == null) {
			monthOfYear = new ArrayList<MonthOfYear>();
		}
		return monthOfYear;
	}
	public void setMonthOfYear(List<MonthOfYear> monthOfYear) {
		this.monthOfYear = monthOfYear;
	}
	
	@EnvironmentSpecific
	@ValueEnumerator(enumerator = YearEnumerator.class)
	public List<Integer> getYear() {
		if (year == null) {
			year = new ArrayList<Integer>();
		}
		return year;
	}
	public void setYear(List<Integer> year) {
		this.year = year;
	}
	
	@EnvironmentSpecific
	@ValueEnumerator(enumerator = DayEnumerator.class)
	public List<Integer> getDayOfMonth() {
		if (dayOfMonth == null) {
			dayOfMonth = new ArrayList<Integer>();
		}
		return dayOfMonth;
	}
	public void setDayOfMonth(List<Integer> dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	
	@EnvironmentSpecific
	@ValueEnumerator(enumerator = HourEnumerator.class)
	public List<Integer> getHour() {
		if (hour == null) {
			hour = new ArrayList<Integer>();
		}
		return hour;
	}
	public void setHour(List<Integer> hour) {
		this.hour = hour;
	}
	
	@EnvironmentSpecific
	@ValueEnumerator(enumerator = MinuteEnumerator.class)
	public List<Integer> getMinute() {
		if (minute == null) {
			minute = new ArrayList<Integer>();
		}
		return minute;
	}
	public void setMinute(List<Integer> minute) {
		this.minute = minute;
	}
	
	@EnvironmentSpecific
	@ValueEnumerator(enumerator = MinuteEnumerator.class)
	public List<Integer> getSecond() {
		if (second == null) {
			second = new ArrayList<Integer>();
		}
		return second;
	}
	public void setSecond(List<Integer> second) {
		this.second = second;
	}
	
	@EnvironmentSpecific
	@ValueEnumerator(enumerator = WeekOfMonthEnumerator.class)
	public List<Integer> getWeekOfMonth() {
		if (weekOfMonth == null) {
			weekOfMonth = new ArrayList<Integer>();
		}
		return weekOfMonth;
	}
	public void setWeekOfMonth(List<Integer> weekOfMonth) {
		this.weekOfMonth = weekOfMonth;
	}

	public static class WeekOfMonthEnumerator implements Enumerator {
		@Override
		public List<?> enumerate() {
			List<Integer> values = new ArrayList<Integer>();
			for (int i = 1; i <= 5; i++) {
				values.add(i);
			}
			return values;
		}
	}
	public static class DayEnumerator implements Enumerator {
		@Override
		public List<?> enumerate() {
			List<Integer> values = new ArrayList<Integer>();
			for (int i = 1; i <= 31; i++) {
				values.add(i);
			}
			return values;
		}
	}
	public static class HourEnumerator implements Enumerator {
		@Override
		public List<?> enumerate() {
			List<Integer> values = new ArrayList<Integer>();
			for (int i = 0; i <= 23; i++) {
				values.add(i);
			}
			return values;
		}
	}
	public static class MinuteEnumerator implements Enumerator {
		@Override
		public List<?> enumerate() {
			List<Integer> values = new ArrayList<Integer>();
			for (int i = 0; i <= 59; i++) {
				values.add(i);
			}
			return values;
		}
	}
	public static class YearEnumerator implements Enumerator {
		@Override
		public List<?> enumerate() {
			List<Integer> values = new ArrayList<Integer>();
			Calendar calendar = Calendar.getInstance();
			for (int i = calendar.get(Calendar.YEAR); i <= calendar.get(Calendar.YEAR) + 50; i++) {
				values.add(i);
			}
			return values;
		}
	}
	
}
