package org.elkd.core.server.converters;

import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.server.RpcRequestVoteRequest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RequestVoteRequestConverterTest {

  private static final int TERM = 1;
  private static final String CANDIDATE_ID = "candidateId";
  private static final int LAST_LOG_INDEX = 2;
  private static final int LAST_LOG_TERM = 3;

  private static final RequestVoteRequest REQUEST_VOTE_REQUEST = RequestVoteRequest
      .builder(TERM, CANDIDATE_ID, LAST_LOG_INDEX, LAST_LOG_TERM).build();
  private static final RpcRequestVoteRequest RPC_REQUEST_VOTE_REQUEST = RpcRequestVoteRequest.newBuilder()
      .setTerm(TERM).setCandidateId(CANDIDATE_ID).setLastLogIndex(LAST_LOG_INDEX).setLastLogTerm(LAST_LOG_TERM).build();
  private RequestVoteRequestConverter mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    mUnitUnderTest = new RequestVoteRequestConverter();
  }

  @Test
  public void should_convert_RequestVoteRequest() {
    // Given / When
    final RpcRequestVoteRequest request = mUnitUnderTest.convert(REQUEST_VOTE_REQUEST, mock(ConverterRegistry.class));

    // Then
    assertEquals(TERM, request.getTerm());
    assertEquals(CANDIDATE_ID, request.getCandidateId());
    assertEquals(LAST_LOG_INDEX, request.getLastLogIndex());
    assertEquals(LAST_LOG_TERM, request.getLastLogTerm());
  }

  @Test
  public void should_convert_RpcRequestVoteRequest() {
    // Given / When
    final RequestVoteRequest request = mUnitUnderTest.convert(RPC_REQUEST_VOTE_REQUEST, mock(ConverterRegistry.class));

    // Then
    assertEquals(TERM, request.getTerm());
    assertEquals(CANDIDATE_ID, request.getCandidateId());
    assertEquals(LAST_LOG_INDEX, request.getLastLogIndex());
    assertEquals(LAST_LOG_TERM, request.getLastLogTerm());
  }
}
