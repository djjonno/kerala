package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver

class StreamObserverDecorator<T>(
    val stream: StreamObserver<T>,
    val onNextBlock: (StreamObserver<T>, T) -> Unit = { stream, value -> stream.onNext(value) },
    val onErrorBlock: (StreamObserver<T>, Throwable) -> Unit = { stream, throwable -> stream.onError(throwable) },
    val onCompleteBlock: (StreamObserver<T>) -> Unit = { stream.onCompleted() }
) : StreamObserver<T> {
  override fun onNext(value: T) {
    onNextBlock(stream, value)
  }

  override fun onError(t: Throwable) {
    onErrorBlock(stream, t)
  }

  override fun onCompleted() {
    onCompleteBlock(stream)
  }
}
