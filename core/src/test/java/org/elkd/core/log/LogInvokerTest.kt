package org.elkd.core.log

import com.google.common.collect.ImmutableList
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.ds.Log
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class LogInvokerTest {

  @Mock lateinit var entry1: Entry
  @Mock lateinit var entry2: Entry
  @Mock lateinit var log: Log<Entry>
  @Mock lateinit var listener1: LogChangeListener<Entry>
  @Mock lateinit var listener2: LogChangeListener<Entry>
  @Mock lateinit var commitResult: CommitResult<Entry>

  private lateinit var unitUnderTest: LogInvoker<Entry>

  private val entries: ImmutableList<Entry>
    get() = ImmutableList.of(entry1, entry2)

  @Before
  @Throws(Exception::class)
  fun setup() {
    MockitoAnnotations.initMocks(this)
    unitUnderTest = LogInvoker(log)
  }

  @Test
  fun should_delegate_to_log_on_append() {
    // Given / When
    unitUnderTest.append(entry1)

    // Then
    verify<Log<Entry>>(log).append(entry1)
  }

  @Test
  fun should_delegate_to_log_on_append_with_index() {
    // Given / When
    val index = 0
    unitUnderTest.append(index.toLong(), entry1)

    // Then
    verify<Log<Entry>>(log).append(index.toLong(), entry1)
  }

  @Test
  fun should_return_index_from_delegate_log_on_append() {
    // Given
    val index: Long = 10
    doReturn(index)
        .`when`<Log<Entry>>(log)
        .append(entry1)

    // When
    val append = unitUnderTest.append(entry1)

    // Then
    assertEquals(index, append)
  }

  @Test
  fun should_return_index_from_delegate_log_on_append_with_index() {
    // Given
    val index: Long = 10
    val requestedIndex = 5
    doReturn(index)
        .`when`<Log<Entry>>(log)
        .append(requestedIndex.toLong(), entry1)

    // When
    val append = unitUnderTest.append(requestedIndex.toLong(), entry1)

    // Then
    assertEquals(index, append)
  }

  @Test
  fun should_notify_listeners_on_append_with_index() {
    // Given
    unitUnderTest.registerListener(listener1)

    // When
    unitUnderTest.append(0, entry1)

    // Then
    verify<LogChangeListener<Entry>>(listener1).onAppend(0, entry1)
  }

  @Test
  fun should_delegate_to_log_on_read() {
    // Given
    val index = 0
    doReturn(entry1)
        .`when`<Log<Entry>>(log)
        .read(index.toLong())

    // When
    val entry = unitUnderTest.read(index.toLong())

    // Then
    verify<Log<Entry>>(log).read(index.toLong())
    assertSame(entry1, entry)
  }

  @Test
  fun should_delegate_to_log_on_read_series() {
    // Given
    val expected = entries
    val from = 0
    val to = 1
    doReturn(expected)
        .`when`<Log<Entry>>(log)
        .read(from.toLong(), to.toLong())

    // When
    val entries = unitUnderTest.read(from.toLong(), to.toLong())

    // Then
    verify<Log<Entry>>(log).read(from.toLong(), to.toLong())
    assertSame(expected, entries)
  }

  @Test
  fun should_delegate_to_log_on_commit() {
    // Given
    val t1 = unitUnderTest.append(entry1)
    doReturn(commitResult)
        .`when`<Log<Entry>>(log)
        .commit(t1)

    // When
    val commit = unitUnderTest.commit(t1)

    // Then
    verify<Log<Entry>>(log).commit(t1)
    assertSame(commitResult, commit)
  }

  @Test
  fun should_delegate_to_log_on_revert() {
    // Given
    val index = unitUnderTest.append(entry1)

    // When
    unitUnderTest.revert(index)

    // Then
    verify<Log<Entry>>(log).revert(index)
  }

  @Test
  fun should_deregister_listener() {
    // Given
    unitUnderTest.registerListener(listener1)
    unitUnderTest.deregisterListener(listener1)

    // When
    unitUnderTest.append(entry1)

    // Then
    verify<LogChangeListener<Entry>>(listener1, never()).onAppend(0, entry1)
  }

  @Test
  fun should_notify_listeners_on_append() {
    // Given
    unitUnderTest.registerListener(listener1)
    unitUnderTest.registerListener(listener2)

    // When
    unitUnderTest.append(entry1)
    unitUnderTest.append(entry2)

    // Then
    verify<LogChangeListener<Entry>>(listener1).onAppend(0, entry1)
    verify<LogChangeListener<Entry>>(listener2).onAppend(0, entry2)
  }

  @Test
  fun should_notify_listeners_on_commit() {
    // Given
    val index = (entries.size - 1).toLong()
    doReturn(CommitResult(entries, index))
        .`when`<Log<Entry>>(log)
        .commit(index)
    unitUnderTest.registerListener(listener1)
    unitUnderTest.registerListener(listener2)

    // When
    unitUnderTest.commit(index)

    // Then
    entries.forEachIndexed { i, entry -> verify<LogChangeListener<Entry>>(listener1).onCommit(i.toLong(), entry) }
    entries.forEachIndexed { i, entry -> verify<LogChangeListener<Entry>>(listener2).onCommit(i.toLong(), entry) }
  }

  @Test
  fun should_return_commitIndex_from_delegate() {
    // Given
    val commit: Long = 10
    doReturn(commit)
        .`when`<Log<Entry>>(log)
        .commitIndex

    // When
    val commitIndex = unitUnderTest.commitIndex

    // Then
    assertEquals(commit, commitIndex)
  }

  @Test
  fun should_return_lastIndex_from_delegate() {
    // Given
    val expectedIndex: Long = 10
    doReturn(expectedIndex)
        .`when`<Log<Entry>>(log)
        .lastIndex

    // When
    val lastIndex = unitUnderTest.lastIndex

    // Then
    assertEquals(expectedIndex, lastIndex)
  }
}
