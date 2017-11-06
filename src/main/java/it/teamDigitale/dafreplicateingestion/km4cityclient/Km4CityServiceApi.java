package it.teamDigitale.dafreplicateingestion.km4cityclient;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;;

@Service
public class Km4CityServiceApi extends AbstractKm4CityApiRestClient {
	@Retryable(
		      value = { Exception.class }, 
		      maxAttempts = 3
	)
	public JSONObject consume(String serviceUri) {
		try {
			final String endpoint =  baseUrl + "?serviceUri=" + serviceUri;
			
			ResponseEntity<String> response = restTemplate.getForEntity(
					endpoint,
					String.class);
			
			JSONObject jsonResponse = new JSONObject(response.getBody());
			
			LOGGER.debug(jsonResponse.toString());	
			return jsonResponse;
		} catch(Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw e;
		}
	}
}
