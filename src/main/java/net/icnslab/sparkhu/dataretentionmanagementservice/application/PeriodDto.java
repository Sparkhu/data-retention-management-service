package net.icnslab.sparkhu.dataretentionmanagementservice.application;


public class PeriodDto {
	private String startDate;
	private String period;
	
	public PeriodDto(String startDate, String period) {
		this.setStartDate(startDate);
		this.setPeriod(period);
	}
	
	public String getStartDate() {
		return startDate;
	}
	
	public String getPeriod() {
		return period;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public void setPeriod(String period) {
		this.period = period;
	}
}
