package org.elkd.core.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonFactory {
  private static GsonFactory mGsonFactory;
  private static Gson mGson;

  private GsonFactory() {
    mGson = configureGson();
  }

  public static synchronized GsonFactory getInstance() {
    if (mGsonFactory == null) {
      mGsonFactory = new GsonFactory();
    }

    return mGsonFactory;
  }

  public Gson getGson() {
    return mGson;
  }

  private Gson configureGson() {
    final GsonBuilder gsonBuilder = new GsonBuilder();

    return gsonBuilder.create();
  }
}
