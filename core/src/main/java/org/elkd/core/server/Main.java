package org.elkd.core.server;

import org.elkd.core.log.Entry;

import java.util.logging.Logger;

public class Main {
  private static final Logger log = Logger.getLogger(Main.class.getName());

  public static void main(final String[] args) {
    log.info("running elkd");
    final Entry entry = Entry.builder("amznStock")
        .value("price", 123)
        .value("close", 122)
        .value("ask", 124)
        .build();

    System.out.println(entry);
  }
}
