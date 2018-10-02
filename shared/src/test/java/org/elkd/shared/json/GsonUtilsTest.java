package org.elkd.shared.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class GsonUtilsTest {
  private static final String KEY = "key";
  private static final String VAL = "val";

  @Test
  public void should_serialize_element() {
    // Given
    final JsonObject expected = new JsonObject();
    expected.addProperty(KEY, VAL);

    // When
    final JsonElement json = GsonUtils.builder()
        .withStringElement(KEY, VAL)
        .build();

    assertEquals(expected, json);
  }
}