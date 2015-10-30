package be.nabu.eai.artifacts.scheduler.complex;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import be.nabu.eai.api.Enumerator;
import be.nabu.eai.api.ValueEnumerator;
import be.nabu.eai.artifacts.scheduler.base.BaseSchedulerConfiguration;

@XmlRootElement(name = "complexScheduler")
public class ComplexSchedulerConfiguration extends BaseSchedulerConfiguration {
	
	private List<Integer> year;
	private List<MonthOfYear> monthOfYear;
	private List<Integer> dayOfMonth;
	private List<DayOfWeek> dayOfWeek;
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
	public List<DayOfWeek> getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(List<DayOfWeek> dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public List<MonthOfYear> getMonthOfYear() {
		return monthOfYear;
	}
	public void setMonthOfYear(List<MonthOfYear> monthOfYear) {
		this.monthOfYear = monthOfYear;
	}
	@ValueEnumerator(enumerator = YearEnumerator.class)
	public List<Integer> getYear() {
		return year;
	}
	public void setYear(List<Integer> year) {
		this.year = year;
	}
	@ValueEnumerator(enumerator = DayEnumerator.class)
	public List<Integer> getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(List<Integer> dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	@ValueEnumerator(enumerator = HourEnumerator.class)
	public List<Integer> getHour() {
		return hour;
	}
	public void setHour(List<Integer> hour) {
		this.hour = hour;
	}
	@ValueEnumerator(enumerator = MinuteEnumerator.class)
	public List<Integer> getMinute() {
		return minute;
	}
	public void setMinute(List<Integer> minute) {
		this.minute = minute;
	}
	@ValueEnumerator(enumerator = MinuteEnumerator.class)
	public List<Integer> getSecond() {
		return second;
	}
	public void setSecond(List<Integer> second) {
		this.second = second;
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
			for (int i = calendar.get(Calendar.YEAR); i <= 50; i++) {
				values.add(i);
			}
			return values;
		}
	}
	
}
