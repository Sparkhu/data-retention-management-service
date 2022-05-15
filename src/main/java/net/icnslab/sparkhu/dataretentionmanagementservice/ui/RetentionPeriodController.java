package net.icnslab.sparkhu.dataretentionmanagementservice.ui;

import org.springframework.web.bind.annotation.RestController;

import net.icnslab.sparkhu.dataretentionmanagementservice.application.GetRetentionPeriodService;
import net.icnslab.sparkhu.dataretentionmanagementservice.application.PeriodDto;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class RetentionPeriodController {
	
	@Autowired
	private GetRetentionPeriodService getRetentionPeriodService;
	
	@GetMapping("/retention/period")
	public ResponseEntity<?> getPeriod(){
		PeriodDto period = getRetentionPeriodService.getRetentionPeriod();
		
		return ResponseEntity.ok(period);
	}
}
