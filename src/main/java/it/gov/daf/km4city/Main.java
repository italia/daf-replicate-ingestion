package it.gov.daf.km4city;

import it.gov.daf.km4city.api.ApiEvent;
import it.gov.daf.km4city.api.ApiLocation;
import it.gov.daf.km4city.converter.UtilConverter;
import it.gov.daf.km4city.producer.KafkaSend;
import it.teamDigitale.avro.Event;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.List;

public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        logger.info("Process Starting...");

        //just a little test
        ApiLocation apiLocation = new ApiLocation();
        KafkaSend sender = new KafkaSend();

        //starting kafka producer
        Thread threadSender = new Thread(sender);
        threadSender.start();

        try {
            //prende i sensori di firenze
            List<JSONObject> result = apiLocation.getLocationRecords(43.743817,11.176357,43.812729,11.304588);
            logger.info("result {}",result);
            //pubblica info sui sensori
            result.forEach(
                    item -> {
                        Event e = new Event();
                        UtilConverter.convertLocation(item,e);
                        sender.getQueue().add(e);
                    }
            );

            //prende eventi
            while (true) {
                result.stream().forEach(
                        item -> {
                            ApiEvent event = new ApiEvent();
                            try {
                                JSONObject json = event.getEventsFromJsonReply(item);
                                Event e = new Event();
                                UtilConverter.convertEvent(json,e);
                                sender.getQueue().add(e);
                            } catch (SocketTimeoutException e) {
                                logger.warn("service not replying!");
                            } catch (Throwable e) {
                                logger.error("error", e);
                            } finally {
                                event.close();
                            }
                        }
                );
                Thread.sleep(30000);//polla ogni 30 secondi le info del stato
            }


        } catch (Exception e) {
            logger.error("error",e);
        }
        finally {
            apiLocation.close();
            sender.stop();
            threadSender.join();
        }
    }

}
