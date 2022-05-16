package net.icnslab.sparkhu.dataretentionmanagementservice.domain;

import java.time.LocalDate;
public class DateUtil {
	
	public static boolean isTodayFirstDayOfTheMonth() {
		LocalDate today = LocalDate.now();
		return today.getDayOfMonth() == 1;
	}
	
	public static boolean isTodayFirstDayofTheYear() {
		LocalDate today = LocalDate.now();
		return (today.getDayOfYear() == 1) && (today.getDayOfMonth() == 1);
	}
}
