/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.km4cityclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author alessandro
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application.yml")
public class Km4CityServiceApiTest {
	
	@Autowired
	private Km4CityServiceApi serviceApi;
	
	@Autowired
	private Config config;
	
    @TestConfiguration
    static class Km4CityServiceApiTestContextConfiguration {
  
        @Bean
        public Km4CityServiceApi serviceApi() {
            return new Km4CityServiceApi();
        }
    }
	
    @Test
    public void getServiceGivenServiceUri() 
      throws Exception {
 
        JSONObject jsonResponse = this.serviceApi.consume(config.getParkings().get(0));
 
        assertEquals("Garage La Stazione Spa", jsonResponse
        		.getJSONObject("realtime")
        		.getJSONObject("head")
        		.getJSONArray("parkingArea").get(0));
    }
	
	@Configuration
	@EnableConfigurationProperties
	@ConfigurationProperties(prefix = "km4city.services")
	static class Config {
		private List<String> parkings;
		
		public List<String> getParkings() {
			return parkings;
		}

		public void setParkings(List<String> parkings) {
			this.parkings = parkings;
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyPlaceholder() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}
}
