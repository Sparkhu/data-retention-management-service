package net.icnslab.sparkhu.dataretentionmanagementservice.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RetentionPeriodUtil {
	public static boolean verifyPeriod(String period) {
		if(period == null)
			return false;
		if(period.equals("no policy"))
			return true;
		String[] tokens = period.split(" ");
		if(tokens.length != 2) {
			return false;
		}
		
		int num;
		try {
			num = Integer.parseInt(tokens[0]);
			if(num <= 0) {
				return false;
			}
		} catch(NumberFormatException e) {
			return false;
		}
		if(tokens[1].equals("months") || tokens[1].equals("years")) {
			return true;
		}
		return false;
	}
	
	public static int getNum(String period) {
		return Integer.parseInt(period.split(" ")[0]);
	}
	
	public static String getUnit(String period) {
		return period.split(" ")[1];
	}
	
	public static boolean verifyStartDate(String startDate) {
		if (startDate.trim().equals(""))
		{
		    return false;
		}
		SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM-dd");
		Date start;
		try {
			start = sdfrmt.parse(startDate);
		} catch(ParseException e) {
			return false;
		}
		return start.before(new Date());
	}
}
