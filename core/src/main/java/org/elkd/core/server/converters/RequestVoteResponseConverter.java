package org.elkd.core.server.converters;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.server.RpcRequestVoteResponse;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;

import java.util.Set;

public class RequestVoteResponseConverter implements Converter {
  @Override
  public Set<Class> forTypes() {
    return ImmutableSet.of(
        RequestVoteResponse.class,
        RpcRequestVoteResponse.class
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convert(final Object source, final ConverterRegistry registry) throws ConverterException {
    Preconditions.checkNotNull(source, "source");

    if (RequestVoteResponse.class.isInstance(source)) {
      return (T) convertRequestVoteResponse(source);
    } else if (RpcRequestVoteResponse.class.isInstance(source)) {
      return (T) convertRpcRequestVoteResponse(source);
    }

    throw new ConverterNotFoundException(source.getClass());
  }

  private RpcRequestVoteResponse convertRequestVoteResponse(final Object source) {
    final RequestVoteResponse cast = (RequestVoteResponse) source;

    return RpcRequestVoteResponse.newBuilder()
        .setTerm(cast.getTerm())
        .setVoteGranted(cast.isVoteGranted())
        .build();
  }

  private RequestVoteResponse convertRpcRequestVoteResponse(final Object source) {
    final RpcRequestVoteResponse cast = (RpcRequestVoteResponse) source;

    return RequestVoteResponse.builder(cast.getTerm(), cast.getVoteGranted()).build();
  }
}
