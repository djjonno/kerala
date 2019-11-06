package org.kerala.core.runtime.client.stream

import io.grpc.stub.StreamObserver

class StreamObserverDecorator<T>(
    val next: StreamObserver<T>,
    val onNextBlock: (StreamObserver<T>, T) -> Unit = { stream, value -> stream.onNext(value) },
    val onErrorBlock: (StreamObserver<T>, Throwable) -> Unit = { stream, throwable -> stream.onError(throwable) },
    val onCompleteBlock: (StreamObserver<T>) -> Unit = { next.onCompleted() }
) : StreamObserver<T> {
  override fun onNext(value: T) {
    onNextBlock(next, value)
  }

  override fun onError(t: Throwable) {
    onErrorBlock(next, t)
  }

  override fun onCompleted() {
    onCompleteBlock(next)
  }
}
