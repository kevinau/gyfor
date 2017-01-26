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

import org.gyfor.object.UserEntryException;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.todo.NotYetImplementedException;
import org.gyfor.util.SimpleBuffer;


public class PathType extends PathBasedType<File> {

  public PathType () {
    super ();
  }
  
  
  public PathType (int viewSize, String dialogName, String[] filterExtensions, String[] filterNames) {
    super (viewSize, dialogName, filterExtensions, filterNames);
  }


  @Override
  public File createFromString (String source) throws UserEntryException {
    String sv = source.trim();
    File file = new File(sv);
    validate (file);
    return file;
  }


  @Override
  public File primalValue() {
    String userHome = System.getProperty("user.home");
    return new File(userHome);
  }


  @Override
  public File newInstance(String source) {
    return new File(source);
  }

  
  @Override
  public int getFieldSize() {
    return 255;
  }

  
  @Override
  public File getFromBuffer (SimpleBuffer b) {
    throw new NotYetImplementedException();
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, File v) {
    throw new NotYetImplementedException();
  }

  
  @Override
  public int getBufferSize () {
    throw new NotYetImplementedException();
  }
  
  
  @Override
  public String getSQLType() {
    return "VARCHAR(" + getFieldSize() + ")";
  }


  @Override
  public void setStatementFromValue(IPreparedStatement stmt, int sqlIndex, File value) {
    stmt.setString(sqlIndex, value.toString());
  }


  @Override
  public File getResultValue(IResultSet resultSet, int sqlIndex) {
    return new File(resultSet.getString(sqlIndex));
  }

}
