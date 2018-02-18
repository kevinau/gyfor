package org.gyfor.nio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SafeOutputStream extends OutputStream {

  private final Path targetPath;
  private final File tempFile;
  private final FileOutputStream tempOutputStream;
  
  private boolean committed = false;
  
  
  public SafeOutputStream (Path targetPath) {
    this.targetPath = targetPath;
    this.tempFile = new File(targetPath.toString() + ".part");
    this.tempFile.getParentFile().mkdirs();
    try {
      this.tempOutputStream = new FileOutputStream(tempFile);
    } catch (FileNotFoundException ex) {
      throw new UncheckedIOException(ex);
    }
  }
  
  
  @Override
  public void close() {
    try {
      tempOutputStream.close();
      if (committed) {
        Files.move(tempFile.toPath(),  targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        committed = false;
      }
      if (tempFile.exists()) {
        tempFile.delete();
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }


  public void commit() {
    committed = true;
  }

  
  public FileLock lock() {
    try {
      return tempOutputStream.getChannel().lock();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  
  @Override
  public void flush() throws IOException {
    tempOutputStream.flush();
  }
  
  
  @Override
  public void write(byte[] b) throws IOException {
    tempOutputStream.write(b);
  }
  
  
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    tempOutputStream.write(b, off, len);
  }

  
  @Override
  public void write(int b) throws IOException {
    tempOutputStream.write(b);
  }
  
}
