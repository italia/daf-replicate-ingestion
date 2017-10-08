package it.teamDigitale.dafreplicateingestion.km4cityclient;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;;

@Service
public class Km4CityServiceApi extends AbstractKm4CityApiRestClient {

	public JSONObject consume(String serviceUri) {
		try {
			final String endpoint =  baseUrl + "?serviceUri=" + serviceUri;
			
			ResponseEntity<String> response = restTemplate.getForEntity(
					endpoint,
					String.class);
			
			JSONObject jsonResponse = new JSONObject(response.getBody());
			
			LOGGER.info(jsonResponse.toString());	
			return jsonResponse;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
