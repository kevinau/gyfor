package org.gyfor.nio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CleanTextReader implements AutoCloseable {

  private final BufferedReader reader;

  public CleanTextReader (String filePath) throws FileNotFoundException {
    reader = new BufferedReader(new FileReader(filePath));
  }


  public CleanTextReader (File file) throws FileNotFoundException {
    reader = new BufferedReader(new FileReader(file));
  }


  /** 
   * Get a line of text from the file, skipping blank lines and comments. 
   * Comments are lines that start with a '#' character.  
   * <p>
   * When determining if a line is
   * blank or a comment, all leading and trailing whitespace is first removed.
   * @Return a text line, stripped of all leading and trailing whitespace.
   */
  public String readLine() throws IOException {
    String line = reader.readLine();
    while (line != null) {
      line = line.trim();
      if (line.length() > 0 && line.charAt(0) != '#') {
        return line;
      }
      line = reader.readLine();
    }
    return null;
  }


  @Override
  public void close() throws IOException {
    reader.close();
  }

}
    

