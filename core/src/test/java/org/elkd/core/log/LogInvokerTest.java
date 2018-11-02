package org.elkd.core.log;

import com.google.common.collect.ImmutableList;
import org.elkd.core.consensus.messages.Entry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class LogInvokerTest {

  @Mock
  Entry mEntry1;
  @Mock Entry mEntry2;
  @Mock Log<Entry> mLog;
  @Mock LogChangeListener<Entry> mListener1;
  @Mock LogChangeListener<Entry> mListener2;
  @Mock CommitResult<Entry> mCommitResult;

  private LogInvoker<Entry> mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    mUnitUnderTest = new LogInvoker<>(mLog);
  }

  @Test
  public void should_delegate_to_log_on_append() {
    // Given / When
    mUnitUnderTest.append(mEntry1);

    // Then
    verify(mLog).append(mEntry1);
  }

  @Test
  public void should_delegate_to_log_on_append_list() {
    // Given / When
    final ImmutableList<Entry> entries = getEntries();
    mUnitUnderTest.append(entries);

    // Then
    verify(mLog).append(entries);
  }

  @Test
  public void should_delegate_to_log_on_read() {
    // Given
    final int index = 0;
    doReturn(mEntry1)
        .when(mLog)
        .read(index);

    // When
    final Entry entry = mUnitUnderTest.read(index);

    // Then
    verify(mLog).read(index);
    assertSame(mEntry1, entry);
  }

  @Test
  public void should_delegate_to_log_on_read_series() {
    // Given
    final ImmutableList<Entry> expected = getEntries();
    final int from = 0;
    final int to = 1;
    doReturn(expected)
        .when(mLog)
        .read(from, to);

    // When
    final List<Entry> entries = mUnitUnderTest.read(from, to);

    // Then
    verify(mLog).read(from, to);
    assertSame(expected, entries);
  }

  @Test
  public void should_delegate_to_log_on_commit() {
    // Given
    final List<Entry> entries = getEntries();
    final long t1 = mUnitUnderTest.append(entries);
    doReturn(mCommitResult)
        .when(mLog)
        .commit(t1);

    // When
    final CommitResult<Entry> commit = mUnitUnderTest.commit(t1);

    // Then
    verify(mLog).commit(t1);
    assertSame(mCommitResult, commit);
  }

  @Test
  public void should_delegate_to_log_on_revert() {
    // Given
    final long index = mUnitUnderTest.append(mEntry1);

    // When
    mUnitUnderTest.revert(index);

    // Then
    verify(mLog).revert(index);
  }

  @Test
  public void should_deregister_listener() {
    // Given
    final ImmutableList<Entry> entries = getEntries();
    mUnitUnderTest.registerListener(mListener1);
    mUnitUnderTest.deregisterListener(mListener1);

    // When
    mUnitUnderTest.append(entries);

    // Then
    verify(mListener1, never()).onAppend(entries);
  }

  @Test
  public void should_notify_listeners_on_append() {
    // Given
    final ImmutableList<Entry> entries = getEntries();
    mUnitUnderTest.registerListener(mListener1);
    mUnitUnderTest.registerListener(mListener2);

    // When
    mUnitUnderTest.append(entries);

    // Then
    verify(mListener1).onAppend(entries);
    verify(mListener2).onAppend(entries);
  }

  @Test
  public void should_notify_listeners_on_commit() {
    // Given
    final ImmutableList<Entry> entries = getEntries();
    final int index = entries.size() - 1;
    doReturn(new CommitResult<>(entries, index))
        .when(mLog)
        .commit(index);
    mUnitUnderTest.registerListener(mListener1);
    mUnitUnderTest.registerListener(mListener2);

    // When
    mUnitUnderTest.commit(index);

    // Then
    verify(mListener1).onCommit(entries);
    verify(mListener2).onCommit(entries);
  }

  private ImmutableList<Entry> getEntries() {
    return ImmutableList.of(mEntry1, mEntry2);
  }
}
