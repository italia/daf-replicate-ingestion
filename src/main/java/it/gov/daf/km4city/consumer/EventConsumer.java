package it.gov.daf.km4city.consumer;

import it.teamDigitale.avro.Event;
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
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * executor that receives avro objects from a kafka topic
 */
public class EventConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private static final String TOPIC = "OUTPUT_TOPIC";
    private static final long TIMEOUT = 30000;
    private final AtomicBoolean isExiting = new AtomicBoolean(false);

    private KafkaConsumer<String, Event> consumer = null;
    // on startup
    TransportClient client;
    private long received = 0;

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

    /**
     * stop the executor
     */
    public void stop() {
        logger.info("exiting...");
        isExiting.set(true);
        if (consumer != null) {
            consumer.wakeup(); //exit from poll...
        }
    }

    /**
     * while not stopped, it will send orders...
     */
    @Override
    public void run() {

        try {
            client = new PreBuiltTransportClient(Settings.builder()
                    .put("cluster.name", "internal_test").build())
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("40.115.16.108"), 9300));
        } catch (UnknownHostException e) {
            logger.error("error",e);
        }

        try {
            consumer = new KafkaConsumer<>(setup());
            consumer.subscribe(Collections.singletonList(TOPIC));
            while (!isExiting.get()) {
                consumer.poll(TIMEOUT).forEach(this::process);
                consumer.commitAsync();
            }

        } catch (WakeupException w) {
            //ignoring
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (consumer != null) {
                try {
                    consumer.commitSync();
                } finally {
                    consumer.close();
                }
            }
        }
    }

    private void process(ConsumerRecord<String, Event> event) {
        logger.debug("received event [{},{}]", event.key(), event.value());
        ++received;

        final String json = event.value().getBody().toString();
        final String mapping = event.value().getSource().toString();
        final String index = "iot";

        //put in elastic
        logger.info("indexing mapping {}, source {}",json,mapping);
        IndexResponse response = client.prepareIndex(index, mapping)
                .setSource(json)
                .get();

        logger.info("index result",response.status());
    }
}
