package net.icnslab.sparkhu.dataretentionmanagementservice.application;

import org.springframework.stereotype.Service;

@Service
public class GetRetentionPeriodServiceImpl implements GetRetentionPeriodService {

	public PeriodDto getRetentionPeriod() {
		return new PeriodDto("2022-01-01", "6 months");
	}
}
