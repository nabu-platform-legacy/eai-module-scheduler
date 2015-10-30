package be.nabu.eai.artifacts.scheduler.complex;

import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

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
	public List<Integer> getYear() {
		return year;
	}
	public void setYear(List<Integer> year) {
		this.year = year;
	}
	public List<Integer> getDayOfMonth() {
		return dayOfMonth;
	}
	public void setDayOfMonth(List<Integer> dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	public List<Integer> getHour() {
		return hour;
	}
	public void setHour(List<Integer> hour) {
		this.hour = hour;
	}
	public List<Integer> getMinute() {
		return minute;
	}
	public void setMinute(List<Integer> minute) {
		this.minute = minute;
	}
	public List<Integer> getSecond() {
		return second;
	}
	public void setSecond(List<Integer> second) {
		this.second = second;
	}
}
