package it.gov.daf.km4city;

import it.gov.daf.km4city.api.ApiEvent;
import it.gov.daf.km4city.api.ApiLocation;
import it.gov.daf.km4city.converter.UtilConverter;
import it.gov.daf.km4city.producer.KafkaSend;
import it.teamDigitale.avro.Event;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        //just a little test
        ApiLocation apiLocation = new ApiLocation();
        KafkaSend sender = new KafkaSend();

        try {
            List<JSONObject> result = apiLocation.getLocationRecords(43.743817,11.176357,43.812729,11.304588);


            logger.info("result {}",result);
            result.forEach(
                    item -> {
                        Event e = new Event();
                        UtilConverter.convertLocation(item,e);
                        sender.getQueue().add(e);
                    }
            );


            List<JSONObject> events = result.stream().map(
                    item -> {
                        ApiEvent event = new ApiEvent();
                        try {
                            return event.getEventsFromJsonReply(item);
                        } catch (SocketTimeoutException e) {
                            logger.warn("service not replying!");
                        } catch (Exception e) {
                            logger.error("error", e);
                        } finally {
                            event.close();
                        }
                        return null;
                    }
            ).collect(Collectors.toList());


            logger.info("events {}",events);

        } catch (Exception e) {
            logger.error("error",e);
        }
        finally {
            apiLocation.close();
            sender.stop();
        }


    }

}