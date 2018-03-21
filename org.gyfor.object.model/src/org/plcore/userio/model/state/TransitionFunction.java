package org.plcore.userio.model.state;


@FunctionalInterface
public interface TransitionFunction<A, S extends Enum<?>> {

  public S apply(A arg);

}
