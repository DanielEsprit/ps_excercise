package com.excercise.playstation.core.service.jobs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;

import com.excercise.playstation.core.pojo.JokeData;
import com.excercise.playstation.core.service.data.ChuckNorrisDataService;

@ExtendWith(MockitoExtension.class)
class ChuckNorrisChronJobTest {

    @Mock
    private ChuckNorrisDataService chuckNorrisDataService;

    private JokeData jokeData = new JokeData();

    private ChuckNorrisChronJob underTest;

    @BeforeEach
    public void setup() {
        underTest = new ChuckNorrisChronJob(chuckNorrisDataService);
    }

    @Test
    public void testTheJokeIsStoredWhenJokeIsReturned() {
        when(chuckNorrisDataService.retrieveJokeData()).thenReturn(jokeData);

        underTest.run();

        verify(chuckNorrisDataService).storeJokeData(jokeData);
    }

    @Test
    public void testTheJokeIsNotStoredWhenJokeIsNull() {
        when(chuckNorrisDataService.retrieveJokeData()).thenReturn(null);

        underTest.run();

        verify(chuckNorrisDataService, times(0))
            .storeJokeData(any(JokeData.class));
    }

}