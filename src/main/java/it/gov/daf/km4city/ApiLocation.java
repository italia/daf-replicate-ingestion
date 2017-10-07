package it.gov.daf.km4city;

import java.io.IOException;

public class ApiLocation extends ApiInvoker {

    private static final String url="http://servicemap.disit.org/WebAppGrafo/api/v1/?selection=";
    private static final String params = "&categories=SensorSite;Car_park&lang=it&format=json";

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
}
