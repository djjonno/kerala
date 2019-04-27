package org.elkd.core.consensus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Timer;
import java.util.TimerTask;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TimeoutAlarmTest {
  private static final long TIMEOUT = 50;

  @Mock TimeoutAlarm.TimerFactory mTimerFactory;
  @Mock Runnable mTimeoutTask;
  @Mock Timer mTimer;

  private TimeoutAlarm mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    doReturn(mTimer)
        .when(mTimerFactory)
        .createDaemonTimer();

    doAnswer(invocation -> {
      Thread.sleep(invocation.getArgument(1));
      ((Runnable) invocation.getArgument(0)).run();
      return null;
    })
        .when(mTimer)
        .schedule(any(TimerTask.class), eq(TIMEOUT));

    mUnitUnderTest = new TimeoutAlarm(mTimeoutTask, mTimerFactory);
  }

  @Test
  public void should_use_timerFactory() {
    // Given / When
    mUnitUnderTest.reset(TIMEOUT);

    // Then
    verify(mTimerFactory).createDaemonTimer();
  }

  @Test
  public void should_schedule_timeoutTask_with_timeout() {
    // Given / When
    mUnitUnderTest.reset(TIMEOUT);

    // Then
    verify(mTimer).schedule(any(TimerTask.class), eq(TIMEOUT));
  }

  @Test
  public void should_execute_timeoutTask_after_timeout_elapsed() throws InterruptedException {
    // Given / When
    mUnitUnderTest.reset(TIMEOUT);

    // Then
    verify(mTimeoutTask).run();
  }

  @Test
  public void should_use_new_timeout_when_reset() {
    // Given / When
    final long newTimeout = TIMEOUT * 2;
    mUnitUnderTest.reset(newTimeout);

    // Then
    verify(mTimer).schedule(any(TimerTask.class), eq(newTimeout));
  }

  @Test
  public void should_cancel_timer_on_stop() {
    // Given
    mUnitUnderTest.reset(TIMEOUT);

    // When
    mUnitUnderTest.stop();

    // Then
    verify(mTimer).cancel();
  }

  @Test
  public void should_not_cancel_timer_if_not_monitoring() {
    // Given / When
    mUnitUnderTest.stop();

    // Then
    verify(mTimer, never()).cancel();
  }

  @Test
  public void should_stop_previous_timer_on_reset() {
    // Given
    final Timer first = mock(Timer.class);
    final Timer second = mock(Timer.class);
    doReturn(first, second)
        .when(mTimerFactory)
        .createDaemonTimer();
    mUnitUnderTest.reset(TIMEOUT);

    // When
    mUnitUnderTest.reset(TIMEOUT);

    // Then
    verify(first).cancel();

    // When
    mUnitUnderTest.reset(TIMEOUT);

    // Then
    verify(second).cancel();
  }
}
