package net.icnslab.sparkhu.dataretentionmanagementservice.application;


public interface GetRetentionPeriodService {

	public PeriodDto getRetentionPeriod();
	public PeriodDto changeRetentionPeriod(PeriodDto new_period);
}
