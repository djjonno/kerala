package org.elkd.core.consensus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutMonitor {
  private final Runnable mTimeoutTask;
  private final TimerFactory mTimerFactory;

  private long mTimeout;
  private Timer mTimer;

  public TimeoutMonitor(@Nonnull final Runnable timeoutTask) {
    this(timeoutTask, new TimerFactory());
  }

  @VisibleForTesting
  TimeoutMonitor(final Runnable timeoutTask, final TimerFactory timerFactory) {
    mTimeoutTask = Preconditions.checkNotNull(timeoutTask, "timeoutTask");
    mTimerFactory = Preconditions.checkNotNull(timerFactory, "timerFactory");
  }

  public void reset(final long timeout) {
    Preconditions.checkState(timeout > 0);
    stop();
    mTimeout = timeout;
    mTimer = mTimerFactory.createDaemonTimer();
    mTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        mTimeoutTask.run();
      }
    }, mTimeout);
  }

  public void stop() {
    if (mTimer != null) {
      mTimer.cancel();
    }
  }

  @VisibleForTesting static class TimerFactory {
    Timer createDaemonTimer() {
      return new Timer(true);
    }
  }
}
