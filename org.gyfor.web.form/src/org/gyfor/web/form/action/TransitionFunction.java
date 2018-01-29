package org.gyfor.web.form.action;


@FunctionalInterface
public interface TransitionFunction<A, S extends Enum<?>> {

  public S apply(A arg);

}
