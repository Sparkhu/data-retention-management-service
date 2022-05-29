package net.icnslab.sparkhu.dataretentionmanagementservice.ui;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.annotations.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
// import io.swagger.annotations.ApiResponses;
import net.icnslab.sparkhu.dataretentionmanagementservice.application.RetentionPeriodService;
import net.icnslab.sparkhu.dataretentionmanagementservice.application.MessageDto;
import net.icnslab.sparkhu.dataretentionmanagementservice.application.PeriodDto;
import net.icnslab.sparkhu.dataretentionmanagementservice.application.PeriodView;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
public class RetentionPeriodController {
	
	@Autowired
	private RetentionPeriodService retentionPeriodService;
	
	@ApiOperation(value = "보존 기간 조회")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK", 
				content= {@Content(mediaType="application/json", 
				schema = @Schema(implementation = PeriodDto.class), 
				examples= {@ExampleObject(value = "{\"condition\":\"backup\", \"period\": \"4 months\", \"startDate\": \"2022-01-01\"}")})}
		),
		@ApiResponse(responseCode = "400", description = "Bad Request")
	})
	@GetMapping("/retention/period")
	public ResponseEntity<?> getPeriod(@RequestParam("condition") String condition){
		Optional<PeriodDto> period = retentionPeriodService.getRetentionPeriod(condition);
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
		//return period.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.badRequest().build());
	}
	
	@ApiOperation(value = "보존 기간 변경")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request")
	})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PutMapping("/retention/period")
	public ResponseEntity<?> changePeriod(@RequestBody PeriodDto period){
		MessageDto message = retentionPeriodService.changeRetentionPeriod(period);
		return new ResponseEntity<>(new AbstractMap.SimpleEntry<String, String>("message", message.getMessage()), HttpStatus.resolve(message.getStatus()));
	}
	
}
