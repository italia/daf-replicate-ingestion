package it.gov.daf.km4city.converter;

import it.teamDigitale.avro.Event;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;

public class UtilConverter {

    private static final Integer LOCATION = 1;

    public static void convertLocation(JSONObject item, Event output) {
        output.setEventTypeId(LOCATION);
        output.setTs(System.nanoTime());
        output.setBody(ByteBuffer.wrap(item.toJSONString().getBytes()));
    }
}
