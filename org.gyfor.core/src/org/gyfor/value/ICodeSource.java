package org.gyfor.value;

import java.util.List;


@FunctionalInterface
public interface ICodeSource<T extends ICode> {

  public List<T> values();

}
