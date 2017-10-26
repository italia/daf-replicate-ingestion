/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import it.teamDigitale.avro.Event;
import it.teamDigitale.dafreplicateingestion.converter.ParkingConverterImpl;
import it.teamDigitale.dafreplicateingestion.km4cityclient.Km4CityServiceApi;
import it.teamDigitale.dafreplicateingestion.producer.Sender;

/**
 * @author alessandro
 *
 */
@Service
public class ScheduledIngestionServiceImpl implements IngestionService {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Km4CityServiceApi serviceApi;
	
	@Autowired
	private Km4CityAvailableServices availableServices;
	
	@Autowired
	private ParkingConverterImpl parkingConverter;
	
	@Autowired
	private Sender sender;
	
	@Value("${kafka.topic.km4city}")
	private String topic;
	
	@Override
	@Scheduled(cron = "${km4city.ingestion_cron}")
	public void ingest() {
		LOGGER.info("Scheduled ingestion started!");
		for(String service : availableServices.getParkings() ) {
			JSONObject response = serviceApi.consume(service);
			Event event = parkingConverter.convertToEvent(response);;
			sender.send(event, topic);
		}
		LOGGER.info("Scheduled ingestion stopped!");
	}
}
