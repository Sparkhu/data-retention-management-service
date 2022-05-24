package net.icnslab.sparkhu.dataretentionmanagementservice.domain;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.icnslab.sparkhu.dataretentionmanagementservice.application.RetentionPeriodService;
import net.icnslab.sparkhu.dataretentionmanagementservice.application.PeriodDto;

@Component
@EnableAsync
public class DataArchiver {
	
	@Autowired
	private DataStorage storage;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Value("${storage.sources.dir}")
	private String sourceDir;
	
	@Value("${storage.archive.dir}")
	private String archiveDir;
	
	
	@Scheduled(cron = "0 0 0 * * *")
	@Async
	public void dailyArchive() {
		ValueOperations<String, String> retentionPolicyRepository = redisTemplate.opsForValue();
		String _period = retentionPolicyRepository.get("retention/backup/period");
		String _startDate = retentionPolicyRepository.get("retention/backup/startDate");
		if(_period != null && !_period.equals("no policy")) {
			String glob = DataStorage.join(sourceDir, "/*/year=[0-9][0-9][0-9][0-9]/month=[0-9][0-9]/day=[0-9][0-9]");
			ArrayList<String> days = storage.getGlobPaths(glob);
			List<String> toBackup = days.stream().filter(day -> filterToBackup.apply(day, _period, _startDate)).collect(Collectors.toList());
			for(String backup: toBackup) {
				storage.archive(backup.substring(backup.indexOf(sourceDir)), archiveDir, true);
			}
		}
		disposeExpiredArchives();
	}
	
	@Async
	public void disposeExpiredArchives() {
		ValueOperations<String, String> retentionPolicyRepository = redisTemplate.opsForValue();
		String period = retentionPolicyRepository.get("retention/disposal/period");
		if(period != null && !period.equals("no policy")) {
			String glob = DataStorage.join(archiveDir, "/*-[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9].har");
			ArrayList<String> days = storage.getGlobPaths(glob);
			List<String> toDisposal = days.stream().filter(day -> filterToDisposal.apply(day, period)).collect(Collectors.toList());
			for(String disposal: toDisposal) {
				storage.remove(disposal, true);
			}
		}
	}
	private void monthlyArchive() {

	}
	
	private void yearlyArchive() {
		
	}
	
	private TriFunction<String, String, String, Boolean> filterToBackup = (day, period, startDate) -> {
		try {
			Date date = DateUtil.parseFromDir(day.substring(day.lastIndexOf("year")));
			// 날짜 지났거나 시작일 지난.
			Date expiry = new Date();
			String unit = RetentionPeriodUtil.getUnit(period);
			if(unit.equals("months")) {
				expiry = DateUtils.addMonths(expiry, -RetentionPeriodUtil.getNum(period));
			} else {
				expiry = DateUtils.addYears(expiry, -RetentionPeriodUtil.getNum(period));
			}
			if(startDate != null) {
				Date _startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
				if(expiry.before(_startDate)) {
					expiry = _startDate;
				}
			}
			return date.before(expiry);
		} catch(Exception e) {
			return new Boolean(false);
		}
	};
	
	private BiFunction<String, String, Boolean>  filterToDisposal = (day, period) -> {
		try {
			Date date = DateUtil.parseFromHar(day.substring(day.lastIndexOf(archiveDir) + archiveDir.length()));
			Date expiry = new Date();
			String unit = RetentionPeriodUtil.getUnit(period);
			if(unit.equals("months")) {
				expiry = DateUtils.addMonths(expiry, -RetentionPeriodUtil.getNum(period));
			} else {
				expiry = DateUtils.addYears(expiry, -RetentionPeriodUtil.getNum(period));
			}
			return date.before(expiry);
		} catch (Exception e) {
			return new Boolean(false);
		}
	};
}
