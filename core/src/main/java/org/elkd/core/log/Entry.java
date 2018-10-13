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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;

public class Entry {
  private String mEvent;
  private Long mTime;
  private Document<Object> mDocument; // TODO: change Document to be a list of StateMachine commands

  private Entry(@Nonnull final String type, @Nonnull final Document<Object> document) {
    this(type, document, DateTime.now().getMillis());
  }

  private Entry(@Nonnull final String type, @Nonnull final Document<Object> document, @Nonnull final Long time) {
    mEvent = Preconditions.checkNotNull(type, "type");
    mDocument = Preconditions.checkNotNull(document, "document");
    mTime = Preconditions.checkNotNull(time);
  }

  public static Builder builder(final String type) {
    return new Builder(type);
  }

  public static class Builder {
    private String mType;
    private Document.Builder<Object> mDocumentBuilder;

    Builder(@Nonnull final String type) {
      mType = Preconditions.checkNotNull(type, "type");
      mDocumentBuilder = Document.builder();
    }

    public Builder value(final String key, final Object value) {
      mDocumentBuilder.put(key, value);
      return this;
    }

    public Entry build() {
      return new Entry(
          mType,
          mDocumentBuilder.build()
      );
    }
  }

  public String getType() {
    return mEvent;
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
    return "Entry{" +
        "mEvent='" + mEvent + '\'' +
        ", mTime=" + mTime +
        ", mDocument=" + mDocument +
        '}';
  }

  @Override
  public boolean equals(final Object rhs) {
    if (this == rhs) {
      return true;
    }
    if (rhs == null || getClass() != rhs.getClass()) {
      return false;
    }
    final Entry entry = (Entry) rhs;
    return Objects.equals(mEvent, entry.mEvent) &&
        Objects.equals(mDocument, entry.mDocument);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mEvent, mTime, mDocument);
  }

  public static class Serializer implements JsonSerializer<Entry> {
    private static final String EVENT_KEY = "event";
    private static final String TIME_KEY = "time";
    private static final String DOCUMENT_KEY = "document";
    private static final Type DOCUMENT_CONTEXT_TYPE = new TypeToken<Document<Object>>() { }.getType();

    @Override
    public JsonElement serialize(final Entry entry, final Type type, final JsonSerializationContext context) {
      return GsonUtils.builder()
          .withStringElement(EVENT_KEY, entry.mEvent)
          .withLongElement(TIME_KEY, entry.mTime)
          .withJsonElement(DOCUMENT_KEY, context.serialize(entry.mDocument, DOCUMENT_CONTEXT_TYPE))
          .build();
    }
  }

  public static class Deserializer implements JsonDeserializer<Entry> {
    @Override
    public Entry deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
      final GsonUtils.JsonObjectParser parser = GsonUtils.parser(json);

      final String eventType = parser.getString(Serializer.EVENT_KEY);
      final Long time = parser.getLong(Serializer.TIME_KEY);
      final JsonElement documentJsonElement = parser.getElement(Serializer.DOCUMENT_KEY);
      final Document<Object> eventDocument = context.deserialize(documentJsonElement, Serializer.DOCUMENT_CONTEXT_TYPE);

      return new Entry(eventType, eventDocument, time);
    }
  }
}
