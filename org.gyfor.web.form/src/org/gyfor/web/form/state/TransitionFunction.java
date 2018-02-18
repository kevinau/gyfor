package org.gyfor.web.form.state;


@FunctionalInterface
public interface TransitionFunction<S extends Enum<?>> {

  public S apply();

}
