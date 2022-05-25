package net.icnslab.sparkhu.dataretentionmanagementservice.application;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "보존 기간 정보")
public class PeriodDto {
	
	@Schema(description = "정책 시작일. backup period 변경시 바로 정책에 영향을 받는 데이터들의 기준일이 됩니다. nullable이며 disposal condition에서는 활성화되지 않습니다.", example= "2022-01-01", nullable = true)
	//@ApiModelProperty(value = "정책 시작일 (", , dataType="String", example="2022-01-04",required=false)
	private String startDate;
	
	// @ApiModelProperty(value = "기간", dataType="String", example="6 months",required=true)
	@Schema(description = "기간. 'no police' 값은 백업/폐기 정책이 없음을 의미합니다.", nullable = false, required = true, example = "6 months", allowableValues = {"x years", "y months", "no police"})
	private String period;
	
	@JsonView(PeriodView.Retrieve.class)
	@Schema(description = "백업/폐기 선택", nullable = false, required = true, example = "backup",allowableValues = {"backup", "disposal"} )
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
