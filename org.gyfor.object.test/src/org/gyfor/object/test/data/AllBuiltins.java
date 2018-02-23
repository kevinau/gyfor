package org.gyfor.object.test.data;

import java.net.URL;
import java.nio.file.Path;

import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.gyfor.object.IOField;
import org.gyfor.object.value.FileContent;
import org.gyfor.value.VersionTime;

public class AllBuiltins {

  // TODO
  //private Directory directory;
  
  @IOField
  private EmailAddress emailAddress;
  
  @IOField
  private FileContent fileContent;
  
  // TODO
  //private ImageCode imageCode;
  
  // TODO
  //private Password password;
  
  @IOField
  private Path path;
  
  // TODO
  //private PercentValue percent;
  
  // TODO
  //private PhoneNumber phoneNumbere;
  
  // TODO
  //private RegexString regexString;
  
  @IOField
  private String string;
  
  // TODO
  //private Tree tree;
  
  @IOField
  private URL url;
  
  @IOField
  private VersionTime version;
  
}
