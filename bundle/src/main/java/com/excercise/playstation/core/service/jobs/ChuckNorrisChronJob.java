package com.excercise.playstation.core.service.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excercise.playstation.core.pojo.JokeData;
import com.excercise.playstation.core.service.data.ChuckNorrisDataService;

/**
 * Chuck Norris Chron Job.
 *
 * <p>This chron job gets run to request and process the chuck norris data.
 */
public class ChuckNorrisChronJob implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ChuckNorrisChronJob.class);

    public ChuckNorrisDataService chuckNorrisDataService;

    /**
     * Create a new Chuck Norris Chron Job.
     *
     * @param chuckNorrisDataService the data service for calling the endpoint.
     */
    public ChuckNorrisChronJob(ChuckNorrisDataService chuckNorrisDataService) {
        this.chuckNorrisDataService = chuckNorrisDataService;
    }

    /**
     * Run the job.
     */
    public void run() {
        // get the data service to store the information within the JCR
        final JokeData joke = chuckNorrisDataService.retrieveJokeData();

        // If the joke is null, early escape and log the error.
        if (joke == null) {
            log.warn("The joke data that was retrieved from the chuck norris joke endpoint was null");
            return;
        }

        chuckNorrisDataService.storeJokeData(joke);
    }
}
