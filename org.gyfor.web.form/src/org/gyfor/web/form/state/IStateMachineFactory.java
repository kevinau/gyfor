package org.gyfor.web.form.state;

import java.util.Map;

public interface IStateMachineFactory {

  public StateMachine<? extends Enum<?>, ? extends Enum<?>> getStateMachine(Map<String, Object> props);

}
