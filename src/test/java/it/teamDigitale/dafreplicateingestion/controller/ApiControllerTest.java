/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.teamDigitale.dafreplicateingestion.model.api.IngestionParams;
import it.teamDigitale.dafreplicateingestion.service.Km4CityServiceType;

/**
 * @author alessandro
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("kafka")
@WebAppConfiguration
public class ApiControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@MockBean
	private ApiController apiController;

	private IngestionParams validIngestion;
	private IngestionParams invalidIngestion;

	@Before
	public void setup() {
		List<String> services = new ArrayList<>();
		services.add("http://www.disit.org/km4city/resource/RT04801702315PO");
		
		this.validIngestion = new IngestionParams(services, "km4city", Km4CityServiceType.PARKING);
		
		this.invalidIngestion = new IngestionParams();
		this.invalidIngestion.setServices(services);
		
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testIngestionWithValidJson() throws Exception {
		mockMvc.perform(post("/api/v1/ingest")
				.content(new ObjectMapper().writeValueAsString(this.validIngestion))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isAccepted());

	}
	
	@Test
	public void testIngestionWithInvalidJson() throws Exception {
		mockMvc.perform(post("/api/v1/ingest")
				.content(new ObjectMapper().writeValueAsString(this.invalidIngestion))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());

	}
}
