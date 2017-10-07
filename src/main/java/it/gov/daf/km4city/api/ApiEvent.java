package it.gov.daf.km4city.api;

import java.io.IOException;

public class ApiEvent extends ApiInvoker {

    private static final String url="http://servicemap.disit.org/WebAppGrafo/api/v1/?serviceUri=";

    public String getEvents(String sensorUri) throws IOException {
        return invoke(url+sensorUri);
    }

}
