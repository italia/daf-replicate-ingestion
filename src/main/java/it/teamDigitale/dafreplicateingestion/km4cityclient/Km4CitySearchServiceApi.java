/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.km4cityclient;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author alessandro
 *
 */
@Service
public class Km4CitySearchServiceApi extends AbstractKm4CityApiRestClient {
	
	public JsonNode consume(Map<String, String> parameters) {
		try {
			
			ResponseEntity<String> response = restTemplate.getForEntity(
					baseUrl,
					String.class,
					parameters);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonBody = mapper.readTree(response.getBody());
			
			LOGGER.debug(response.getBody());	
			return jsonBody;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
