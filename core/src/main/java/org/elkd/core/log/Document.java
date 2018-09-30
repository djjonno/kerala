package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Document<V> {
  private final Map<String, V> mEntries;

  private Document(final ImmutableMap<String, V> values) {
    mEntries = Preconditions.checkNotNull(values, "values");
  }

  static <V> Builder<V> builder() {
    return new Builder<>();
  }

  static class Builder<V> {
    private final Map<String, V> mEntries = new HashMap<>();

    Builder<V> put(final String key, final V value) {
      mEntries.put(key, value);
      return this;
    }

    Document<V> build() {
      return new Document<V>(ImmutableMap.copyOf(mEntries));
    }
  }

  ImmutableMap<String, V> getEntries() {
    return ImmutableMap.copyOf(mEntries);
  }

  @Override
  public String toString() {
    return mEntries.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Document<?> document = (Document<?>) o;
    return Objects.equals(mEntries, document.mEntries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mEntries);
  }

  public static class Serializer<V> implements JsonSerializer<Document<V>> {
    static final String DOCUMENT_ENTRIES_MAP_KEY = "entries";
    private final Class<String> mKeyType;
    private final Class<V> mValueType;

    public Serializer(final Class<V> valueType) {
      mKeyType = String.class;
      mValueType = Preconditions.checkNotNull(valueType, "valueType");
    }

    @Override
    public JsonElement serialize(final Document<V> document, final Type type, final JsonSerializationContext context) {
      final JsonObject jsonObject = new JsonObject();
      jsonObject.add(
          DOCUMENT_ENTRIES_MAP_KEY,
          serializeEntries(document.mEntries, context)
      );
      return jsonObject;
    }

    private JsonElement serializeEntries(final Map<String, V> entries, final JsonSerializationContext context) {
      final JsonObject jsonObject = new JsonObject();

      for (final Map.Entry<String, V> entry : entries.entrySet()) {
        final JsonElement keyJson = context.serialize(entry.getKey(), mKeyType);
        final JsonElement valueJson = context.serialize(entry.getValue(), mValueType);

        jsonObject.add(keyJson.getAsString(), valueJson);
      }

      return jsonObject;
    }
  }

  public static class Deserializer<V> implements JsonDeserializer<Document<V>> {
    private final Class<String> mKeyType;
    private final Class<V> mValueType;

    public Deserializer(final Class<V> valueType) {
      mKeyType = String.class;
      mValueType = Preconditions.checkNotNull(valueType, "valueType");
    }

    @Override
    public Document<V> deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
      final JsonObject jsonObject = json.getAsJsonObject();
      return new Document<>(deserializeEntries(jsonObject.get(Serializer.DOCUMENT_ENTRIES_MAP_KEY), context));
    }

    private ImmutableMap<String, V> deserializeEntries(final JsonElement jsonElement, final JsonDeserializationContext context) {
      ImmutableMap.Builder<String, V> map = new ImmutableMap.Builder<>();

      for (final Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
        final String key = context.deserialize(new JsonPrimitive(entry.getKey()), mKeyType);
        final V value = context.deserialize(entry.getValue(), mValueType);
        map.put(key, value);
      }

      return map.build();
    }
  }
}
