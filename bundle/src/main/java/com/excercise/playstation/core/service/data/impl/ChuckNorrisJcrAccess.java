package com.excercise.playstation.core.service.data.impl;

import static javax.jcr.nodetype.NodeType.NT_UNSTRUCTURED;
import static com.day.cq.commons.jcr.JcrConstants.JCR_PRIMARYTYPE;
import static com.excercise.playstation.core.service.data.impl.ChuckNorrisDataServiceImpl.SUB_SERVICE;
import static com.excercise.playstation.core.service.data.impl.ChuckNorrisDataServiceImpl.STORAGE_LOCATION;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excercise.playstation.core.models.JokeResource;
import com.excercise.playstation.core.pojo.JokeData;
import com.google.common.collect.ImmutableMap;

/**
 * Chuck Norris JCR Access
 *
 * <p>Responsible for access the JCR for joke resources.
 */
@Service(ChuckNorrisJcrAccess.class)
@Component(immediate = true)
public class ChuckNorrisJcrAccess {

    private static final Logger log = LoggerFactory.getLogger(ChuckNorrisJcrAccess.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * @return a list of the joke resources stored within the JCR.
     */
    public List<JokeResource> retrieveStoredRecords() {
        ResourceResolver resourceResolver = buildServiceUserResourceResolver();

        if (resourceResolver == null) {
            log.error("Unable to retrieve the stored Chuck Norris Joke Data, the resource resolver was null");

            return Collections.emptyList();
        }

        return retrieveStoredRecords(resourceResolver);
    }

    /**
     * Store the new Joke data within the JCR.
     * @param jokeData to be stored within the JCR.
     */
    public void storeJokeData(JokeData jokeData, int storedJokesLimit) {
        ResourceResolver resourceResolver = buildServiceUserResourceResolver();

        // If the resource resolver failed to create the service user session then early escape and log.
        if (resourceResolver == null) {
            log.error("Unable to store the Joke Data due to null resource");
            return;
        }

        try {
            // Check that the Joke storage location is present, if it is not present then we cannot save any data
            Resource storageLocationResource = resourceResolver.getResource(STORAGE_LOCATION);
            // If the storage location for the jokes are null then early escape and log.
            if (storageLocationResource == null) {
                log.error("Cannot store the Joke Data, the storage location {}, was not present", STORAGE_LOCATION);
                return;
            }
            // Get the jokes that are stored within the JCR
            List<JokeResource> storedJokeResources = retrieveStoredRecords(resourceResolver);

            // We want to check that the joke data is not present in the data store.
            if (isJokeDataPresentInDataStore(jokeData, storedJokeResources)) {
                log.info("The joke data that was requested was already present in the stored jokes {}", jokeData);
                return;
            } else {
                // Since this is a new Joke then we need to create it.
                Resource resource = resourceResolver.create(storageLocationResource, jokeData.getId(),
                    buildResourceMapFromJokeData(jokeData));
                // Add the current entry to the list of resources
                storedJokeResources.add(resource.adaptTo(JokeResource.class));
            }

            // If the current list size is at the storedJokesLimit we would have just exceeded that limit
            // with the new resource.
            while (storedJokeResources.size() > storedJokesLimit) {
                // remove the first "oldest" resource from the list
                JokeResource remove = storedJokeResources.remove(0);
                // delete the resource from the jcr
                resourceResolver.delete(remove.getSourceResource());
            }
            // commit the changes to the JCR and close the resource resolver.
            resourceResolver.commit();
            resourceResolver.close();
        } catch (PersistenceException e) {
            log.error("Unable to create the resource {} for Chuck Norris Joke within the folder",
                jokeData.getId(), e);
        }
    }

    // Get an active resource resolver based on the service user.
    private ResourceResolver buildServiceUserResourceResolver() {
        try {
            return resourceResolverFactory.getServiceResourceResolver(
                ImmutableMap.of(ResourceResolverFactory.SUBSERVICE, SUB_SERVICE));
        } catch (LoginException e) {
            log.error("Unable to create service resource resolver for service user : {}", SUB_SERVICE);
        }

        return null;
    }

    // Check to see if the joke data is present in the list of stored jokes.
    private static boolean isJokeDataPresentInDataStore(JokeData endpointJokeData,
        List<JokeResource> storedJokeResources) {

        // Check that the any of the ID's for the stored joke data do not match the
        return storedJokeResources.stream()
            .anyMatch(jokeResource -> jokeResource.getId().equals(endpointJokeData.getId()));
    }

    // Get a list of the joke resources that are stored in the JCR.
    private static List<JokeResource> retrieveStoredRecords(ResourceResolver resourceResolver) {
        Resource storageLocation = resourceResolver.getResource(STORAGE_LOCATION);
        List<JokeResource> jokeResources = new ArrayList<>();

        if (storageLocation != null) {
            storageLocation
                .getChildren()
                .forEach(a -> jokeResources.add(a.adaptTo(JokeResource.class)));
        }

        jokeResources.sort(Comparator.comparing(JokeResource::getCreationTime));

        return jokeResources;
    }

    // Build a hash map to be used for the resource creation of the joke resource.
    private static Map<String, Object> buildResourceMapFromJokeData(JokeData jokeData) {
        return ImmutableMap.<String, Object>builder()
            .put(JokeResource.CATEGORY_PROPERTY, jokeData.getCategories().toArray())
            .put(JokeResource.ICON_PROPERTY, jokeData.getIconUrl())
            .put(JokeResource.ID_PROPERTY, jokeData.getId())
            .put(JokeResource.URL_PROPERTY, jokeData.getUrl())
            .put(JokeResource.VALUE_PROPERTY, jokeData.getValue())
            .put(JokeResource.CREATION_PROPERTY, Calendar.getInstance())
            .put(JCR_PRIMARYTYPE, NT_UNSTRUCTURED)
            .build();
    }

}