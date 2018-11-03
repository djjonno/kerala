package org.elkd.core.server.converters;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.server.RpcRequestVoteRequest;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;

import java.util.Set;

public class RequestVoteRequestConverter implements Converter {
  @Override
  public Set<Class> forTypes() {
    return ImmutableSet.of(
        RequestVoteRequest.class,
        RpcRequestVoteRequest.class
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convert(final Object source, final ConverterRegistry registry) throws ConverterException {
    Preconditions.checkNotNull(source, "source");

    if (RequestVoteRequest.class.isInstance(source)) {
      return (T) convertRequestVoteRequest(source);
    } else if (RpcRequestVoteRequest.class.isInstance(source)) {
      return (T) convertRpcRequestVoteRequest(source);
    }

    throw new ConverterNotFoundException(source.getClass());
  }

  private RpcRequestVoteRequest convertRequestVoteRequest(final Object source) {
    final RequestVoteRequest cast = (RequestVoteRequest) source;

    return RpcRequestVoteRequest.newBuilder()
        .setTerm(cast.getTerm())
        .setCandidateId(cast.getCandidateId())
        .setLastLogIndex(cast.getLastLogIndex())
        .setLastLogTerm(cast.getLastLogTerm())
        .build();
  }

  private RequestVoteRequest convertRpcRequestVoteRequest(final Object source) {
    final RpcRequestVoteRequest cast = (RpcRequestVoteRequest) source;

    return RequestVoteRequest.builder(
        cast.getTerm(),
        cast.getCandidateId(),
        cast.getLastLogIndex(),
        cast.getLastLogTerm()
    ).build();
  }
}
