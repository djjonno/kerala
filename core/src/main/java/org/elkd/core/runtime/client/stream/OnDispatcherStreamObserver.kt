package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class OnDispatcherStreamObserver<T>(
    private val next: StreamObserver<T>,
    private val coroutineScope: CoroutineScope
) : StreamObserver<T>, CoroutineScope by coroutineScope {
  override fun onNext(value: T) {
    runBlocking {
      launch {
        next.onNext(value)
      }
    }
  }

  override fun onError(t: Throwable) {
    runBlocking {
      launch {
        next.onError(t)
      }
    }
    coroutineScope.coroutineContext.cancel()
  }

  override fun onCompleted() {
    runBlocking {
      launch {
        next.onCompleted()
      }
    }
    coroutineScope.coroutineContext.cancel()
  }
}
