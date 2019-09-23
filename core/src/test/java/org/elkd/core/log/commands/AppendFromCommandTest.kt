package org.elkd.core.log.commands

import com.nhaarman.mockitokotlin2.*
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.ds.Log
import org.elkd.core.log.LogChangeReason
import org.elkd.core.log.exceptions.NonSequentialAppendException
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

class AppendFromCommandTest {

  @Mock private lateinit var log: Log<Entry>

  private var entry1: Entry = Entry.builder(0, "topic").build()
  private var entry2: Entry = Entry.builder(0, "topic").build()

  private lateinit var entries: List<Entry>

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    entries = listOf(entry1, entry2)

    doReturn(-1L)
        .`when`(log)
        .lastIndex
  }

  @Test
  fun should_append_entries() {
    // Given
    val command = AppendFromCommand.build(0, entries, LogChangeReason.REPLICATION)

    // When
    command.execute(log)

    // Then
    argumentCaptor<Entry>().apply {
      verify(log, times(2)).append(capture())
      assertEquals(allValues, entries)
    }
  }

  @Test
  fun should_revert_entries_when_not_from_end_of_log() {
    // Given
    val from = 1L
    whenever(log.lastIndex).thenReturn(from + 1)
    val command = AppendFromCommand.build(from, entries, LogChangeReason.REPLICATION)

    // Given
    command.execute(log)

    // Then
    verify(log).revert(from)
    argumentCaptor<Entry>().apply {
      verify(log, times(2)).append(capture())
      assertEquals(allValues, entries)
    }
  }

  @Test(expected = NonSequentialAppendException::class)
  fun should_throw_exception_when_not_from_index_not_continuous() {
    // Given
    val from = 2L
    whenever(log.lastIndex).thenReturn(0)
    val command = AppendFromCommand.build(from, entries, LogChangeReason.REPLICATION)

    // When
    command.execute(log)

    // Then - exception thrown
  }
}
