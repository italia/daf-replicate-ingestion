package it.gov.daf.km4city.converter;

import it.teamDigitale.avro.Event;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class UtilConverter {

    private static final Integer LOCATION = 1;
    private static final Integer EVENT = 2;
    private static final String KM4CITY = "KM4CITY";

    public static void convertLocation(JSONObject item, Event output) {
        long timestamp = System.nanoTime();
        output.setEventTypeId(LOCATION);
        output.setSource("location");
        output.setId(""+timestamp);
        output.setEventTypeId(LOCATION);
        output.setHost("NA");
        output.setLocation("NA");
        output.setService(KM4CITY);
        output.setTs(timestamp);
        output.setBody(ByteBuffer.wrap(item.toJSONString().getBytes()));
        output.setAttributes(new HashMap<>());
    }

    public static void convertEvent(JSONObject item, Event output) {
        long timestamp = System.nanoTime();
        output.setEventTypeId(EVENT);
        output.setSource("event");
        output.setId(""+timestamp);
        output.setEventTypeId(LOCATION);
        output.setHost("NA");
        output.setLocation("NA");
        output.setService(KM4CITY);
        output.setTs(timestamp);
        output.setBody(ByteBuffer.wrap(item.toJSONString().getBytes()));
        output.setAttributes(new HashMap<>());
    }
}
