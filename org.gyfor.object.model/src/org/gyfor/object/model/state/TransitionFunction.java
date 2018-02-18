package org.gyfor.object.model.state;


@FunctionalInterface
public interface TransitionFunction<A, S extends Enum<?>> {

  public S apply(A arg);

}
