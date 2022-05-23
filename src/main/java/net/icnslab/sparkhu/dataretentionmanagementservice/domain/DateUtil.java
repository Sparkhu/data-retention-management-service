package net.icnslab.sparkhu.dataretentionmanagementservice.domain;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
public class DateUtil {
	
	public static boolean isTodayFirstDayOfTheMonth() {
		LocalDate today = LocalDate.now();
		return today.getDayOfMonth() == 1;
	}
	
	public static boolean isTodayFirstDayofTheYear() {
		LocalDate today = LocalDate.now();
		return (today.getDayOfYear() == 1) && (today.getDayOfMonth() == 1);
	}
	
	public static String todayDir() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("/'year'=yyyy/'month'=MM/'day'=dd"));
	}
	
	public static boolean compare() {
		return true;
	}
	
	public static Date parseFromDir(String d) throws Exception{
		return new SimpleDateFormat("'year'=yyyy/'month'=MM/'day'=dd").parse(d);
	}
}
