package org.elkd.shared.json;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public class GsonUtils {

  private GsonUtils() { }

  public static JsonObjectBuilder builder() {
    return new JsonObjectBuilder();
  }

  public static class JsonObjectBuilder {
    private final JsonObject mJsonObject;

    JsonObjectBuilder() {
      mJsonObject = new JsonObject();
    }

    public JsonObjectBuilder withStringElement(final String key, final String value) {
      Preconditions.checkNotNull(key, "key");
      Preconditions.checkNotNull(value, "value");

      mJsonObject.addProperty(key, value);
      return this;
    }

    public JsonObjectBuilder withLongElement(final String key, final Long value) {
      Preconditions.checkNotNull(key, "key");
      Preconditions.checkNotNull(value, "value");

      mJsonObject.addProperty(key, value);
      return this;
    }

    public JsonObjectBuilder withJsonElement(final String key, final JsonElement jsonElement) {
      Preconditions.checkNotNull(key, "key");
      Preconditions.checkNotNull(jsonElement, "jsonElement");

      mJsonObject.add(key, jsonElement);
      return this;
    }

    public JsonObject build() {
      return mJsonObject;
    }
  }

  public static JsonObjectParser parser(final JsonElement jsonElement) {
    Preconditions.checkNotNull(jsonElement, "jsonElement");

    if (!jsonElement.isJsonObject()) {
      throw new JsonParseException("Expected JsonObject but received: " + jsonElement.getClass());
    }

    return parser(jsonElement.getAsJsonObject());
  }

  public static JsonObjectParser parser(final JsonObject jsonObject) {
    Preconditions.checkNotNull(jsonObject, "jsonObject");

    return new JsonObjectParser(jsonObject);
  }

  public static class JsonObjectParser {
    private final JsonObject mJsonObject;

    private JsonObjectParser(final JsonObject jsonObject) {
      this.mJsonObject = jsonObject;
    }

    public String getString(final String key) {
      Preconditions.checkNotNull(key, "key");

      return getElement(key).getAsString();
    }

    public Long getLong(final String key) {
      Preconditions.checkNotNull(key, "key");

      return getElement(key).getAsLong();
    }

    public JsonElement getElement(final String key) {
      Preconditions.checkNotNull(key, "key");

      final JsonElement jsonElement = mJsonObject.get(key);

      if (jsonElement == null) {
        throw new JsonParseException("Key \"" + key + "\" not found.");
      }

      return jsonElement;
    }

    public JsonObject getRootObject() {
      return mJsonObject;
    }
  }

  public static GsonBuilder registerSerializers(final GsonBuilder builder, final ImmutableMap<Type, JsonSerializer> serializers) {
    for (final Map.Entry<Type, JsonSerializer> typeAdapter : serializers.entrySet()) {
      builder.registerTypeAdapter(typeAdapter.getKey(), typeAdapter.getValue());
    }

    return builder;
  }

  public static GsonBuilder registerDeserializers(final GsonBuilder builder, final ImmutableMap<Type, JsonDeserializer> deserializers) {
    for (final Map.Entry<Type, JsonDeserializer> typeAdapter : deserializers.entrySet()) {
      builder.registerTypeAdapter(typeAdapter.getKey(), typeAdapter.getValue());
    }

    return builder;
  }
}
