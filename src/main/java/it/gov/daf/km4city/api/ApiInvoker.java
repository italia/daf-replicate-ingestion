package it.gov.daf.km4city.api;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ApiInvoker {

    public static final Logger logger = LoggerFactory.getLogger(ApiInvoker.class);
    private final DefaultHttpClient httpClient;
    protected final JSONParser parser = new JSONParser();

    public ApiInvoker() {
        httpClient = new DefaultHttpClient();
    }

    public void close() {
        httpClient.getConnectionManager().shutdown();
    }

    public String invoke(String request) throws IOException {

        HttpGet getRequest = new HttpGet(request);
        getRequest.addHeader("accept", "application/json");

        HttpResponse response = httpClient.execute(getRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            logger.error("request error status {}",response.getStatusLine().getStatusCode());
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));
        String tmp;
        StringBuilder output = new StringBuilder();
        while ((tmp = br.readLine()) != null) {
            output.append(tmp);
        }
        return output.toString();
    }


}