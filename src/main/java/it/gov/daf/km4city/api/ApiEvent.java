package it.gov.daf.km4city.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiEvent extends ApiInvoker {

    private static final String url="http://servicemap.disit.org/WebAppGrafo/api/v1/?serviceUri=";

    public String getEvents(String sensorUri) throws IOException {
        return invoke(url+sensorUri);
    }

    public JSONObject getEventsFromJsonReply(JSONObject reply) throws IOException, ParseException {
        JSONObject properties = (JSONObject) reply.get("properties");
        String serviceUri = (String) reply.get("serviceUri");
        if (serviceUri != null) {
            return (JSONObject) parser.parse( getEvents(serviceUri));
        }
        return null;
    }



}
