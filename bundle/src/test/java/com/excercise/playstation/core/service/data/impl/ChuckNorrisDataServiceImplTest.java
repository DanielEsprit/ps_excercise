package com.excercise.playstation.core.service.data.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static com.excercise.playstation.core.service.data.impl.ChuckNorrisDataServiceImpl.CHRON_EXPRESSION;
import static com.excercise.playstation.core.service.data.impl.ChuckNorrisDataServiceImpl.CHRON_JOB_ENABLED;
import static com.excercise.playstation.core.service.data.impl.ChuckNorrisDataServiceImpl.DEFAULT_CHRON_EXPRESSION;
import static com.excercise.playstation.core.service.data.impl.ChuckNorrisDataServiceImpl.DEFAULT_LIMIT;
import static com.excercise.playstation.core.service.data.impl.ChuckNorrisDataServiceImpl.LIMIT_PROPERTY;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.excercise.playstation.core.service.jobs.ChuckNorrisChronJob;

@ExtendWith(MockitoExtension.class)
class ChuckNorrisDataServiceImplTest {

    @InjectMocks
    private ChuckNorrisDataServiceImpl chuckNorrisDataService;

    @Mock
    private Scheduler scheduler;

    @Mock
    private ChuckNorrisJcrAccess jcrAccess;

    @Mock
    private ChuckNorrisApiAccess apiAccess;

    private Map<String, Object> properties = new HashMap<>();

    @Mock
    private ScheduleOptions scheduleOptions;

    @BeforeEach
    void setup() {
        properties.put(LIMIT_PROPERTY, DEFAULT_LIMIT);
        properties.put(CHRON_EXPRESSION, DEFAULT_CHRON_EXPRESSION);
    }

    @Test
    void testSchedulerIsCalledWhenChronJobIsEnabled() {
        when(scheduler.EXPR(DEFAULT_CHRON_EXPRESSION)).thenReturn(scheduleOptions);
        properties.put(CHRON_JOB_ENABLED, true);

        chuckNorrisDataService.activate(properties);

        verify(scheduler, times(1)).schedule(any(ChuckNorrisChronJob.class), eq(scheduleOptions));
    }

    /**
     * Using this structure because for some reason I could not get @MethodSource
     * to work correctly.
     */
    @Test
    void testSchedulerIsNotCalledWhenChronJobIsDisabled() {
        properties.put(CHRON_JOB_ENABLED, false);

        chuckNorrisDataService.activate(properties);

        verifyZeroInteractions(scheduler);
    }

}