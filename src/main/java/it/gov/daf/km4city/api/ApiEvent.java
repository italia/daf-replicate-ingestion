package it.gov.daf.km4city.api;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Terminated;
import it.gov.daf.km4city.converter.UtilConverter;
import it.teamDigitale.avro.Event;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiEvent extends ApiInvoker {

    private final ActorRef kafkaSender;

    public ApiEvent(ActorRef kafkaSender) {
        super();
        this.kafkaSender = kafkaSender;
    }

    private static final String url="http://servicemap.disit.org/WebAppGrafo/api/v1/?serviceUri=";
    private static Object TICK_KEY = "TickKey";

    private static final class Tick {
    }

    private JSONObject json;

    public String getEvents(String sensorUri) throws IOException {
        return invoke(url+sensorUri);
    }

    public JSONObject getEventsFromJsonReply() throws IOException, ParseException {
        JSONObject properties = (JSONObject) json.get("properties");
        String serviceUri = (String) properties.get("serviceUri");
        if (serviceUri != null) {
            return (JSONObject) parser.parse( getEvents(serviceUri));
        }
        return null;
    }

    @Override
    public void preStart() {
        logger.info("Api Event Application started");
    }

    @Override
    public void postStop() {
        logger.info("Api Event Application stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JSONObject.class, json -> {
                    this.json = json;
                    logger.debug("receiving json message {}",json);
                    JSONObject event = getEventsFromJsonReply();
                    Event e = new Event();
                    UtilConverter.convertEvent(event,e);
                    kafkaSender.tell(e,getSelf());
                    getTimers().startPeriodicTimer(TICK_KEY, new Tick(),
                            Duration.create(100, TimeUnit.SECONDS));

                })
                .match(Tick.class, tick -> {
                    logger.debug("receiving tick message");
                    JSONObject event = getEventsFromJsonReply();
                })
                .match(Terminated.class, end -> logger.info("exiting"))
                .matchAny(o -> logger.error("received unknown message"))
                .build();
    }




}
