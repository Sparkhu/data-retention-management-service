package net.icnslab.sparkhu.dataretentionmanagementservice.ui;

import org.springframework.web.bind.annotation.RestController;

import net.icnslab.sparkhu.dataretentionmanagementservice.application.RetentionPeriodService;
import net.icnslab.sparkhu.dataretentionmanagementservice.application.MessageDto;
import net.icnslab.sparkhu.dataretentionmanagementservice.application.PeriodDto;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class RetentionPeriodController {
	
	@Autowired
	private RetentionPeriodService retentionPeriodService;
	
	@GetMapping("/retention/period")
	public ResponseEntity<?> getPeriod(@RequestBody PeriodDto condition){
		Optional<PeriodDto> period = retentionPeriodService.getRetentionPeriod(condition.getCondition());
		if(period.isPresent()) {
			if(period.get().getCondition().equals("backup")) {
				return ResponseEntity.ok(period);
			}else {
				Map<String, Object> ret = new HashMap<String, Object>();
				ret.put("period", period.get().getPeriod());
				ret.put("condition", period.get().getCondition());
				return ResponseEntity.ok(ret);
			}
		}
		return new ResponseEntity<>(new AbstractMap.SimpleEntry<String, String>("message", "invalid request, expected condition: [backup | disposal]"), HttpStatus.BAD_REQUEST);
	}
	
	@PutMapping("/retention/period")
	public ResponseEntity<?> changePeriod(@RequestBody PeriodDto period){
		MessageDto message = retentionPeriodService.changeRetentionPeriod(period);
		return new ResponseEntity<>(new AbstractMap.SimpleEntry<String, String>("message", message.getMessage()), HttpStatus.resolve(message.getStatus()));
	}
	
}
