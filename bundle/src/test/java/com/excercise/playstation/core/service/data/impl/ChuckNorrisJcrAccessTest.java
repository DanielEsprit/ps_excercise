package com.excercise.playstation.core.service.data.impl;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.excercise.playstation.core.service.data.impl.ChuckNorrisDataServiceImpl.STORAGE_LOCATION;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.excercise.playstation.core.models.JokeResource;
import com.excercise.playstation.core.pojo.JokeData;
import com.google.common.collect.ImmutableList;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChuckNorrisJcrAccessTest {

    @InjectMocks
    private ChuckNorrisJcrAccess chuckNorrisJcrAccess;

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource storageLocationResource;

    @Mock
    private Resource storedResourceX, createdResource, sourceResourceA, sourceResourceB, sourceResourceC;

    private JokeResource jokeResourceA, jokeResourceB, jokeResourceC;

    @BeforeEach
    void setup() throws LoginException, PersistenceException {
        ImmutableList<Resource> childResources = ImmutableList.of(storedResourceX, storedResourceX, storedResourceX);
        // Create the list of joke resources.
        jokeResourceA = createJokeResource("zzzzzz", 20, sourceResourceA);
        jokeResourceB = createJokeResource("xxxxxx", 8, sourceResourceB);
        jokeResourceC = createJokeResource("yyyyyy", 2, sourceResourceC);

        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        when(resourceResolver.getResource(STORAGE_LOCATION)).thenReturn(storageLocationResource);
        when(storageLocationResource.getChildren()).thenReturn(childResources);

        when(storedResourceX.adaptTo(JokeResource.class)).thenReturn(jokeResourceA, jokeResourceB, jokeResourceC);
        when(resourceResolver.create(eq(storageLocationResource), anyString(), anyMap())).thenReturn(createdResource);
        when(createdResource.adaptTo(JokeResource.class)).thenReturn(new JokeResource());
    }

    @Test
    void shouldCreateANewJokeResource() throws PersistenceException {
        JokeData jokeData = createJokeData("wwwwwww");

        // Set the limit to be shorter than the number of resources
        chuckNorrisJcrAccess.storeJokeData(jokeData, 3);

        // Verify the resource has been created
        verify(resourceResolver).create(eq(storageLocationResource), eq("wwwwwww"), anyMap());
        // Should delete the oldest resource
        verify(resourceResolver).delete(sourceResourceC);
        verify(resourceResolver).commit();
        verify(resourceResolver).close();
    }

    @Test
    void shouldFailToCreateANewJokeResourceWhenResourceResolverIsNull() throws PersistenceException, LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(null);
        JokeData jokeData = createJokeData("wwwwwww");

        // Set the limit to be shorter than the number of resources
        chuckNorrisJcrAccess.storeJokeData(jokeData, 3);

        // Should never create new resource
        verify(resourceResolver, never()).create(eq(storageLocationResource), eq("wwwwwww"), anyMap());
    }

    @Test
    void shouldFailToCreateANewJokeResourceWhenStorageResourceIsNull() throws PersistenceException, LoginException {
        when(resourceResolver.getResource(STORAGE_LOCATION)).thenReturn(null);
        JokeData jokeData = createJokeData("wwwwwww");

        // Set the limit to be shorter than the number of resources
        chuckNorrisJcrAccess.storeJokeData(jokeData, 3);

        // Should never create new resource
        verify(resourceResolver, never()).create(eq(storageLocationResource), eq("wwwwwww"), anyMap());
    }

    @Test
    void shouldFailToCreateANewJokeResourceWhenJokeIDInTheStoredJokesList()
        throws PersistenceException, LoginException {
        JokeData jokeData = createJokeData("zzzzzz");

        // Set the limit to be shorter than the number of resources
        chuckNorrisJcrAccess.storeJokeData(jokeData, 3);

        // Should never create new resource
        verify(resourceResolver, never()).create(eq(storageLocationResource), eq("zzzzzz"), anyMap());
    }


    private static JokeData createJokeData(String id) {
        JokeData jokeData = new JokeData();

        jokeData.setCategories(new ArrayList<>());
        jokeData.setId(id);
        jokeData.setIconUrl("http://www.icon.url");
        jokeData.setUrl("http://www.api.url");

        jokeData.setValue("This is the value of the joke");

        return jokeData;
    }

    private static JokeResource createJokeResource(String id, int dayOfMonth, Resource sourceResource) {
        JokeResource jokeResource = mock(JokeResource.class);

        Calendar.Builder builder = new Calendar.Builder().setDate(2020, 5, dayOfMonth).setTimeOfDay(8, 30, 0);

        when(jokeResource.getId()).thenReturn(id);
        when(jokeResource.getCreationTime()).thenReturn(builder.build().getTime());
        when(jokeResource.getSourceResource()).thenReturn(sourceResource);

        return jokeResource;
    }

}