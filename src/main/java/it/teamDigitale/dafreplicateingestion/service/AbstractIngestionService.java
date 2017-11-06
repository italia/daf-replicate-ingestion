/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.teamDigitale.avro.Event;
import it.teamDigitale.dafreplicateingestion.converter.ParkingConverterImpl;
import it.teamDigitale.dafreplicateingestion.km4cityclient.Km4CityServiceApi;
import it.teamDigitale.dafreplicateingestion.model.api.IngestionParams;
import it.teamDigitale.dafreplicateingestion.producer.Sender;

/**
 * @author alessandro
 *
 */
public abstract class AbstractIngestionService {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected Km4CityServiceApi serviceApi;
	
	@Autowired
	private Sender sender;
	
	@Autowired
	private ParkingConverterImpl parkingConverterImpl;
	
	protected void ingest(IngestionParams ingestion) {
		for(String service : ingestion.getServices()) {
			JSONObject response = serviceApi.consume(service);
			Event event = parkingConverterImpl.convertToEvent(response);
			sender.send(event, ingestion.getTopic());
		}
	}
}
