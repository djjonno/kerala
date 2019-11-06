package org.kerala.shared.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GsonUtilsTest {
  private static final String STRING_KEY = "key1";
  private static final String LONG_KEY = "key2";
  private static final String STRING_VAL = "1234";
  private static final Long LONG_VAL = 1234L;

  @Test
  public void builder_should_serialize_element() {
    // Given
    final JsonElement expected = createJsonWithStringProperty();

    // When
    final JsonElement json = GsonUtils.builder()
        .withStringElement(STRING_KEY, STRING_VAL)
        .build();

    assertEquals(expected, json);
  }

  @Test
  public void builder_should_serialize_with_long_element() {
    // Given
    final JsonElement expected = createJsonWithLongProperty();

    // When
    final JsonElement json = GsonUtils.builder()
        .withLongElement(LONG_KEY, LONG_VAL)
        .build();

    // Then
    assertEquals(expected, json);
  }

  @Test
  public void builder_should_serialize_with_json_element() {
    // Given
    final JsonElement element = createJsonWithJsonElement();
    final JsonObject expected = new JsonObject();
    expected.add(STRING_KEY, element);

    // When
    final JsonElement json = GsonUtils.builder()
        .withJsonElement(STRING_KEY, element)
        .build();

    // Then
    assertEquals(expected, json);
  }

  @Test
  public void parser_should_parse_string_element() {
    // Given
    final JsonElement jsonElement = createJsonWithStringProperty();

    // When
    final String property = GsonUtils.parser(jsonElement)
        .getString(STRING_KEY);

    // Then
    assertEquals(STRING_VAL, property);
  }

  @Test
  public void parser_should_parse_string_object() {
    // Given
    final JsonObject jsonObject = (JsonObject) createJsonWithStringProperty();

    // When
    final String property = GsonUtils.parser(jsonObject)
        .getString(STRING_KEY);

    // Then
    assertEquals(STRING_VAL, property);
  }

  @Test
  public void parser_should_parse_long_element() {
    // Given
    final JsonElement jsonElement = createJsonWithLongProperty();

    // When
    final Long property = GsonUtils.parser(jsonElement)
        .getLong(LONG_KEY);

    // Then
    assertEquals(LONG_VAL, property);
  }

  @Test
  public void parser_should_parse_long_object() {
    // Given
    final JsonObject jsonObject = (JsonObject) createJsonWithLongProperty();

    // When
    final Long property = GsonUtils.parser(jsonObject)
        .getLong(LONG_KEY);

    // Then
    assertEquals(LONG_VAL, property);
  }

  @Test
  public void parser_should_parse_json_element() {
    // Given
    final JsonElement jsonElement = createJsonWithJsonElement();

    // When
    final String stringProperty = GsonUtils.parser(jsonElement)
        .getString(STRING_KEY);
    final Long longProperty = GsonUtils.parser(jsonElement)
        .getLong(LONG_KEY);

    // Then
    assertEquals(STRING_VAL, stringProperty);
    assertEquals(LONG_VAL, longProperty);
  }

  @Test
  public void parser_should_parse_json_object() {
    // Given
    final JsonObject jsonElement = (JsonObject) createJsonWithJsonElement();

    // When
    final String stringProperty = GsonUtils.parser(jsonElement)
        .getString(STRING_KEY);
    final Long longProperty = GsonUtils.parser(jsonElement)
        .getLong(LONG_KEY);

    // Then
    assertEquals(STRING_VAL, stringProperty);
    assertEquals(LONG_VAL, longProperty);
  }

  private JsonElement createJsonWithStringProperty() {
    final JsonElement element = new JsonObject();
    ((JsonObject) element).addProperty(STRING_KEY, STRING_VAL);
    return element;
  }

  private JsonElement createJsonWithLongProperty() {
    final JsonElement element = new JsonObject();
    ((JsonObject) element).addProperty(LONG_KEY, LONG_VAL);
    return element;
  }

  private JsonElement createJsonWithJsonElement() {
    final JsonElement element = new JsonObject();
    ((JsonObject) element).addProperty(STRING_KEY, STRING_VAL);
    ((JsonObject) element).addProperty(LONG_KEY, LONG_VAL);
    return element;
  }
}
