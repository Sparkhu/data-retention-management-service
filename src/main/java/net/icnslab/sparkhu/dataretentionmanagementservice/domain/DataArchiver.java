package net.icnslab.sparkhu.dataretentionmanagementservice.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
public class DataArchiver {
	
	@Autowired
	private DataStorage storage;
	
	@Value("${storage.sources.dir}")
	private String sourceDir;
	
	@Value("${storage.archive.dir}")
	private String archiveDir;
	
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
		List<String> sources = storage.list(sourceDir);
		for(String source: sources) {
			List<String> tables = storage.list(source);
			for(String table: tables) {
				String dailyDir = DataStorage.join(table, DateUtil.todayDir());
				String archiveDirTarget = DataStorage.join(archiveDir, table.substring(sourceDir.length(), table.length()));
				archiveDirTarget = DataStorage.join(archiveDirTarget, DateUtil.todayDir());
				if(storage.exists(dailyDir)) {
					storage.archive(dailyDir, archiveDirTarget);				
				}
			}
		}
	}
	
	private void monthlyArchive() {

	}
	
	private void yearlyArchive() {
		
	}
}
