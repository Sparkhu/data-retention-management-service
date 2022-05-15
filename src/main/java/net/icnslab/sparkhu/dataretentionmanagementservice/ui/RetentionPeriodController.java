package net.icnslab.sparkhu.dataretentionmanagementservice.ui;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class RetentionPeriodController {
	
	
	@GetMapping("/retention/period")
	public ResponseEntity<?> getPeriod(){
		Map<String, String> period = new HashMap<>();
		period.put("startDate", "2022-01-01");
		period.put("period", "6 months");
		
		return ResponseEntity.ok().body(period);
	}
}
