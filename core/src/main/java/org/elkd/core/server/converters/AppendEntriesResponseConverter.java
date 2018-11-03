package org.elkd.core.server.converters;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.server.RpcAppendEntriesResponse;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;

import java.util.Set;

public class AppendEntriesResponseConverter implements Converter {
  @Override
  public Set<Class> forTypes() {
    return ImmutableSet.of(
        AppendEntriesResponse.class,
        RpcAppendEntriesResponse.class
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convert(final Object source, final ConverterRegistry registry) throws ConverterException {
    Preconditions.checkNotNull(source, "source");

    if (RpcAppendEntriesResponse.class.isInstance(source)) {
      return (T) convertRpcAppendEntriesResponse(source);
    } else if (AppendEntriesResponse.class.isInstance(source)) {
      return (T) convertAppendEntriesResponse(source);
    }

    throw new ConverterNotFoundException(source.getClass());
  }

  private AppendEntriesResponse convertRpcAppendEntriesResponse(final Object source) {
    final RpcAppendEntriesResponse cast = (RpcAppendEntriesResponse) source;
    return AppendEntriesResponse.builder(cast.getTerm(), cast.getSuccess()).build();
  }

  private RpcAppendEntriesResponse convertAppendEntriesResponse(final Object source) {
    final AppendEntriesResponse cast = (AppendEntriesResponse) source;

    return RpcAppendEntriesResponse.newBuilder()
        .setTerm(cast.getTerm())
        .setSuccess(cast.isSuccessful())
        .build();
  }
}
