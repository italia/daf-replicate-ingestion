/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author alessandro
 *
 */
@Component
@ConfigurationProperties(prefix = "km4city.services")
public class Km4CityAvailableServices {
	private List<String> parkings;

	public Km4CityAvailableServices() {
		this.parkings = new ArrayList<>();
	}

	public List<String> getParkings() {
		return this.parkings;
	}
}
