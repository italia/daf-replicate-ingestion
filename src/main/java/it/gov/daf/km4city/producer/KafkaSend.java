package it.gov.daf.km4city.producer;

import it.teamDigitale.avro.Event;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaSend implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(KafkaSend.class);

    private static final String TOPIC = "OUTPUT_TOPIC";
    private AtomicBoolean isExiting = new AtomicBoolean(false);
    private final BlockingQueue queue = new ArrayBlockingQueue(1024);

    public BlockingQueue getQueue() {
        return queue;
    }

    /**
     * read the config file
     *
     * @return properties config
     * @throws IOException in case of errors
     */
    private Properties setup() throws IOException {
        final Properties config = new Properties();
        try (final InputStream stream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("producer.properties")) {
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
    }


    /**
     * while not stopped, it will send events...
     */
    @Override
    public void run() {
        KafkaProducer<String, Event> producer = null;
        try {
            producer = new KafkaProducer<>(setup());
            while (!isExiting.get()) {
                Event event = (Event) queue.take();
                logger.info("sending event {}", event);
                final ProducerRecord<String, Event> record = new ProducerRecord<>(TOPIC, event);
                producer.send(record, (metadata, e) -> {
                    if (e != null) {
                        //error handling
                        logger.error("error while sending ", e);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (producer != null) {
                producer.close();
            }
        }
    }
}
