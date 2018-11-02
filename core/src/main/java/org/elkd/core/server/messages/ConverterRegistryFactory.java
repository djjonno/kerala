package org.elkd.core.server.messages;

import com.google.common.collect.ImmutableMap;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.server.RpcAppendEntriesRequest;
import org.elkd.core.server.RpcAppendEntriesResponse;
import org.elkd.core.server.RpcEntry;
import org.elkd.core.server.RpcStateMachineCommand;

class ConverterRegistryFactory {
  private ConverterRegistryFactory() {
  }

  static ImmutableMap<Class, Converter> getConverters() {
    return new ImmutableMap.Builder<Class, Converter>()
        .put(RpcAppendEntriesRequest.class,  null)
        .put(RpcAppendEntriesResponse.class, null)

        .put(AppendEntriesRequest.class,     null)
        .put(AppendEntriesResponse.class,    null)

        .put(RpcEntry.class,                 null)
        .put(Entry.class,                    null)

        .put(RpcStateMachineCommand.RpcSetStateMachineCommand.class, null)
        .put(RpcStateMachineCommand.RpcUnSetStateMachineCommand.class, null)

        .build();
  }
}
