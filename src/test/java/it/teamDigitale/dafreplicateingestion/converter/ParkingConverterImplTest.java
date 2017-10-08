/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.converter;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.teamDigitale.avro.Event;
import it.teamDigitale.dafreplicateingestion.converter.ParkingConverterImpl;

/**
 * @author alessandro
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ParkingConverterImplTest {
	
	@Autowired
	private ParkingConverterImpl parkingConverter;
	private JSONObject jsonObject;
	
	@Before
	public void setup() throws Exception {
		jsonObject = new JSONObject("{\"Service\":{\"type\":\"FeatureCollection\",\"features\":[{\"geometry\":{\"type\":\"Point\",\"coordinates\":[11.2495,43.7759]},\"type\":\"Feature\",\"properties\":{\"name\":\"Garage La Stazione Spa\",\"typeLabel\":\"Car park\",\"serviceType\":\"TransferServiceAndRenting_Car_park\",\"phone\":\"055284784\",\"fax\":\"\",\"website\":\"\",\"province\":\"FI\",\"city\":\"FIRENZE\",\"cap\":\"50123\",\"email\":\"\",\"linkDBpedia\":[],\"note\":\"\",\"description\":\"\",\"description2\":\"\",\"multimedia\":\"\",\"serviceUri\":\"http://www.disit.org/km4city/resource/RT04801702315PO\",\"address\":\"PIAZZA DELLA STAZIONE\",\"civic\":\"3A\",\"wktGeometry\":\"\",\"photos\":[],\"photoThumbs\":[],\"photoOrigs\":[],\"avgStars\":0.0,\"starsCount\":0,\"comments\":[]},\"id\":1}]},\"realtime\":{\"head\":{\"parkingArea\":[\"Garage La Stazione Spa\"],\"vars\":[\"capacity\",\"freeParkingLots\",\"occupiedParkingLots\",\"occupancy\",\"updating\"]},\"results\":{\"bindings\":[{\"capacity\":{\"value\":\"617\"},\"freeParkingLots\":{\"value\":\"0\"},\"occupiedParkingLots\":{\"value\":\"0\"},\"occupancy\":{\"value\":\"0.0\"},\"status\":{\"value\":\"carParkClosed\"},\"updating\":{\"value\":\"2016-05-27T12:57:00+02:00\"}}]}}}");
	}
	
	@Test
	public void testParkingConverterJSONOjectToEvent() throws Exception {
		Event event = parkingConverter.convertToEvent(jsonObject);
		assertEquals("11.2495-43.7759", event.getLocation());
		assertEquals("http://www.disit.org/km4city/resource/RT04801702315PO", event.getService());
		assertEquals("Km4CityParking", event.getId());
	}
}
