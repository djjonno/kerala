package org.kerala.shared.io;

public class File {
  private File() { }

  public static String join(final String directory, final String file) {
    return new java.io.File(directory, file).getPath();
  }
}
