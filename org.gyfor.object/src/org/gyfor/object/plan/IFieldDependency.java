package org.gyfor.object.plan;

import java.util.List;

public interface IFieldDependency {

  public List<String> getDependencies (String className, String methodName);

}
