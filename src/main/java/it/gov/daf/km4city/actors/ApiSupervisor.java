package it.gov.daf.km4city.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpResponse;
import akka.pattern.Patterns;
import akka.util.Timeout;
import it.gov.daf.km4city.Km4CityMicroservice;
import it.gov.daf.km4city.actors.messages.GetStats;
import it.gov.daf.km4city.actors.messages.StartArea;
import it.gov.daf.km4city.actors.messages.Stats;
import it.gov.daf.km4city.converter.UtilConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import scala.concurrent.Await;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ApiSupervisor extends ApiInvoker {

    private static final String url = "http://servicemap.disit.org/WebAppGrafo/api/v1/?selection=";
    private static final String params = "&categories=SensorSite;Car_park&lang=it&format=json";
    private final static List<ActorRef> actors = new ArrayList<>();

    public ApiSupervisor() {
        actors.add(Km4CityMicroservice.ActorContext.elasticSink);
        actors.add(Km4CityMicroservice.ActorContext.kafkaProducer);
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
                .match(StartArea.class, area -> {
                    logger.debug("receiving area {}", area);
                    List<JSONObject> sensors = getLocationRecords(area.getLat1(), area.getLong1(), area.getLat2(), area.getLong2());
                    sensors.forEach(s -> {
                        ActorRef child = getContext().actorOf(Props.create(ApiWorker.class));
                        logger.info("creating child actor for polling events, {}", child);
                        child.tell(s, getSelf());
                        actors.add(child);
                    });
                    logger.debug("receiving area done!");
                })
                .match(GetStats.class, getStats -> {
                    logger.info("fetching stats");
                    final Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
                    List<Stats> fetchedStats = actors.stream()
                            .map(
                                    c -> Patterns.ask(c, getStats, timeout)
                            ).map(
                                    future -> {
                                        Stats stats = null;
                                        try {
                                            stats = (Stats) Await.result(future, timeout.duration());
                                        } catch (Exception e) {
                                            logger.error("error", e);
                                        }
                                        return stats;
                                    }
                            ).filter(Objects::nonNull)
                            .collect(Collectors.toList());


                    HttpResponse httpResponse = HttpResponse.create()
                            .withEntity(ContentTypes.APPLICATION_JSON,
                                    UtilConverter.statsToJson(fetchedStats)
                                    );

                    getSender().tell(httpResponse, getSelf());


                })
                .matchAny(o -> logger.error("received unknown message {}", o))
                .build();
    }

    public List<JSONObject> getLocationRecords(double c1, double c2, double c3, double c4) throws IOException, ParseException {
        List<JSONObject> result = new ArrayList<>();
        JSONObject json = (JSONObject) parser.parse(getLocation(c1, c2, c3, c4));
        JSONObject features = (JSONObject) json.get("SensorSites");
        if (features != null) {
            JSONArray records = (JSONArray) features.get("features");
            result.addAll(records);
        }
        features = (JSONObject) json.get("Services");
        if (features != null) {
            JSONArray records = (JSONArray) features.get("features");
            result.addAll(records);
        }
        return result;
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

}
