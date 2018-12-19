package org.elkd.core.consensus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutMonitor {
  private final long mTimeout;
  private final Runnable mTimeoutTask;
  private final TimerFactory mTimerFactory;

  private Timer mTimer;

  public TimeoutMonitor(final int timeout, @Nonnull final Runnable timeoutTask) {
    this(timeout, timeoutTask, new TimerFactory());
  }

  @VisibleForTesting
  TimeoutMonitor(final long timeout, final Runnable timeoutTask, final TimerFactory timerFactory) {
    mTimeout = timeout;
    mTimeoutTask = Preconditions.checkNotNull(timeoutTask, "timeoutTask");
    mTimerFactory = Preconditions.checkNotNull(timerFactory, "timerFactory");
  }

  public void reset() {
    stop();
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
