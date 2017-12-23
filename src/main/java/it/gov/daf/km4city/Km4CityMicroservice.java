package it.gov.daf.km4city;


import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.PatternsCS;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.util.Timeout;
import it.gov.daf.km4city.actors.ApiSupervisor;
import it.gov.daf.km4city.actors.ElasticSearchSink;
import it.gov.daf.km4city.actors.KafkaProducer;
import it.gov.daf.km4city.actors.messages.GetStats;
import it.gov.daf.km4city.actors.messages.Resume;
import it.gov.daf.km4city.actors.messages.StartArea;
import it.gov.daf.km4city.actors.messages.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.Properties;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static it.gov.daf.km4city.Config.loadConfig;
import static it.gov.daf.km4city.Km4CityMicroservice.ActorContext.*;


public class Km4CityMicroservice extends AllDirectives {

    public static final Logger logger = LoggerFactory.getLogger(Km4CityMicroservice.class);

    //global references/settings
    public static class ActorContext {
        public static int polling;
        public static ActorRef kafkaProducer;
        public static ActorRef elasticSink;
        public static ActorRef km4citySupervisor;
    }

    public static void main(String[] args) throws Exception {

        //akka actor system
        final ActorSystem system = ActorSystem.create("Km4City-System");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        //In order to access all directives we need an instance where the routes are define.
        Km4CityMicroservice app = new Km4CityMicroservice();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("0.0.0.0", 8080), materializer);

        logger.info("server listening on 0.0.0.0:8080");

        //configuration
        final Properties applicationConfig = loadConfig();
        polling= Integer.parseInt(applicationConfig.getProperty("polling"));
        final double lat1 = Double.parseDouble(applicationConfig.getProperty("area.lat1"));
        final double long1 = Double.parseDouble(applicationConfig.getProperty("area.long1"));
        final double lat2 = Double.parseDouble(applicationConfig.getProperty("area.lat2"));
        final double long2 = Double.parseDouble(applicationConfig.getProperty("area.long2"));

        //actors
        kafkaProducer = system.actorOf(Props.create(KafkaProducer.class), "kafkaSink");
        elasticSink = system.actorOf(Props.create(ElasticSearchSink.class), "esSink");
        km4citySupervisor = system.actorOf(Props.create(ApiSupervisor.class), "km4citySupervisor");
        //starting...
        km4citySupervisor.tell(new StartArea(lat1,long1,lat2,long2), km4citySupervisor);

        //handling shout down
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                        //unbind rest api and stop actor system
                        binding
                                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                                .thenAccept(unbound -> system.terminate()); // and shutdown when done));
                }));

    }


    private Route createRoute() {
        return get(() -> route(
                path("resume",(() ->
                        extractRequest(httpRequest -> {
                            Timeout timeout = new Timeout(Duration.create(500, TimeUnit.SECONDS));
                            CompletionStage<HttpResponse> completionStage = PatternsCS.ask(km4citySupervisor, new Resume(), timeout)
                                    .thenApplyAsync(HttpResponse.class::cast);
                            return completeWithFuture(completionStage);
                        })
                )),
                path("pause",(() ->
                        extractRequest(httpRequest -> {
                            Timeout timeout = new Timeout(Duration.create(500, TimeUnit.SECONDS));
                            CompletionStage<HttpResponse> completionStage = PatternsCS.ask(km4citySupervisor, new Stop(), timeout)
                                    .thenApplyAsync(HttpResponse.class::cast);
                            return completeWithFuture(completionStage);
                        })
                )),
                path("stats",(() ->
                        extractRequest(httpRequest -> {
                            Timeout timeout = new Timeout(Duration.create(500, TimeUnit.SECONDS));
                            CompletionStage<HttpResponse> completionStage = PatternsCS.ask(km4citySupervisor, new GetStats(), timeout)
                                    .thenApplyAsync(HttpResponse.class::cast);
                            return completeWithFuture(completionStage);
                        })
                ))
        ));
    }

}