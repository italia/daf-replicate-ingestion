package it.gov.daf.km4city.converter;

import it.gov.daf.km4city.actors.messages.Stats;
import it.teamDigitale.avro.Event;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

public class UtilConverter {

    private static final Integer EVENT = 1;

    public static void convertEvent(JSONObject item, Event output) throws IOException {

        long timestamp = System.nanoTime();
        output.setEventTypeId(EVENT);
        output.setTs(timestamp);
        output.setBody(ByteBuffer.wrap(item.toJSONString().getBytes()));
        output.setAttributes(new HashMap());

        output.setSource("sensor");
        output.setId("iot.id."+timestamp);
        output.setHost("host");
        output.setLocation("location");
        output.setService("type");

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
