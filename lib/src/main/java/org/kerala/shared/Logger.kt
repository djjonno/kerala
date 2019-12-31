package org.kerala.shared

import org.apache.log4j.Logger

inline fun <reified T> T.logger(block: LoggerBuilder.() -> Unit) {
  LoggerBuilder(Logger.getLogger(T::class.java)).block()
}

inline fun <reified T> T.logger(msg: String) {
  LoggerBuilder(Logger.getLogger(T::class.java)).i(msg)
}

inline fun <reified T> T.logger(obj: Any) {
  LoggerBuilder(Logger.getLogger(T::class.java)).i(obj.toString())
}

class LoggerBuilder(private val logger: Logger) {
  fun i(msg: String?) {
    logger.info(msg)
  }

  fun d(msg: String?) {
    logger.debug(msg)
  }

  fun t(msg: String?) {
    logger.trace(msg)
  }

  fun e(msg: String?) {
    logger.error(msg)
  }

  fun f(msg: String?) {
    logger.fatal(msg)
  }
}
