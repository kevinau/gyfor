package org.gyfor.nio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SafeWriter extends Writer {

  private final Path targetPath;
  private final File tempFile;
  private final Writer tempWriter;
  private final String lineSeparator = System.getProperty("line.separator");
  
  
  public SafeWriter (Path targetPath) {
    this.targetPath = targetPath;
    this.tempFile = new File(targetPath.toString() + ".part");
    this.tempFile.getParentFile().mkdirs();
    try {
      this.tempWriter = new FileWriter(tempFile);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
  
  
  @Override
  public void write(int c) throws IOException {
    tempWriter.write(c);
  }

  
  @Override
  public void flush() throws IOException {
    tempWriter.flush();
  }
  
  
  public void commit() throws IOException {
    tempWriter.close();
    Files.move(tempFile.toPath(),  targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
  }
  
  
  @Override
  public void close() throws IOException {
    tempWriter.close();
    if (tempFile.exists()) {
      tempFile.delete();
    }
  }
  
  
  public void newLine() throws IOException {
    write(lineSeparator);
  }

  
  @Override
  public void write(String str) throws IOException {
    tempWriter.write(str);
  }
  

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    tempWriter.write(cbuf,  off,  len);
  }

}
