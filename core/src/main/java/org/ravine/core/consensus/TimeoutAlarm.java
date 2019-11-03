package org.ravine.core.consensus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutAlarm {
  private final Runnable mTimeoutTask;
  private final TimerFactory mTimerFactory;

  private Timer mTimer;

  public TimeoutAlarm(@Nonnull final Runnable timeoutTask) {
    this(timeoutTask, new TimerFactory());
  }

  @VisibleForTesting
  TimeoutAlarm(final Runnable timeoutTask, final TimerFactory timerFactory) {
    mTimeoutTask = Preconditions.checkNotNull(timeoutTask, "timeoutTask");
    mTimerFactory = Preconditions.checkNotNull(timerFactory, "timerFactory");
  }

  public void reset(final long timeout) {
    Preconditions.checkState(timeout > 0);
    stop();
    mTimer = mTimerFactory.createDaemonTimer();
    mTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        mTimeoutTask.run();
      }
    }, timeout);
  }

  public void stop() {
    if (mTimer != null) {
      mTimer.cancel();
    }
  }

  @VisibleForTesting static class TimerFactory {
    Timer createDaemonTimer() {
      return new Timer(false);
    }
  }
}
