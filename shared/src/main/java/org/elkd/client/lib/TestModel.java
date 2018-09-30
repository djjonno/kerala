package org.elkd.client.lib;

import com.google.common.base.Preconditions;

public class TestModel {
  private String mName;

  public TestModel(final String name) {
    mName = Preconditions.checkNotNull(name, "name");
  }

  public String getName() {
    return mName;
  }

  @Override
  public String toString() {
    return "TestModel{" +
        "mName='" + mName + '\'' +
        '}';
  }
}
