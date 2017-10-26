/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.teamDigitale.dafreplicateingestion.model.api.ApiResponse;
import it.teamDigitale.dafreplicateingestion.model.api.ApiResponseError;
import it.teamDigitale.dafreplicateingestion.model.api.ApiResponseSuccess;
import it.teamDigitale.dafreplicateingestion.model.api.IngestionParams;
import it.teamDigitale.dafreplicateingestion.service.ApiIngestionService;

/**
 * @author alessandro
 *
 */
@RestController
@RequestMapping("api/v1/")
public class ApiController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

	@Autowired
	private ApiIngestionService apiIngestionService;

	@RequestMapping(value = "ingest", method = RequestMethod.POST, produces = "application/json")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ResponseBody
	public ApiResponse ingest(@Valid @RequestBody IngestionParams ingestion) {
		try {
			apiIngestionService.ingest(ingestion);
			
			return new ApiResponseSuccess(ingestion, Instant.now().toEpochMilli());
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e.getCause()));
			
			Map<String, Object> data = new HashMap<>();
			data.put("error", HttpStatus.INTERNAL_SERVER_ERROR.name());
			data.put("exception", e.getClass().getCanonicalName());
			data.put("stacktrace", e.getStackTrace());
			
			return new ApiResponseError(data, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
					Instant.now().toEpochMilli());
		}
	}
}
