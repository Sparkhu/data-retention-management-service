package net.icnslab.sparkhu.dataretentionmanagementservice.application;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.icnslab.sparkhu.dataretentionmanagementservice.domain.RetentionPeriodUtil;

@Service
public class RetentionPeriodServiceImpl implements RetentionPeriodService {

	@Value("${retention.default.active.period}")
	private String defaultActivePeriod;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	public Optional<PeriodDto> getRetentionPeriod(String _condition) {
		ValueOperations<String, String> retentionPolicyRepository = redisTemplate.opsForValue();
		
		// if period is null, then no retention (persist)
		if(_condition.equals("backup")) {
			String period = retentionPolicyRepository.get("retention/backup/period");
			if(period == null)
				period = "no policy";
			String startDate = retentionPolicyRepository.get("retention/backup/startDate");
			String condition = "backup";
			return Optional.ofNullable(new PeriodDto(startDate, period, condition));
		}
		else if(_condition.equals("disposal")){
			String period = retentionPolicyRepository.get("retention/disposal/period");
			if(period == null)
				period = "no policy";
			String startDate = retentionPolicyRepository.get("retention/disposal/startDate");
			String condition = "disposal";
			return Optional.ofNullable(new PeriodDto(startDate, period, condition));
		}
		else {
			return Optional.empty();
		}
	}
	
	public MessageDto changeRetentionPeriod(PeriodDto new_period) {
		ValueOperations<String, String> retentionPolicyRepository = redisTemplate.opsForValue();
		String period = new_period.getPeriod();
		String startDate = new_period.getStartDate();
		String condition = new_period.getCondition();
		if(condition.equals("backup")) {
			if(!RetentionPeriodUtil.verifyPeriod(period)) {
				return new MessageDto(400, "invalid period, expected [num years | num months | no policy]");
			}
			if(startDate != null && !RetentionPeriodUtil.verifyStartDate(startDate)) {
				return new MessageDto(400, "invalid startDate, expected yyyy-MM-dd, startDate must be a date in the past.");
			}
			if(period != null) {
				retentionPolicyRepository.set("retention/backup/period", period);
			}
			if(startDate != null) {
				retentionPolicyRepository.set("retention/backup/startDate", startDate);
			}
			
			return new MessageDto(204, "backup period changed successfully"); 
		}
		else if(condition.equals("disposal")) {
			if(period == null) {
				return new MessageDto(400, "invalid request, period fields must be given");
			}
			if(!RetentionPeriodUtil.verifyPeriod(period)) {
				return new MessageDto(400, "invalid period, expected [num years | num months | no policy]"); 
			}
			retentionPolicyRepository.set("retention/disposal/period", period);
			return new MessageDto(204, "disposal period changed successfully");
		}
		return new MessageDto(400, "invalid condition, expected condition: [backup | disposal]");
	}
}
