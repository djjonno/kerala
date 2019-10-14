package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class DispatchOnPoolStreamDecorator<T>(private val next: StreamObserver<T>,
                                       private val executor: ExecutorService) : StreamObserver<T> {
  override fun onNext(value: T) {
    executor.submit {
      next.onNext(value)
    }.get()
  }

  override fun onError(t: Throwable) {
    executor.submit {
      next.onError(t)
    }.get()
  }

  override fun onCompleted() {
    executor.submit {
      next.onCompleted()
    }.get()
  }
}
