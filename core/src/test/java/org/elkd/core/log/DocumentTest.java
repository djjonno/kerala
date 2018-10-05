package org.elkd.core.log;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.elkd.core.server.GsonFactory;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DocumentTest {
  private static final String KEY_1 = "key1";
  private static final String VAL_1 = "val1";
  private static final String KEY_2 = "key2";
  private static final String VAL_2 = "val2";
  private static final Gson GSON = GsonFactory.getInstance().getGson();

  private Document.Builder<Object> mDocumentBuilder;

  @Before
  public void setUp() {
    mDocumentBuilder = Document.builder();
  }

  @Test
  public void should_add_key_value_to_document() {
    // Given
    mDocumentBuilder.put(KEY_1, VAL_1);

    // When
    final Document<Object> doc = mDocumentBuilder.build();

    // Then
    assertEquals(ImmutableMap.of(KEY_1, VAL_1), doc.getEntries());
  }

  @Test
  public void should_add_multiple_key_values_to_document() {
    // Given
    mDocumentBuilder
        .put(KEY_1, VAL_1)
        .put(KEY_2, VAL_2);

    // When
    final Document<Object> doc = mDocumentBuilder.build();

    // Then
    assertEquals(ImmutableMap.of(KEY_1, VAL_1, KEY_2, VAL_2), doc.getEntries());
  }

  @Test
  public void should_serialize_document_with_entries_key() {
    // Given
    mDocumentBuilder
        .put(KEY_1, VAL_1)
        .put(KEY_2, VAL_2);
    final Document<Object> doc = mDocumentBuilder.build();

    // When
    final String serialized = GSON.toJson(doc);
    final Type docType = new TypeToken<Document<Object>>() { }.getType();
    final Document<Object> deSerializedDoc = GSON.fromJson(serialized, docType);

    // Then
    assertNotNull(serialized);
    assertNotNull(deSerializedDoc);
    assertEquals(doc, deSerializedDoc);
  }
}
