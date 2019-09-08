package org.elkd.core.server.converters;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.consensus.messages.KV;
import org.elkd.core.server.client.RpcKV;
import org.elkd.core.server.cluster.RpcEntry;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        .setUuid(source.getUuid())
        .setTerm(source.getTerm())
        .setTopic(source.getTopic())
        .addAllKv(source.getKvs().stream().map((Function<KV, RpcKV>) registry::convert)
            .collect(Collectors.toList()))
        .build();
  }

  private Entry convertRpcEntry(final RpcEntry source, final ConverterRegistry registry) {
    return Entry.builder(source.getTerm(), source.getTopic(), source.getUuid())
        .addAllKV(source.getKvList().stream().map((Function<RpcKV, KV>) registry::convert)
            .collect(Collectors.toList()))
        .build();
  }
}
