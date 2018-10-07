package org.elkd.core.log;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

public class LogTest {
  @Mock Entry mEntry1;
  @Mock Entry mEntry2;
  @Mock Entry mEntry3;

  private Log<Entry> mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    mUnitUnderTest = new InMemoryLog();
  }

  @Test
  public void should_start_indexing_from_zero() {
    // Given / When
    final long firstIndex = 0;
    final long index = mUnitUnderTest.append(mEntry1);

    // Then
    assertEquals(firstIndex, index);
  }

  @Test
  public void should_increment_index_when_appended() {
    // Given
    final long secondTransaction = 1;
    mUnitUnderTest.append(mEntry1);

    // When
    final long t2 = mUnitUnderTest.append(mEntry2);

    // Then
    assertEquals(secondTransaction, t2);
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_exception_when_accessing_uncommitted_entry() {
    // Given
    final long t1 = mUnitUnderTest.append(mEntry1);

    // When
    mUnitUnderTest.read(t1);

    // Then - exception thrown
  }

  @Test
  public void should_return_corresponding_entry_when_read() {
    // Given
    final long t1 = mUnitUnderTest.append(mEntry1);
    mUnitUnderTest.commit(t1);

    // When
    final Entry entry = mUnitUnderTest.read(t1);

    // Then
    assertEquals(mEntry1, entry);
  }

  @Test
  public void should_return_entry_series_in_order() {
    // Given
    final long t1 = mUnitUnderTest.append(mEntry1);
    final long t2 = mUnitUnderTest.append(mEntry2);
    final long t3 = mUnitUnderTest.append(mEntry3);
    mUnitUnderTest.commit(t3);

    // When
    List<Entry> entries = mUnitUnderTest.read(t1, t3);

    // Then
    assertEquals(entries.get((int) t1), mEntry1);
    assertEquals(entries.get((int) t2), mEntry2);
    assertEquals(entries.get((int) t3), mEntry3);
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_exception_committing_out_of_range_index() {
    // Given
    final long outOfRange = 1;
    mUnitUnderTest.append(mEntry1);

    // When
    mUnitUnderTest.commit(outOfRange);

    // Then - exception thrown
  }

  @Test
  public void should_overwrite_uncommitted_entry() {
    // Given
    final long t1 = mUnitUnderTest.append(mEntry1);
    final long t2 = mUnitUnderTest.append(mEntry2);
    mUnitUnderTest.commit(t1);

    // When
    mUnitUnderTest.revert(t2);
    final long t3 = mUnitUnderTest.append(mEntry3);
    mUnitUnderTest.commit(t3);
    final Entry entry = mUnitUnderTest.read(t3);

    // Then
    assertEquals(t2, t3);
    assertEquals(mEntry3, entry);
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_exception_when_reverting_committed_entry() {
    // Given
    final long t1 = mUnitUnderTest.append(mEntry1);

    // When
    mUnitUnderTest.commit(t1);
    mUnitUnderTest.revert(t1);

    // Then - exception thrown
  }
}
