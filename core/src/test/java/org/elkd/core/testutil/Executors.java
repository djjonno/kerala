package org.elkd.core.testutil;

import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class Executors {
  private Executors() { }

  /**
   * This executor will instantly execute any runnable object that is
   * passed to the executor on the same thread as the one invokving mExecutor.execute(Runnable)
   *
   * @return mocked ExecutorService
   */
  public static ExecutorService getMockedSerialExecutor() {
    final ExecutorService executorService = mock(ExecutorService.class);

    doAnswer(invocation -> {
      ((Runnable) invocation.getArgument(0)).run();
      return null;
    })
        .when(executorService)
        .execute(any());

    return executorService;
  }
}
