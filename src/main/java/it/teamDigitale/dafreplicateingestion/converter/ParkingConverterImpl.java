/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.converter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.teamDigitale.avro.DataPoint;
import it.teamDigitale.avro.Event;

/**
 * @author alessandro
 *
 */
@Component
public class ParkingConverterImpl implements Converter{
	@Value("${km4city.base_url}")
	private String host;
	
	@Override
	public Event convertToEvent(JSONObject toBeConverted) {
		try {
			JSONArray coordinates = toBeConverted
					.getJSONObject("Service")
					.getJSONArray("features")
					.getJSONObject(0)
					.getJSONObject("geometry")
					.getJSONArray("coordinates");
			String location = coordinates.getString(0) + "-" + coordinates.getString(1);
			String service = toBeConverted
					.getJSONObject("Service")
					.getJSONArray("features")
					.getJSONObject(0)
					.getJSONObject("properties")
					.getString("serviceUri");
			long timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(toBeConverted
					.getJSONObject("realtime")
					.getJSONObject("results")
					.getJSONArray("bindings")
					.getJSONObject(0)
					.getJSONObject("updating")
					.getString("value")).getTime();
			ByteBuffer body = ByteBuffer.wrap(toBeConverted.toString().getBytes());
			@SuppressWarnings("unchecked")
			Map<CharSequence, CharSequence> attributes = new ObjectMapper().readValue(toBeConverted
					.getJSONObject("realtime").toString(), HashMap.class);
			
			return Event.newBuilder()
					.setLocation(location)
					.setAttributes(attributes)
					.setBody(body)
					.setHost(host)
					.setService(service)
					.setTs(timestamp)
					.setId("Km4CityParking")
					.setEventTypeId(1)
					.build();
			
		} catch (JSONException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public DataPoint convertToDataPoint(JSONObject toBeConverted) {
		// TODO Auto-generated method stub
		return null;
	}
}
