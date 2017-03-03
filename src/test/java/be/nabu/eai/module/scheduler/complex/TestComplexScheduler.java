package be.nabu.eai.module.scheduler.complex;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import be.nabu.eai.module.scheduler.complex.ComplexSchedulerConfiguration.DayOfWeek;

public class TestComplexScheduler {
	public static void main(String...args) throws IOException, ParseException {
		ComplexSchedulerConfiguration configuration = new ComplexSchedulerConfiguration();
//		configuration.setDayOfMonth(Arrays.asList(1, 3));
//		configuration.setHour(Arrays.asList(3, 6));
//		configuration.setYear(Arrays.asList(2015, 2016));
//		configuration.setWeekOfMonth(Arrays.asList(1));
//		configuration.setDayOfWeek(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
//		configuration.setMinute(Arrays.asList(0));
		configuration.setSecond(Arrays.asList(0, 10, 20, 30, 40, 50));
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date start = formatter.parse("2015-01-12 13:03");
//		Date start = new Date();
		Date start = formatter.parse("2017-03-03 16:10:50");
		System.out.println("Start: " + start);
		for (int i = 0; i < 5; i++) {
			start = ComplexSchedulerArtifact.calculateNextRun(configuration, start);
			System.out.println("NEXT RUN: " + start);
		}
	}
}
