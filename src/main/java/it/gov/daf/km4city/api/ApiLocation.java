package it.gov.daf.km4city.api;

import akka.actor.ActorRef;
import akka.actor.Props;
import it.gov.daf.km4city.api.messages.Area;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiLocation extends ApiInvoker {

    private static final String url = "http://servicemap.disit.org/WebAppGrafo/api/v1/?selection=";
    private static final String params = "&categories=SensorSite;Car_park&lang=it&format=json";
    private final ActorRef kafkaSender;

    public ApiLocation(ActorRef kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    /**
     * @param c1 lat 1
     * @param c2 long 1
     * @param c3 lat 2
     * @param c4 long 2
     * @return the full json of the respose
     * @throws IOException
     */
    public String getLocation(double c1, double c2, double c3, double c4) throws IOException {
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
        JSONObject features = (JSONObject) json.get("SensorSites");
        if (features !=null) {
            JSONArray records = (JSONArray) features.get("features");
            result.addAll(records);
        }
       features= (JSONObject) json.get("Services");
        if (features !=null) {
            JSONArray records = (JSONArray) features.get("features");
            result.addAll(records);
        }
        return result;
    }

    @Override
    public void preStart() {
        logger.info("Api Location Application started");
    }

    @Override
    public void postStop() {
        logger.info("Api Location Application stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Area.class, area -> {
                    logger.debug("receiving area {}",area);
                    List<JSONObject> sensors = getLocationRecords(area.getLat1(), area.getLong1(), area.getLat2(), area.getLong2());
                    sensors.forEach( s -> {
                        ActorRef child = getContext().actorOf(Props.create(ApiEvent.class,kafkaSender));
                        logger.info("creating child actor for polling events, {}",child);
                        child.tell(s, getSelf());
                    });
                    logger.debug("receiving area done!");
                })
                .matchAny(o -> logger.error("received unknown message"))
                .build();
    }

}
