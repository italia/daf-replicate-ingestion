package it.gov.daf.km4city.actors;

import akka.actor.AbstractActor;
import akka.actor.Terminated;
import it.gov.daf.km4city.actors.messages.GetStats;
import it.gov.daf.km4city.actors.messages.Stats;
import it.teamDigitale.avro.Event;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * actor that receives avro objects from a kafka topic
 */
public class ElasticSearchSink extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchSink.class);

    // es client
    private TransportClient client;

    //stats
    private int ok = 0;
    private int ko = 0;


    public ElasticSearchSink() throws IOException {
        Properties esProperites = setup();
        try {
            client = new PreBuiltTransportClient(Settings.builder()
                    .put("cluster.name", esProperites.getProperty("cluster.name")).build())
                    .addTransportAddress(new InetSocketTransportAddress(
                            InetAddress.getByName(esProperites.getProperty("cluster.url")),
                            Integer.parseInt(esProperites.getProperty("cluster.port")))
                    );
        } catch (UnknownHostException e) {
            logger.error("error",e);
        }
    }

    /**
     * read the config file
     *
     * @return properties config
     * @throws IOException in case of errors
     */
    private Properties setup() throws IOException {
        final Properties config = new Properties();
        try (final InputStream stream = Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream("elasticsearch.properties")) {
            config.load(stream);
        }

        return config;
    }

    private void process(Event event) {
        try {
            final String json = event.getBody().toString();
            final String mapping = event.getSource().toString();
            final String index = "iot_"+mapping;

            //put in elastic
            logger.info("indexing mapping {}, source {}", json, mapping);
            IndexResponse response = client.prepareIndex(index+"_"+mapping, mapping)
                    .setSource(json)
                    .get();

            logger.info("index result", response.status());
        } catch (Exception e) {
            logger.error("error while receiving...",e);
        }
    }

    @Override
    public void preStart() {
        logger.info("ES Sink started");
    }

    @Override
    public void postStop() {
        logger.info("ES Sink stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Event.class, event -> {
                    try {
                        logger.info("indexing event {}", event);
                        ok++;
                    } catch (Exception e) {
                        logger.error("error", e);
                        ko++;
                    }
                })
                .match(GetStats.class, getStats -> {
                    getSender().tell(new Stats("elasticsearch", ok,ko),getSelf());
                })
                .match(Terminated.class, eos -> client.close())
                .matchAny(o -> logger.error("received unknown message"))
                .build();
    }
}
