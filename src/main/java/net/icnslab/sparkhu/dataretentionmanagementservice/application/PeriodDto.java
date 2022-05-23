package net.icnslab.sparkhu.dataretentionmanagementservice.application;


public class PeriodDto {
	private String startDate;
	private String period;
	private String condition;
	
	public PeriodDto(String startDate, String period, String condition) {
		this.setStartDate(startDate);
		this.setPeriod(period);
		this.setCondition(condition);
	}
	
	public String getStartDate() {
		return startDate;
	}
	
	public String getPeriod() {
		return period;
	}
	
	public String getCondition() {
		return condition;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public void setPeriod(String period) {
		this.period = period;
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}
}
