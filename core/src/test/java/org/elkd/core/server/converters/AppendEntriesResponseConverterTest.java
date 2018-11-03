package org.elkd.core.server.converters;

import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.server.RpcAppendEntriesResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AppendEntriesResponseConverterTest {

  private static final int TERM = 1;
  private static final boolean SUCCESS = true;

  private static final AppendEntriesResponse APPEND_ENTRIES_RESPONSE = AppendEntriesResponse.builder(TERM, SUCCESS).build();
  private static final RpcAppendEntriesResponse RPC_APPEND_ENTRIES_RESPONSE = RpcAppendEntriesResponse.newBuilder()
      .setTerm(TERM).setSuccess(SUCCESS).build();

  private AppendEntriesResponseConverter mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    mUnitUnderTest = new AppendEntriesResponseConverter();
  }

  @Test
  public void should_convert_AppendEntriesResponse() {
    // Given / When
    final RpcAppendEntriesResponse response = mUnitUnderTest.convert(APPEND_ENTRIES_RESPONSE, mock(ConverterRegistry.class));

    // Then
    assertEquals(TERM, response.getTerm());
    assertEquals(SUCCESS, response.getSuccess());
  }

  @Test
  public void should_convert_RpcAppendEntriesResponse() {
    // Given / When
    final AppendEntriesResponse response = mUnitUnderTest.convert(RPC_APPEND_ENTRIES_RESPONSE, mock(ConverterRegistry.class));

    // Then
    assertEquals(TERM, response.getTerm());
    assertEquals(SUCCESS, response.isSuccessful());
  }
}
