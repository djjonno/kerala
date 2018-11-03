package org.elkd.core.server.converters;

import com.google.common.collect.ImmutableList;

import java.util.List;

class ConverterRegistryFactory {
  private ConverterRegistryFactory() {
  }

  static List<Converter> getConverters() {
    return ImmutableList.of(
        /* Register default converters here */
        new AppendEntriesRequestConverter(),
        new AppendEntriesResponseConverter(),
        new RequestVoteRequestConverter(),
        new RequestVoteResponseConverter(),
        new EntryConverter(),
        new StateMachineCommandConverter()
    );
  }
}
