package org.elkd.core

open class ElkdRuntimeException : RuntimeException {
  constructor() : super() {}

  constructor(message: String) : super(message) {}

  constructor(throwable: Throwable) : super(throwable) {}

  constructor(message: String, cause: Throwable) : super(message, cause) {}
}
