package it.gov.daf.km4city.api;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
        // set the connection timeout value to 30 seconds (30000 milliseconds)
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams,5000);
        httpClient = new DefaultHttpClient(httpParams);
    }

    public void close() {
        httpClient.getConnectionManager().shutdown();
    }

    public String invoke(String request) throws IOException {

        HttpGet getRequest = new HttpGet(request);
        getRequest.addHeader("accept", "application/json");

        logger.info("calling {}",request);
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
