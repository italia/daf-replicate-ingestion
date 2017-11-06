/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.converter;

import org.json.JSONObject;

import it.teamDigitale.avro.DataPoint;
import it.teamDigitale.avro.Event;

/**
 * @author alessandro
 *
 */
public interface Converter {
	public Event convertToEvent(JSONObject toBeConverted);
	public DataPoint convertToDataPoint(JSONObject toBeConverted);
}
