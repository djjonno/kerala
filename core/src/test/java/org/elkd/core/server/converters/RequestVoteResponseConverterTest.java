package org.elkd.core.server.converters;

import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.RpcRequestVoteResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RequestVoteResponseConverterTest {
  private static final int TERM = 1;
  private static final boolean VOTE_GRANTED = true;
  private static final RequestVoteResponse REQUEST_VOTE_RESPONSE = RequestVoteResponse.builder(TERM, VOTE_GRANTED).build();
  private static final RpcRequestVoteResponse RPC_REQUEST_VOTE_RESPONSE = RpcRequestVoteResponse
      .newBuilder().setTerm(TERM).setVoteGranted(VOTE_GRANTED).build();

  private RequestVoteResponseConverter mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    mUnitUnderTest = new RequestVoteResponseConverter();
  }

  @Test
  public void should_convert_RequestVoteResponse() {
    // Given / When
    final RpcRequestVoteResponse response = mUnitUnderTest.convert(REQUEST_VOTE_RESPONSE, mock(ConverterRegistry.class));

    // Then
    assertEquals(TERM, response.getTerm());
    assertEquals(VOTE_GRANTED, response.getVoteGranted());
  }

  @Test
  public void should_convert_RpcRequestVoteResponse() {
    // Given / When
    final RequestVoteResponse response = mUnitUnderTest.convert(RPC_REQUEST_VOTE_RESPONSE, mock(ConverterRegistry.class));

    // Then
    assertEquals(TERM, response.getTerm());
    assertEquals(VOTE_GRANTED, response.isVoteGranted());
  }
}
