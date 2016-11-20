/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.object.type.builtin;

import java.io.File;

import org.gyfor.object.type.IType;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.Type;


public abstract class PathBasedType<T extends File> extends Type<T> implements IType<T> {

  private static final String REQUIRED_MESSAGE = "a file name is required";
  
  private final static int DIALOG_SIZE = 40;
  private final static String DIALOG_NAME = "Select file";
  
  private final int viewSize;
  private final String dialogName;
  private final String[] filterExtensions;
  private final String[] filterNames;
  
  
  public PathBasedType () {
    this (DIALOG_SIZE, DIALOG_NAME, new String[] {"*.*"}, new String[] {"All Files (*.*)"});
  }
  
  
  public PathBasedType (int viewSize, String dialogName, String[] filterExtensions, String[] filterNames) {
    if (viewSize == -1) {
      this.viewSize = DIALOG_SIZE;
    } else {
      this.viewSize = viewSize;
    }
    this.dialogName = dialogName;
    this.filterExtensions = filterExtensions;
    this.filterNames = filterNames;
  }

  
//  public IEntryControl2 createEntryControl (Composite parent, boolean allowEmpty, boolean primaryKey) {
//    return new FileNameEntryControl(parent, this, viewSize, dialogName, filterExtensions, filterNames, allowEmpty, true);
//  }
//  
//  
//  public IViewControl createViewControl (Composite parent) {
//    return new TextViewControl(parent, viewSize);
//  }
  
  
  @Override
  public abstract T createFromString (String source) throws UserEntryException;


  @Override
  public void validate(T value) throws UserEntryException {
    File fv = (File)value;
    if (!fv.exists()) {
      // Decide if 'incomplete' should be true or false
      boolean incomplete = false;
      File parentDir = fv.getParentFile();
      if (parentDir == null) {
        // error, ie leave 'incomplete' equal to false
      } else {
        String prefix = fv.getName();
        String[] fileNames = parentDir.list();
        for (String fileName : fileNames) {
          if (fileName.startsWith(prefix)) {
            incomplete = true;
            break;
          }
        }
      }
      throw new UserEntryException("file '" + value + "' does not exist", incomplete);
    }
    if (fv.isDirectory()) {
      throw new UserEntryException("file is a directory or system file", true);
    }
    if (!fv.canRead()) {
      throw new UserEntryException("file cannot be read");
    }
  }
  

  @Override
  public String toEntryString (T value, T fillValue) {
    if (value == null) {
      return "";
    }
    String x = value.getAbsolutePath();
    if (fillValue != null) {
      int n = fillValue.getAbsolutePath().length();
      x = x.substring(n + 1);
    }
    return x;
  }
  
  
  public String getDialogName () {
    return dialogName;
  }


  public String[] getFilterExtensions () {
    return filterExtensions;
  }
  
  
  public String[] getFilterNames () {
    return filterNames;
  }
  
  
  public int getViewSize () {
    return viewSize;
  }


  @Override
  public String getRequiredMessage() {
    return REQUIRED_MESSAGE;
  }


  @Override
  public abstract T newInstance(String source);

}
