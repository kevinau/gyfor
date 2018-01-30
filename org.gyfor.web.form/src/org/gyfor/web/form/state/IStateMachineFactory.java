package org.gyfor.web.form.state;


public interface IStateMachineFactory {

  public <S extends Enum<?>, O extends Enum<?>> StateMachine<S, O> getStateMachine();
  
}
