package org.elkd.core.client;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.log4j.Logger;
import org.elkd.core.server.ElkdClusterServiceGrpc;
import org.elkd.core.server.ElkdClusterServiceGrpc.ElkdClusterServiceFutureStub;
import org.elkd.core.server.RpcAppendEntriesRequest;
import org.elkd.core.server.RpcAppendEntriesResponse;
import org.elkd.core.server.RpcEntry;
import org.elkd.core.server.RpcStateMachineCommand;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.elkd.core.server.RpcStateMachineCommand.Operation.SET;

public class TestClient {
  private static final Logger LOG = Logger.getLogger(TestClient.class);

  private final ManagedChannel mManagedChannel;
  private final ElkdClusterServiceFutureStub mStub;
  private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

  public TestClient(final String host, final int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  TestClient(final ManagedChannel managedChannel) {
    mManagedChannel = managedChannel;
    mStub = ElkdClusterServiceGrpc.newFutureStub(managedChannel);
  }

  public void shutdown() throws InterruptedException {
    mManagedChannel.awaitTermination(5, TimeUnit.SECONDS);
  }

  public void appendEntries() {
    LOG.info("appending entries call");

    final RpcAppendEntriesRequest request = RpcAppendEntriesRequest.newBuilder()
        .setTerm(0)
        .setPrevLogIndex(1)
        .setPrevLogTerm(1)
        .setLeaderId("node-1")
        .setLeaderCommit(0)
        .addEntries(
            RpcEntry.newBuilder().setEvent("amznStock").addCommands(
                RpcStateMachineCommand.newBuilder().setOperation(SET).setKey("price").setValue(String.valueOf(Math.random() * 2000)).build()
            ).build()
        )
        .build();
    ListenableFuture<RpcAppendEntriesResponse> response;
    try {
      response = mStub.appendEntries(request);
      response.addListener(() -> {
        try {
          LOG.info(response.get().getTerm());
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
      }, mExecutor);
    } catch (final StatusRuntimeException e) {
      LOG.error(e);
    }
  }

  public static void main(String[] args) throws InterruptedException {
    final TestClient client = new TestClient("localhost", 9191);
    try {
      for (;;) {
        client.appendEntries();
        Thread.sleep((long) (Math.random() * 500));
      }
    } finally {
      client.shutdown();
    }
  }
}
