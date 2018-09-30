package org.elkd.core.server;

import com.google.common.collect.ImmutableMap;
import org.elkd.core.log.Event;

import java.util.logging.Logger;

public class Main {
  private static final Logger log = Logger.getLogger(Main.class.getName());

  public static void main(final String[] args) {
    log.info("running elkd");
    final Event event = Event.builder("amznStock")
        .value("price", 123)
        .value("close", 122)
        .value("ask", 124)
        .build();

    System.out.println(event);
  }
}
