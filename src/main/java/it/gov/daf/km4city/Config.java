package it.gov.daf.km4city;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    static Properties loadConfig() throws IOException {
        try (InputStream configFile=Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            prop.load(configFile);
            return prop;
        } catch (IOException e) {
            logger.error("error while loading config",e);
            throw e;
        }
    }

}
