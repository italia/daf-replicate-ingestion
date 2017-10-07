package it.gov.daf.km4city;

import it.gov.daf.km4city.api.ApiEvent;
import it.gov.daf.km4city.api.ApiLocation;
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
        try {
            List<JSONObject> result = apiLocation.getLocationRecords(43.7741,11.2453,43.7768,11.2515);
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

            logger.info("result {}",result);
            logger.info("events {}",events);

        } catch (Exception e) {
            logger.error("error",e);
        }
        finally {
            apiLocation.close();
        }


    }

}
