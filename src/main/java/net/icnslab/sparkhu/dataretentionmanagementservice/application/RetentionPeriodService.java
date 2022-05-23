package net.icnslab.sparkhu.dataretentionmanagementservice.application;

import java.util.Optional;

public interface RetentionPeriodService {

	public Optional<PeriodDto> getRetentionPeriod(String _condition);
	public MessageDto changeRetentionPeriod(PeriodDto new_period);
}
