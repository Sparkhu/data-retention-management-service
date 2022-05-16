package net.icnslab.sparkhu.dataretentionmanagementservice.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class GetRetentionPeriodServiceImpl implements GetRetentionPeriodService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	public PeriodDto getRetentionPeriod() {
		ValueOperations<String, String> retentionPolicyRepository = redisTemplate.opsForValue();
		String startDate = retentionPolicyRepository.get("retention/startDate");
		String period = retentionPolicyRepository.get("retention/period");
		return new PeriodDto(startDate, period);
	}
	
	public PeriodDto changeRetentionPeriod(PeriodDto new_period) {
		ValueOperations<String, String> retentionPolicyRepository = redisTemplate.opsForValue();
		retentionPolicyRepository.set("retention/startDate", new_period.getStartDate());
		retentionPolicyRepository.set("retention/period", new_period.getPeriod());
		return new_period;
	}
}
