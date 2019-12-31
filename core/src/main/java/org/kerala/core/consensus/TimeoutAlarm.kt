package org.kerala.core.consensus

import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timerTask

class TimeoutAlarm(private val action: TimerTask.() -> Unit) {

  private var timer: Timer? = null
  private var timerFactory = TimerFactory()

  @ExperimentalUnsignedTypes
  fun reset(timeout: ULong) {
    stop()
    timer = timerFactory.createDaemonTimer()
    timer?.schedule(timerTask(action), timeout.toLong())
  }

  fun stop() {
    timer?.cancel()
  }

  class TimerFactory {
    fun createDaemonTimer(): Timer {
      return Timer(false)
    }
  }
}
