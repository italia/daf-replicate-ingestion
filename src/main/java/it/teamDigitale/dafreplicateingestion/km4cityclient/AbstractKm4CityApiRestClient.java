/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.km4cityclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author aletundo
 *
 */
@Service
public abstract class AbstractKm4CityApiRestClient {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	protected final RestTemplate restTemplate;
	
	@Value("${km4city.base_url}")
	protected String baseUrl;
	
	public AbstractKm4CityApiRestClient() {
		this.restTemplate = new RestTemplate();
	}
}
