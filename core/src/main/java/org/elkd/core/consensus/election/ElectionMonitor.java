package org.elkd.core.consensus.election;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;

public class ElectionMonitor {
  private final long mTimeout;
  private final Runnable mTimeoutTask;
  private final TimerFactory mTimerFactory;

  private Timer mTimer;

  public ElectionMonitor(final int timeout, @Nonnull final Runnable timeoutTask) {
    this(timeout, timeoutTask, new TimerFactory());
  }

  @VisibleForTesting
  ElectionMonitor(final long timeout, final Runnable timeoutTask, final TimerFactory timerFactory) {
    mTimeout = timeout;
    mTimeoutTask = Preconditions.checkNotNull(timeoutTask, "timeoutTask");
    mTimerFactory = Preconditions.checkNotNull(timerFactory, "timerFactory");
  }

  public void monitor() {
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
