package org.elkd.core.server;

import io.grpc.stub.StreamObserver;

public class ElkdService extends ElkdServiceGrpc.ElkdServiceImplBase {
  @Override
  public StreamObserver<HelloRequest> sayHello(final StreamObserver<HelloReply> responseObserver) {
    return super.sayHello(responseObserver);
  }
}
