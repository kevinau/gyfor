package org.gyfor.web.form.state;


@FunctionalInterface
public interface TransitionFunction<A, S extends Enum<?>> {

  public S apply(A arg);

}
