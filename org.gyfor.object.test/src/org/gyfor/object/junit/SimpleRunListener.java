package org.gyfor.object.junit;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class SimpleRunListener extends RunListener {

  @Override
  public void testAssumptionFailure(Failure failure) {
    System.out.println("test assumption failure:" + failure);
    super.testAssumptionFailure(failure);
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
    System.out.println("test failure:" + failure);
    super.testFailure(failure);
  }
  
  @Override
  public void testFinished(Description description) throws Exception {
    System.out.println("test finished: " + description);
    super.testFinished(description);
  }
  
  @Override
  public void testIgnored(Description description) throws Exception {
    System.out.println("test ignored: " + description);
    super.testIgnored(description);
  }

  @Override
  public void testRunFinished(Result result) throws Exception {
    System.out.println("test run finished");
    super.testRunFinished(result);
  }
  
  @Override
  public void testRunStarted(Description description) throws Exception {
    System.out.println("test run started: " + description);
    super.testRunStarted(description);
  }
  
  @Override
  public void testStarted(Description description) throws Exception {
    System.out.println("test started: " + description);
    super.testStarted(description);
  }
  
}
