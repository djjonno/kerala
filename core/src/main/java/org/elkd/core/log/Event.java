package org.elkd.core.log;

import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.elkd.shared.json.GsonUtils;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;

public final class Event {
  private String mType;
  private Long mTime;
  private Document<Object> mDocument;

  private Event(final String type, final Document<Object> document) {
    this(type, document, DateTime.now().getMillis());
  }

  private Event(final String type, final Document<Object> document, final Long time) {
    mType = Preconditions.checkNotNull(type, "type");
    mDocument = Preconditions.checkNotNull(document, "document");
    mTime = Preconditions.checkNotNull(time);
  }

  public static Builder builder(final String type) {
    return new Builder(type);
  }

  public static class Builder {
    private String mType;
    private Document.Builder<Object> mDocumentBuilder;

    Builder(final String type) {
      mType = Preconditions.checkNotNull(type, "type");
      mDocumentBuilder = Document.builder();
    }

    public Builder value(final String key, final Object value) {
      mDocumentBuilder.put(key, value);
      return this;
    }

    public Event build() {
      return new Event(
          mType,
          mDocumentBuilder.build()
      );
    }
  }

  public String getType() {
    return mType;
  }

  @Nullable
  public Object getValue(final String key) {
    return mDocument.getEntries().get(key);
  }

  public Long getTime() {
    return mTime;
  }

  @Override
  public String toString() {
    return "Event{" +
        "mType='" + mType + '\'' +
        ", mTime=" + mTime +
        ", mDocument=" + mDocument +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Event event = (Event) o;
    return Objects.equals(mType, event.mType) &&
        Objects.equals(mDocument, event.mDocument);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mType, mTime, mDocument);
  }

  public static class Serializer implements JsonSerializer<Event> {
    static String TYPE_KEY = "type";
    static String TIME_KEY = "time";
    static String DOCUMENT_KEY = "document";
    static Type DOCUMENT_CONTEXT_TYPE = new TypeToken<Document<Object>>() { }.getType();

    @Override
    public JsonElement serialize(final Event event, final Type type, final JsonSerializationContext context) {
      return GsonUtils.builder()
          .withStringElement(TYPE_KEY, event.mType)
          .withLongElement(TIME_KEY, event.mTime)
          .withJsonElement(DOCUMENT_KEY, context.serialize(event.mDocument, DOCUMENT_CONTEXT_TYPE))
          .build();
    }
  }

  public static class Deserializer implements JsonDeserializer<Event> {
    @Override
    public Event deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
      final GsonUtils.JsonObjectParser parser = GsonUtils.parser(json);

      final String eventType = parser.getString(Serializer.TYPE_KEY);
      final Long time = parser.getLong(Serializer.TIME_KEY);
      final JsonElement documentJsonElement = parser.getElement(Serializer.DOCUMENT_KEY);
      final Document<Object> eventDocument = context.deserialize(documentJsonElement, Serializer.DOCUMENT_CONTEXT_TYPE);

      return new Event(eventType, eventDocument, time);
    }
  }
}
