package org.gyfor.nio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * A file writer that cannot create a partly written file.
 * <p>
 * This file writer writes all output to a temporary file.  On "commit"
 * it atomically moves the temporary file to the target file (replacing
 * any existing target file).
 * <p>
 * If the writer does not "commit", the temporary file is removed and 
 * the target file is left unchanged.  In particular, the modified date
 * of the target file is not changed.
 * <p>
 * Note that for this writer, "close" does not "commit".  Merely closing 
 * the writer (either explicitly or via auto close), without a commit,
 * will discard the temporary file and leave the target file unchanged.
 * 
 * @author Kevin Holloway
 *
 */
public class SafeWriter extends Writer {

  private final Path targetPath;
  private final File tempFile;
  private final Writer tempWriter;
  private final String lineSeparator = System.getProperty("line.separator");
  
  private boolean committed = false;
  
  
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
  public void close() {
    try {
      tempWriter.close();
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
  
  
  @Override
  public void flush() throws IOException {
    tempWriter.flush();
  }
  
  
  public void newLine() throws IOException {
    write(lineSeparator);
  }
  
  
  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    tempWriter.write(cbuf,  off,  len);
  }

  
  @Override
  public void write(int c) throws IOException {
    tempWriter.write(c);
  }
  

  @Override
  public void write(String str) throws IOException {
    tempWriter.write(str);
  }

}
