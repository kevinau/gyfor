/*******************************************************************************
s * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.object.type.builtin;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.value.FileContent;
import org.gyfor.todo.NotYetImplementedException;
import org.gyfor.util.SimpleBuffer;


public class FileContentType extends PathBasedType<FileContent> {
  
  public FileContentType () {
    super ();
  }
  
  
  public FileContentType (int viewSize, String dialogName, String[] filterExtensions, String[] filterNames) {
    super (viewSize, dialogName, filterExtensions, filterNames);
  }
  

  @Override
  public FileContent createFromString(String source) throws UserEntryException {
    String sv = source.trim();
    FileContent fileContent = new FileContent(sv);
    validate(fileContent);
    return fileContent;
  }
  
  
  @Override
  public FileContent newInstance (String source) {
    return new FileContent(source);
  }
  
  
  @Override
  public FileContent primalValue() {
    return new FileContent(".");
  }


  @Override
  public int getFieldSize() {
    return 255;
  }


  @Override
  public String[] getSQLTypes() {
    return new String[] {
        "VARCHAR(" + getFieldSize() + ")",
        "BLOB",
    };
  }


  @Override
  public FileContent getFromBuffer(SimpleBuffer b) {
    throw new NotYetImplementedException();
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, FileContent v) {
    throw new NotYetImplementedException();
  }
  
  
  @Override
  public String getSQLType() {
    // Not used
    return null;
  }


  @Override
  public void setSQLValue(PreparedStatement stmt, int sqlIndex, FileContent value) throws SQLException {
    // Not used
  }


  @Override
  public void setSQLValue(PreparedStatement stmt, int[] sqlIndex, FileContent value) throws SQLException {
    stmt.setString(sqlIndex[0]++, value.getFileName());
    Blob blob = stmt.getConnection().createBlob();
    blob.setBytes(1, value.getContents());
    stmt.setBlob(sqlIndex[0]++, blob);
  }


  @Override
  public FileContent getSQLValue(ResultSet resultSet, int sqlIndex) throws SQLException {
    // Not used
    return null;
  }


  @Override
  public FileContent getSQLValue(ResultSet resultSet, int[] sqlIndex) throws SQLException {
    String fileName = resultSet.getString(sqlIndex[0]++);
    Blob blob = resultSet.getBlob(sqlIndex[0]++);
    byte[] bytes = blob.getBytes(1, (int)blob.length());
    return new FileContent(fileName, bytes);
  }
  
}
