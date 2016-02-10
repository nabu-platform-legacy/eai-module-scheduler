package be.nabu.eai.module.scheduler.complex;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class TestComplexScheduler {
	public static void main(String...args) throws IOException, ParseException {
		ComplexSchedulerConfiguration configuration = new ComplexSchedulerConfiguration();
//		configuration.setDayOfMonth(Arrays.asList(1, 3));
//		configuration.setHour(Arrays.asList(3, 6));
//		configuration.setYear(Arrays.asList(2015, 2016));
		configuration.setWeekOfMonth(Arrays.asList(1));
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date start = formatter.parse("2015-01-12 13:03");
		for (int i = 0; i < 5; i++) {
			start = ComplexSchedulerArtifact.calculateNextRun(configuration, start);
			System.out.println(start);
		}
	}
}
