package it.gov.daf.km4city.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiLocation extends ApiInvoker {

    private static final String url="http://servicemap.disit.org/WebAppGrafo/api/v1/?selection=";
    private static final String params = "&categories=SensorSite;Car_park&lang=it&format=json";
    private final JSONParser parser = new JSONParser();

    /**
     *
     * @param c1 lat 1
     * @param c2 long 1
     * @param c3 lat 2
     * @param c4 long 2
     * @return the full json of the respose
     * @throws IOException
     */
    public String getLocation(double c1,double c2, double c3, double c4) throws IOException {
        StringBuilder request = new StringBuilder();
        request.append(url)
                .append(c1)
                .append(";")
                .append(c2)
                .append(";")
                .append(c3)
                .append(";")
                .append(c4)
                .append(params);
        return invoke(request.toString());
    }

    public List<JSONObject> getLocationRecords(double c1, double c2, double c3, double c4) throws IOException, ParseException {
        List<JSONObject> result = new ArrayList<>();
        JSONObject json = (JSONObject) parser.parse(getLocation(c1, c2, c3, c4));
        JSONObject features = (JSONObject)json.get("SensorSites");
        JSONArray records = (JSONArray) features.get("features");
        result.addAll(records);
        return result;
    }
}
