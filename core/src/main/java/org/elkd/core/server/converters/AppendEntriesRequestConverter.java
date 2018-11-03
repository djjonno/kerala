package org.elkd.core.server.converters;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.server.RpcAppendEntriesRequest;
import org.elkd.core.server.RpcEntry;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;

import java.util.Set;

public class AppendEntriesRequestConverter implements Converter {
  @Override
  public Set<Class> forTypes() {
    return ImmutableSet.of(
        AppendEntriesRequest.class,
        RpcAppendEntriesRequest.class
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convert(final Object source, final ConverterRegistry registry) throws ConverterException {
    Preconditions.checkNotNull(source, "source");

    if (AppendEntriesRequest.class.isInstance(source)) {
      return (T) convertAppendEntriesRequest((AppendEntriesRequest) source, registry);
    } else if (RpcAppendEntriesRequest.class.isInstance(source)) {
      return (T) convertRpcAppendEntriesRequest((RpcAppendEntriesRequest) source, registry);
    }

    throw new ConverterNotFoundException(source.getClass());
  }

  private RpcAppendEntriesRequest convertAppendEntriesRequest(final AppendEntriesRequest source,
                                                              final ConverterRegistry registry) {
    final ImmutableList.Builder<RpcEntry> builder = ImmutableList.builder();
    for (final Entry entry : source.getEntries()) {
      builder.add(registry.<RpcEntry>convert(entry));
    }

    return RpcAppendEntriesRequest.newBuilder()
        .addAllEntries(builder.build())
        .setTerm(source.getTerm())
        .setLeaderId(source.getLeaderId())
        .setPrevLogTerm(source.getPrevLogTerm())
        .setPrevLogIndex(source.getPrevLogIndex())
        .setLeaderCommit(source.getLeaderCommit())
        .build();
  }

  private AppendEntriesRequest convertRpcAppendEntriesRequest(final RpcAppendEntriesRequest source,
                                                              final ConverterRegistry registry) {
    final AppendEntriesRequest.Builder builder = AppendEntriesRequest.builder(
        source.getTerm(),
        source.getPrevLogTerm(),
        source.getPrevLogIndex(),
        source.getLeaderId(),
        source.getLeaderCommit()
    );

    for (RpcEntry rpcEntry : source.getEntriesList()) {
      builder.withEntry(registry.convert(rpcEntry));
    }

    return builder.build();
  }
}
