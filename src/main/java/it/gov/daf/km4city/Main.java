package it.gov.daf.km4city;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        ApiLocation apiLocation = new ApiLocation();
        try {
            apiLocation.getLocation(43.7741,11.2453,43.7768,11.2515);
        } catch (Exception e) {
            logger.error("error",e);
        }
        finally {
            apiLocation.close();
        }


    }

}
