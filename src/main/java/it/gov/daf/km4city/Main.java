package it.gov.daf.km4city;

import it.gov.daf.km4city.api.ApiLocation;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        ApiLocation apiLocation = new ApiLocation();
        try {
            List<JSONObject> result = apiLocation.getLocationRecords(43.7741,11.2453,43.7768,11.2515);
            logger.info("result {}",result);
        } catch (Exception e) {
            logger.error("error",e);
        }
        finally {
            apiLocation.close();
        }


    }

}
