/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import it.teamDigitale.dafreplicateingestion.converter.ConverterType;
import it.teamDigitale.dafreplicateingestion.model.api.IngestionParams;

/**
 * @author alessandro
 *
 */
@Service
public class ScheduledIngestionService extends AbstractIngestionService {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Km4CityAvailableServices availableServices;
	
	@Value("${kafka.topic.km4city}")
	private String topic;
	
	@Scheduled(cron = "${km4city.ingestion_cron}")
	public void ingest() {
		LOGGER.info("Scheduled ingestion started!");
		IngestionParams ingestion = new IngestionParams();
		ingestion.setServices(availableServices.getParkings());
		ingestion.setTopic(topic);
		ingestion.setServiceType(ConverterType.PARKING);
		super.ingest(ingestion);
		LOGGER.info("Scheduled ingestion stopped!");
	}
}
