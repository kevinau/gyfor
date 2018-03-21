package org.plcore.osgi;

public class IllegalConfigurationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public IllegalConfigurationException (String message) {
    super(message);
  }
  
}
