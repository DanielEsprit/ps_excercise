package com.excercise.playstation.core.service.data;

import java.util.List;

import com.excercise.playstation.core.models.JokeResource;
import com.excercise.playstation.core.pojo.JokeData;

/**
 * Chuck Norris Data Service.
 *
 * <p>Allows for the retrieval and storage of the chuck norris joke data.
 */
public interface ChuckNorrisDataService {

    /**
     * Retrieve a list of joke resources from the JCR.
     * @return a list of joke resources.
     */
    List<JokeResource> retrieveStoredRecords();

    /**
     * Store the joke data within the JCR.
     * @param jokeData the joke data that we want to store.
     */
    void storeJokeData(JokeData jokeData);

    /**
     * Get the Joke Data from the remote API.
     * @return structured joke Data.
     */
    JokeData retrieveJokeData();
}