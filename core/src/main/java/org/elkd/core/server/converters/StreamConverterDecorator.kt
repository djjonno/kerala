package org.elkd.core.server.converters

import io.grpc.stub.StreamObserver
import java.lang.Exception

class StreamConverterDecorator<Source, Target>(private val targetObserver: StreamObserver<Target>,
                                               private val converter: Converter<Source, Target>) : StreamObserver<Source> {
  override fun onNext(source: Source) {
    try {
      targetObserver.onNext(converter.convert(source))
    } catch (e: Exception) {
      onError(e)
    }
  }

  override fun onError(t: Throwable) {
    targetObserver.onError(t)
  }

  override fun onCompleted() {
    targetObserver.onCompleted()
  }
}
