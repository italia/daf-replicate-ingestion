package it.gov.daf.km4city;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import it.gov.daf.km4city.api.ApiLocation;
import it.gov.daf.km4city.api.messages.Area;
import it.gov.daf.km4city.consumer.EventConsumer;
import it.gov.daf.km4city.producer.KafkaSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    //florence area, todo configuration, not hardcoded
    private static double lat1 = 43.743817;
    private static double long1 = 11.176357;
    private static double lat2 = 43.812729;
    private static double long2 = 11.304588;

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        logger.info("Process Starting...");

        try {
            final ActorSystem system = ActorSystem.create("Km4City-System");

            final ActorRef kafkaSender = system.actorOf(Props.create(KafkaSend.class), "kafka_sender");
            final ActorRef kafka2Elastic = system.actorOf(Props.create(EventConsumer.class), "kafka_2_elastic");

            final ActorRef apiLocation = system.actorOf(Props.create(ApiLocation.class,kafkaSender), "source");
            apiLocation.tell(new Area(lat1,long1,lat2,long2),apiLocation);
            kafka2Elastic.tell(new EventConsumer.Tick(),kafka2Elastic);


        } catch (Exception e) {
            logger.error("error", e);
        }
    }

}
