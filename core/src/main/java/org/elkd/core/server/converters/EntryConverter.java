package org.elkd.core.server.converters;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.server.cluster.RpcEntry;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;

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
    Preconditions.checkNotNull(source, "source");

    if (source instanceof Entry) {
      return (T) convertEntry((Entry) source, registry);
    } else if (source instanceof RpcEntry) {
      return (T) convertRpcEntry((RpcEntry) source, registry);
    }

    throw new ConverterNotFoundException(source.getClass());
  }

  private RpcEntry convertEntry(final Entry source, final ConverterRegistry registry) {
    return RpcEntry.newBuilder()
        .setTerm(source.getTerm())
        .setEvent(source.getEvent())
        .build();
  }

  private Entry convertRpcEntry(final RpcEntry source, final ConverterRegistry registry) {
    final Entry.Builder builder = Entry.builder(source.getTerm(), source.getEvent());
    return builder.build();
  }
}
