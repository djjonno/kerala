package org.elkd.core.server.converters;

import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.server.cluster.RpcEntry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class EntryConverterTest {
  private static final int TERM = 1;
  private static final String EVENT = "topic";
  private static final String KEY = "key";
  private static final String VALUE = "value";

  private static final Entry ENTRY = Entry.builder(TERM, EVENT)
      .build();
  private static final RpcEntry RPC_ENTRY = RpcEntry.newBuilder().setTerm(TERM).setEvent(EVENT).build();

  @Mock ConverterRegistry mConverterRegistry;

  private EntryConverter mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    mUnitUnderTest = new EntryConverter();
  }

  @Test
  public void should_convert_Entry() {
    // Given / When
    final RpcEntry response = mUnitUnderTest.convert(ENTRY, mConverterRegistry);

    // Then
    assertEquals(RPC_ENTRY, response);
  }

  @Test
  public void should_convert_RpcEntry() {
    // Given / When
    final Entry response = mUnitUnderTest.convert(RPC_ENTRY, mConverterRegistry);

    // Then
    assertEquals(ENTRY, response);
    verifyNoMoreInteractions(mConverterRegistry);
  }
}
