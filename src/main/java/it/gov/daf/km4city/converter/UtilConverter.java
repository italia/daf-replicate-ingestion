package it.gov.daf.km4city.converter;

import it.gov.daf.km4city.actors.messages.Stats;
import it.teamDigitale.avro.Event;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

public class UtilConverter {

    private static final Integer EVENT = 1;

    public static void convertEvent(JSONObject item, Event output) throws IOException {

        final JSONObject sensor = (JSONObject) item.get("Sensor");

        final JSONObject features = (JSONObject) ((JSONArray) sensor.get("features")).get(0);
        final JSONObject properties = (JSONObject) features.get("properties");
        //uri of the sensor
        final String serviceUri = (String) properties.get("serviceUri");
        final String sensorName = (String) properties.get("name");
        final String serviceType = (String) properties.get("serviceType");
        //geolocation
        final JSONObject location = (JSONObject) features.get("geometry");

        long timestamp = System.nanoTime();
        output.setEventTypeId(EVENT);
        output.setSource(sensorName);
        output.setId(sensorName+timestamp);
        output.setHost(serviceUri);
        output.setLocation(location.toJSONString());
        output.setService(serviceType);
        output.setTs(timestamp);
        output.setBody(ByteBuffer.wrap(item.toJSONString().getBytes()));
        output.setAttributes(new ObjectMapper().readValue(item.toJSONString(), HashMap.class));

    }

    public static String statsToJson(final List<Stats> stats) {
        JSONObject object = new JSONObject();
        stats.forEach(
                stat -> {
                    JSONObject item = new JSONObject();
                    item.put("ok", stat.getOk());
                    item.put("ko", stat.getKo());
                    object.put(stat.getUri(), item);
                }
        );
        return object.toJSONString();
    }

}
