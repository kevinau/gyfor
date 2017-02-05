package org.gyfor.object.junit;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;
import org.gyfor.object.test.ITestClass;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = TestRunner.class, immediate = true)
public class TestRunner {

  private final Logger log = LoggerFactory.getLogger(TestRunner.class);


  @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
  public void setTestClass(ITestClass testable) {
    log.info("Adding test class: {}", testable.getClass().getName());
    runTests (testable);
  }


  public void unsetTestClass(ITestClass testable) {
    log.info("Removing test class: {}", testable.getClass().getName());
  }


  public void runTests (Object testable) {
    log.info("Starting test runner");
    RunNotifier notifier = new RunNotifier();

//    File reportDir = new File("C:/junit");
//    reportDir.mkdirs();
//    notifier.addListener(new JUnitResultFormatterAsRunListener(new XMLJUnitResultFormatter()) {
//      @Override
//      public void testStarted(Description description) throws Exception {
//        formatter.setOutput(new FileOutputStream(new File(reportDir, "TEST-" + description.getDisplayName() + ".xml")));
//        super.testStarted(description);
//      }
//    });
    notifier.addListener(new SimpleRunListener());  
    
    try {
      Runner testRunner = new JUnit4ObjectRunner(testable);
      testRunner.run(notifier);
    } catch (InitializationError ex) {
      throw new RuntimeException(ex);
    }
  }

}
