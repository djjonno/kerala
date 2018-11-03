package org.elkd.core.server.converters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.server.RpcEntry;
import org.elkd.core.server.RpcStateMachineCommand;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;
import org.elkd.core.statemachine.StateMachineCommand;

import java.util.Set;

public class EntryConverter implements Converter {
  @Override
  public Set<Class> forTypes() {
    return ImmutableSet.of(
        Entry.class,
        RpcEntry.class
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convert(final Object source, final ConverterRegistry registry) throws ConverterException {
    com.google.common.base.Preconditions.checkNotNull(source, "source");

    if (Entry.class.isInstance(source)) {
      return (T) convertEntry((Entry) source, registry);
    } else if (RpcEntry.class.isInstance(source)) {
      return (T) convertRpcEntry((RpcEntry) source, registry);
    }

    throw new ConverterNotFoundException(source.getClass());
  }

  private RpcEntry convertEntry(final Entry source, final ConverterRegistry registry) {
    final ImmutableList.Builder<RpcStateMachineCommand> builder = ImmutableList.builder();
    for (final StateMachineCommand command : source.getCommands()) {
      builder.add(registry.<RpcStateMachineCommand>convert(command));
    }

    return RpcEntry.newBuilder()
        .addAllCommands(builder.build())
        .setEvent(source.getEvent())
        .build();
  }

  private Entry convertRpcEntry(final RpcEntry source, final ConverterRegistry registry) {
    final Entry.Builder builder = Entry.builder(source.getEvent());
    for (final RpcStateMachineCommand command : source.getCommandsList()) {
      builder.withCommand(registry.convert(command));
    }

    return builder.build();
  }
}
