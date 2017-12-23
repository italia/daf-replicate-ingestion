package it.gov.daf.km4city.actors;

import akka.actor.AbstractActor;
import akka.actor.Terminated;
import it.gov.daf.km4city.actors.messages.GetStats;
import it.gov.daf.km4city.actors.messages.Resume;
import it.gov.daf.km4city.actors.messages.Stats;
import it.gov.daf.km4city.actors.messages.Stop;
import it.teamDigitale.avro.Event;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaProducer extends AbstractActor {


    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private static final String TOPIC = "KM4CITY.";
    private final org.apache.kafka.clients.producer.KafkaProducer producer;
    private int ok;
    private int ko;

    public KafkaProducer() throws IOException {
        producer = new org.apache.kafka.clients.producer.KafkaProducer(setup());
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

    @Override
    public void preStart() {
        logger.info("KafkaProducer started");
    }

    @Override
    public void postStop() {
        logger.info("KafkaProducer stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Event.class, event -> {
                    try {
                        logger.info("sending event {}", event);
                        final ProducerRecord<String, Event> record = new ProducerRecord<>(TOPIC+event.getSource(), event);
                        producer.send(record, (metadata, e) -> {
                            if (e != null) {
                                //error handling
                                logger.error("error while sending ", e);
                                synchronized (this) {
                                    ko++;
                                }
                            }
                        });
                    } catch (Exception e) {
                        logger.error("error", e);
                        synchronized (this) {
                            ko++;
                        }
                    }
                    ok++;
                })
                .match(GetStats.class, getStats -> {
                    getSender().tell(new Stats("kafka_producer", ok,ko),getSelf());
                })
                .match(Stop.class, message -> {
                    getSender().tell(message, getSelf());
                })
                .match(Resume.class, message -> {
                    getSender().tell(message, getSelf());

                })
                .match(Terminated.class, eos -> producer.close())
                .matchAny(o -> logger.error("received unknown message"))
                .build();
    }
}
