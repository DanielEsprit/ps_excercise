package com.excercise.playstation.core.service.data.impl;

import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excercise.playstation.core.models.JokeResource;
import com.excercise.playstation.core.pojo.JokeData;
import com.excercise.playstation.core.service.jobs.ChuckNorrisChronJob;
import com.excercise.playstation.core.service.data.ChuckNorrisDataService;

/**
 * Implementing the Chuck Norris Data Service Interface.
 */
@Service(ChuckNorrisDataService.class)
@Component(immediate = true, metatype = true)
public class ChuckNorrisDataServiceImpl implements ChuckNorrisDataService {

    private static final Logger log = LoggerFactory.getLogger(ChuckNorrisDataServiceImpl.class);

    protected static final String DEFAULT_CHRON_EXPRESSION = "* 0/2 * * * ?";

    protected static final int DEFAULT_LIMIT = 10;

    @Property(label = "Stored jokes limit", intValue = DEFAULT_LIMIT)
    protected static final String LIMIT_PROPERTY = "stored.jokes.limit";

    @Property(label = "Job Enabled", boolValue = false)
    protected static final String CHRON_JOB_ENABLED = "stored.jokes.chron.enabled";

    @Property(label = "Chron Expression", value = DEFAULT_CHRON_EXPRESSION)
    protected static final String CHRON_EXPRESSION = "stored.jokes.chron";

    protected static final String STORAGE_LOCATION = "/etc/chuck-norris/chuck-norris-data";

    protected static final String JOKES_ENDPOINT = "https://api.chucknorris.io/jokes/random";

    protected static final String SUB_SERVICE = "chuckNorris";

    protected static final String CHRON_JOB_NAME = "chuck-norris-jokes-job";

    @Reference
    private Scheduler scheduler;

    @Reference
    private ChuckNorrisJcrAccess jcrAccess;

    @Reference
    private ChuckNorrisApiAccess apiAccess;

    private int storedJokesLimit;

    /**
     * Create the Chron job for calling the Chuck Norris endpoint.
     *
     * @param properties the OSGI config properties.
     */
    @Activate
    protected final void activate(final Map<String, Object> properties) {
        // Get the property limit value / default to the DEFAULT_LIMIT
        storedJokesLimit = PropertiesUtil.toInteger(properties.get(LIMIT_PROPERTY), DEFAULT_LIMIT);
        // Get the configured Chron Expression
        String chronExpression = PropertiesUtil.toString(properties.get(CHRON_EXPRESSION), DEFAULT_CHRON_EXPRESSION);

        // Check to see if the job is enabled, if false, then early escape and log.
        if (PropertiesUtil.toBoolean(properties.get(CHRON_JOB_ENABLED), false)) {

            // Create a chron expression
            ScheduleOptions options = scheduler.EXPR(chronExpression);
            options.canRunConcurrently(false);
            options.name(CHRON_JOB_NAME);

            // Schedule the job to run with reference to this data service
            scheduler.schedule(new ChuckNorrisChronJob(this), options);
        } else {
            log.info("The Chuck Norris Joke Data Service is not enabled; enable=false");
        }
    }

    /**
     * Deactivate the component, un-scheduling the chron job.
     *
     * @param properties the OSGI service properties.
     */
    @Deactivate
    protected final void deactivate(final Map<String, Object> properties) {
        // Cancel the job so that it does not continue
        scheduler.unschedule(CHRON_JOB_NAME);
    }

    /**
     * {@inheritDoc}
     */
    public List<JokeResource> retrieveStoredRecords() {
        return jcrAccess.retrieveStoredRecords();
    }

    /**
     * {@inheritDoc}
     */
    public void storeJokeData(JokeData jokeData) {
        jcrAccess.storeJokeData(jokeData, storedJokesLimit);
    }

    /**
     * {@inheritDoc}
     */
    public JokeData retrieveJokeData() {
        return apiAccess.retrieveJokeData();
    }

}
