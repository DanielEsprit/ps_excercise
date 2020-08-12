package com.excercise.playstation.core.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.excercise.playstation.core.service.data.ChuckNorrisDataService;
import com.google.common.collect.ImmutableList;

@ExtendWith(MockitoExtension.class)
class ChuckNorrisListComponentTest {

    @InjectMocks
    private ChuckNorrisListComponent underTest;

    @Mock
    private ChuckNorrisDataService chuckNorrisDataService;

    @Mock
    private JokeResource jokeResource;

    @Test
    void testGetJokesReturnsServicesData() {
        List<JokeResource> list = new ArrayList<>();
        list.add(jokeResource);
        list.add(jokeResource);

        when(chuckNorrisDataService.retrieveStoredRecords()).thenReturn(list);
        underTest.init();

        assertAll(
            () -> assertFalse(underTest.getJokes().isEmpty()),
            () -> assertEquals(underTest.getJokes().size(), 2),
            () -> assertEquals(underTest.getJokes().get(0), jokeResource)
        );
    }

}