package it.gov.daf.km4city.actors;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import it.gov.daf.km4city.Km4CityMicroservice;
import it.gov.daf.km4city.actors.messages.GetStats;
import it.gov.daf.km4city.actors.messages.Resume;
import it.gov.daf.km4city.actors.messages.Stats;
import it.gov.daf.km4city.actors.messages.Stop;
import it.gov.daf.km4city.converter.UtilConverter;
import it.teamDigitale.avro.Event;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiWorker extends ApiInvoker {

    private final ActorRef kafkaSender;
    private final ActorRef esSink;

    private final int polling;
    private Status status;

    private int ok;
    private int ko;

    enum Status {RUNNING, PAUSED}

    public ApiWorker() {
        super();
        this.status = Status.RUNNING;
        this.polling=Km4CityMicroservice.ActorContext.polling;
        this.kafkaSender = Km4CityMicroservice.ActorContext.kafkaProducer;
        this.esSink = Km4CityMicroservice.ActorContext.elasticSink;
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
        String serviceUri = getUri();
        if (serviceUri != null) {
            return (JSONObject) parser.parse( getEvents(serviceUri));
        }
        return null;
    }

    private String getUri() {
        JSONObject properties = (JSONObject) json.get("properties");
        return (String) properties.get("serviceUri");
    }

    @Override
    public void preStart() {
        logger.info("Api Worker started");
    }

    @Override
    public void postStop() {
        logger.info("Api Worker stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JSONObject.class, json -> {
                    this.json = json;
                    handlePollingRequest();
                    getTimers().startPeriodicTimer(TICK_KEY, new Tick(),
                            Duration.create(polling, TimeUnit.SECONDS));

                })
                .match(Tick.class, tick -> {
                    logger.debug("receiving tick message");
                    handlePollingRequest();
                })
                .match(GetStats.class, getStats -> {
                    getSender().tell(new Stats(getUri(), ok,ko),getSelf());
                })
                .match(Terminated.class, end -> logger.info("exiting"))
                .match(Stop.class, message -> {
                    if (status == Status.RUNNING) {
                        logger.info("stopping");
                        getSender().tell(message, getSelf());
                        status = Status.PAUSED;
                    }
                })
                .match(Resume.class, message -> {
                    if (status == Status.PAUSED) {
                        logger.info("resuming");
                        getSender().tell(message, getSelf());
                        status = Status.RUNNING;
                    }
                })
                .matchAny(o -> logger.error("received unknown message"))
                .build();
    }

    private void handlePollingRequest() throws IOException, ParseException {

        if (status == Status.PAUSED) {
            return;
        }

        try {
            logger.debug("receiving json message {}", json);
            JSONObject event = getEventsFromJsonReply();
            Event e = new Event();
            UtilConverter.convertEvent(event, e);
            kafkaSender.tell(e, getSelf());
            esSink.tell(e, getSelf());
            ok++;
        } catch (Exception e) {
            logger.error("error",e);
            ko++;
        }
    }

}
