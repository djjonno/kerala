package org.elkd.core.server;

import io.grpc.stub.StreamObserver;

public class ElkdService extends ElkdServiceGrpc.ElkdServiceImplBase {
  @Override
  public void sayHello(final HelloRequest request, final StreamObserver<HelloReply> responseObserver) {
    super.sayHello(request, responseObserver);
  }
}
