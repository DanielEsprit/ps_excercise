package com.excercise.playstation.core.service.data.impl;

import static com.excercise.playstation.core.service.data.impl.ChuckNorrisDataServiceImpl.JOKES_ENDPOINT;

import java.io.IOException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excercise.playstation.core.pojo.JokeData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Chuck Norris Api Access
 *
 * <p>Responsible for getting the data from the Chuck norris jokes api and returning it as
 * a structured Java object.
 */
@Service(ChuckNorrisApiAccess.class)
@Component(immediate = true)
public class ChuckNorrisApiAccess {

    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;

    private static final Logger log = LoggerFactory.getLogger(ChuckNorrisApiAccess.class);

    private static final ObjectMapper jsonMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * @return Return the joke data from the joke API.
     */
    public JokeData retrieveJokeData() {
        // Create the builder with default config
        CloseableHttpClient client = httpClientBuilderFactory
            .newBuilder()
            .setDefaultRequestConfig(RequestConfig.DEFAULT)
            .build();

        HttpGet getRequest = new HttpGet(JOKES_ENDPOINT);

        try {
            HttpResponse servResponse = client.execute(getRequest);

            if (servResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String responseBody = EntityUtils.toString(servResponse.getEntity());

                return jsonMapper.readValue(responseBody, JokeData.class);
            }
        } catch (JsonProcessingException e) {
            log.error("Unable to parse the JSONResponse", e);
        } catch (IOException e) {
            log.error("Unable to connect to jokes endpoint {}", JOKES_ENDPOINT, e);
        }

        return null;
    }

}