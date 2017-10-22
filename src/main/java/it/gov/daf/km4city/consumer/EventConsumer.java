package it.gov.daf.km4city.consumer;

import akka.actor.AbstractActor;
import akka.actor.Terminated;
import it.gov.daf.km4city.Main;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
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
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Properties;

/**
 * actor that receives avro objects from a kafka topic
 */
public class EventConsumer extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private static final String TOPIC = "OUTPUT_TOPIC";
    private static final long TIMEOUT = 30000;

    public static final class Tick {
    }


    private KafkaConsumer<String, GenericRecord> consumer = null;
    // on startup
    private TransportClient client;
    private long received = 0;

    public EventConsumer() throws IOException {
        try {
            client = new PreBuiltTransportClient(Settings.builder()
                    .put("cluster.name", "elasticsearch").build())
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            logger.error("error",e);
        }

        consumer = new KafkaConsumer<>(setup());
        consumer.subscribe(Collections.singletonList(TOPIC));

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
                .getResourceAsStream("consumer.properties")) {
            config.load(stream);
        }

        return config;
    }


    private void process(ConsumerRecord<String, GenericRecord> event) {
        logger.info("received event [{},{}]", event.key(), event.value());
        ++received;

        try {
            final ByteBuffer json_byte = (ByteBuffer) event.value().get("body");
            final String json = new String(json_byte.array());
            final String mapping = event.value().get("source").toString();
            final String index = "iot";

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
        logger.info("KafkaSend started");
    }

    @Override
    public void postStop() {
        logger.info("KafkaSend stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Tick.class, tick -> {
                    try {
                        logger.info("waiting messages");
                        consumer.poll(TIMEOUT).forEach(this::process);
                        consumer.commitAsync(); //asynch
                        self().tell(tick,self());
                    } catch (WakeupException w) {
                        //ignoring
                    } catch (Exception e) {
                        logger.error("error", e);
                    }
                })
                .match(Terminated.class, eos -> {
                    try {
                        //synch commit before exiting
                        consumer.commitSync();
                    } finally {
                        consumer.close();
                    }
                })
                .matchAny(o -> logger.error("received unknown message"))
                .build();
    }
}
