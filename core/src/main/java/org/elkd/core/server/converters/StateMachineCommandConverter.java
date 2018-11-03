package org.elkd.core.server.converters;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.apache.log4j.Logger;
import org.elkd.core.server.RpcStateMachineCommand;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;
import org.elkd.core.statemachine.SetStateMachineCommand;
import org.elkd.core.statemachine.StateMachineCommand;
import org.elkd.core.statemachine.UnSetStateMachineCommand;

import java.util.Set;

public class StateMachineCommandConverter implements Converter {
  private static final Logger LOG = Logger.getLogger(StateMachineCommandConverter.class);

  @Override
  public Set<Class> forTypes() {
    return ImmutableSet.of(
        SetStateMachineCommand.class,
        UnSetStateMachineCommand.class,

        RpcStateMachineCommand.class
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T convert(final Object source, final ConverterRegistry registry) throws ConverterException {
    Preconditions.checkNotNull(source, "source");

    if (SetStateMachineCommand.class.isInstance(source)) {
      return (T) convertSetStateMachineCommand((SetStateMachineCommand) source);
    } else if (UnSetStateMachineCommand.class.isInstance(source)) {
      return (T) convertUnSetStateMachineCommand((UnSetStateMachineCommand) source);
    } else if (RpcStateMachineCommand.class.isInstance(source)) {
      return (T) convertRpcStateMachineCommand((RpcStateMachineCommand) source);
    }

    throw new ConverterNotFoundException(source.getClass());
  }

  private RpcStateMachineCommand convertSetStateMachineCommand(final SetStateMachineCommand source) {
    return RpcStateMachineCommand.newBuilder()
        .setKey(source.getKey())
        .setValue(source.getValue())
        .setOperation(RpcStateMachineCommand.Operation.SET)
        .build();
  }

  private RpcStateMachineCommand convertUnSetStateMachineCommand(final UnSetStateMachineCommand source) {
    return RpcStateMachineCommand.newBuilder()
        .setKey(source.getKey())
        .setOperation(RpcStateMachineCommand.Operation.UNSET)
        .build();
  }

  private StateMachineCommand convertRpcStateMachineCommand(final RpcStateMachineCommand source) {
    switch (source.getOperation()) {
      case SET:
        return new SetStateMachineCommand(source.getKey(), source.getValue());
      case UNSET:
        return new UnSetStateMachineCommand(source.getKey());
      case UNRECOGNIZED: default:
        throw new ConverterException("Unrecognized command - can't convert");
    }
  }
}
