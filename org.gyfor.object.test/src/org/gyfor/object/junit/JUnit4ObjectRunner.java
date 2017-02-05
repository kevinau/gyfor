package org.gyfor.object.junit;

import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class JUnit4ObjectRunner extends BlockJUnit4ClassRunner {

  private final Object testObj;
  
  public JUnit4ObjectRunner(Object testObj) throws InitializationError {
    super (testObj.getClass());
    this.testObj = testObj;
  }

  
  @Override
  protected void validateConstructor(List<Throwable> errors) {
    // For OSGi, we know the constructor is valid
  }
  
  
  @Override
  protected void validateOnlyOneConstructor(List<Throwable> errors) {
    // For OSGi, we know the constructor is valid
  }
  
  
  @Override
  protected void validateZeroArgConstructor(List<Throwable> errors) {
    // For OSGi, we know the constructor is valid
  }


  @Override
  protected Object createTest() throws Exception {
    return testObj;
  }

}
