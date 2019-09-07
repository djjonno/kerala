package org.elkd.core.server.converters;

import com.google.common.collect.ImmutableSet;
import org.elkd.core.consensus.messages.KV;
import org.elkd.core.server.client.RpcKV;
import org.elkd.core.server.converters.exceptions.ConverterException;

import java.util.Set;

public class KVConverter implements Converter {
  @Override
  public Set<Class> forTypes() {
    return ImmutableSet.of(
        KV.class,
        RpcKV.class
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convert(final Object source, final ConverterRegistry registry) throws ConverterException {
    if (source instanceof KV) {
      return (T) convertKV((KV) source);
    } else if (source instanceof RpcKV) {
      return (T) convertRpcKV((RpcKV) source);
    }
    return null;
  }

  private KV convertRpcKV(final RpcKV source) {
    return new KV(source.getKey(), source.getValue());
  }

  private RpcKV convertKV(final KV source) {
    return RpcKV.newBuilder()
        .setKey(source.getKey())
        .setValue(source.getVal()).build();
  }
}
