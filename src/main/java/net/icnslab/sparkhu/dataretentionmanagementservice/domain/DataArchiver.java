package net.icnslab.sparkhu.dataretentionmanagementservice.domain;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
public class DataArchiver {
	
	@Scheduled(cron = "0 0 0 * * *")
	public void archiveJob() {
		dailyArchive();
		if(DateUtil.isTodayFirstDayOfTheMonth()) {
			monthlyArchive();
		}
		if(DateUtil.isTodayFirstDayofTheYear()) {
			yearlyArchive();
		}
	}
	
	private void dailyArchive() {
		
	}
	
	private void monthlyArchive() {
		
	}
	
	private void yearlyArchive() {
		
	}
}
