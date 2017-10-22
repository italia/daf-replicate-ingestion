package it.gov.daf.km4city.producer;

import akka.actor.AbstractActor;
import akka.actor.Terminated;
import it.gov.daf.km4city.Main;
import it.teamDigitale.avro.Event;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaSend extends AbstractActor {


    private static final Logger logger = LoggerFactory.getLogger(KafkaSend.class);
    private static final String TOPIC = "OUTPUT_TOPIC";
    private final KafkaProducer<String, Event> producer;

    public KafkaSend() throws IOException {
        producer = new KafkaProducer<>(setup());
        logger.info("path {}",getSelf().path());
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


//    /**
//     * while not stopped, it will send events...
//     */
//    @Override
//    public void run() {
//         =null;
//        try {
//
//            while (!isExiting.get()) {
//                Event event = (Event) queue.take();
//                logger.info("sending event {}", event);
//                final ProducerRecord<String, Event> record = new ProducerRecord<>(TOPIC, event);
//                producer.send(record, (metadata, e) -> {
//                    if (e != null) {
//                        //error handling
//                        logger.error("error while sending ", e);
//                    }
//                });
//            }
//        } catch (Exception e) {
//            logger.error("error", e);
//        } finally {
//            if (producer != null) {
//                producer.close();
//            }
//        }
//    }

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
                .match(Event.class, event -> {
                    try {
                        logger.info("sending event {}", event);
                        final ProducerRecord<String, Event> record = new ProducerRecord<>(TOPIC, event);
                        producer.send(record, (metadata, e) -> {
                            if (e != null) {
                                //error handling
                                logger.error("error while sending ", e);
                            }
                        });
                    } catch (Exception e) {
                        logger.error("error", e);
                    }
                })
                .match(Terminated.class, eos -> producer.close())
                .matchAny(o -> logger.error("received unknown message"))
                .build();
    }
}
