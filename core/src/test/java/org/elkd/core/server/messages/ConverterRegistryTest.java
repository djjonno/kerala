package org.elkd.core.server.messages;

import com.google.common.collect.ImmutableMap;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.server.RpcRequestVotesResponse;
import org.elkd.core.server.messages.exceptions.ConverterException;
import org.elkd.core.server.messages.exceptions.ConverterNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ConverterRegistryTest {

  @Mock Converter mConverter;
  @Mock Object mSource;
  @Mock Object mTarget;
  Class<AppendEntriesResponse> mTargetType = AppendEntriesResponse.class;

  private ConverterRegistry mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new ConverterRegistry(ImmutableMap.of(
        mTargetType, mConverter
    ));

    doReturn(mTarget)
        .when(mConverter)
        .convert(mTargetType, mSource);
  }

  @Test
  public void should_transform_object() {
    // Given / When
    final Object target = mUnitUnderTest.transform(mTargetType, mSource);

    // Then
    assertSame(mTarget, target);
    verify(mConverter).convert(mTargetType, mSource);
  }

  @Test(expected = ConverterNotFoundException.class)
  public void should_throw_exception_when_no_adapter_found() {
    // Given
    mUnitUnderTest = new ConverterRegistry(ImmutableMap.of());

    // When
    mUnitUnderTest.transform(mTargetType, mSource);


    // Then - exception thrown
  }

  @Test(expected = ConverterException.class)
  public void should_throw_converter_exception() {
    // Given
    doThrow(new ConverterException("failed to convert"))
        .when(mConverter)
        .convert(mTargetType, mSource);

    // When
    mUnitUnderTest.transform(mTargetType, mSource);

    // Then - exception thrown
  }
}
