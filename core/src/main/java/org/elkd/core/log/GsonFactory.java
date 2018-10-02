package org.elkd.core.log;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elkd.shared.json.GsonUtils;

public final class GsonFactory {
  private static GsonFactory mGsonFactory;
  private static Gson mGson;
  static synchronized GsonFactory getInstance() {
    if (mGsonFactory == null) {
      mGsonFactory = new GsonFactory();
    }

    return mGsonFactory;
  }

  private GsonFactory() {
    mGson = configureGson();
  }

  public Gson getGson() {
    return mGson;
  }

  private Gson configureGson() {
    final GsonBuilder gsonBuilder = new GsonBuilder();

    registerSerializers(gsonBuilder);
    registerDeserializers(gsonBuilder);

    return gsonBuilder.create();
  }

  private void registerSerializers(final GsonBuilder gsonBuilder) {
    GsonUtils.registerSerializers(gsonBuilder, ImmutableMap.of(
        Event.class, new Event.Serializer(),
        Document.class, new Document.Serializer<>(Object.class)
    ));
  }

  private void registerDeserializers(final GsonBuilder gsonBuilder) {
    GsonUtils.registerDeserializers(gsonBuilder, ImmutableMap.of(
        Event.class, new Event.Deserializer(),
        Document.class, new Document.Deserializer<>(Object.class)
    ));
  }
}
