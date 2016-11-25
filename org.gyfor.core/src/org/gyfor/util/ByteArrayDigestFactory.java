package org.gyfor.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ByteArrayHashFactory implements HashFactory {

  private final String algorithm;
  
  private static class DigestOutputStream extends OutputStream {

    private final MessageDigest md;
    
    private DigestOutputStream (String algorithm) {
      try {
        md = MessageDigest.getInstance(algorithm);
      } catch (NoSuchAlgorithmException ex) {
        throw new RuntimeException(ex);
      }
    }
    
    @Override
    public void write(int b) throws IOException {
      md.update((byte)b);
    }
    
    @Override
    public void write(byte[] b) throws IOException {
      md.update(b);
    }
    
    @Override
    public void write(byte[] b, int offset, int len) throws IOException {
      md.update(b, offset, len);
    }
    
    @Override
    public void close () {
    }
    
    @Override
    public void flush() {
    }
    
    private void reset () {
      md.reset();
    }
    
    private byte[] getDigest () {
      return md.digest();
    }
  }
  
  
  protected ByteArrayHashFactory (String algorithm) {
    this.algorithm = algorithm;
  }
  
  
  @Override
  public Hash getFileDigest (Path path) {
    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      InputStream fis = Files.newInputStream(path);
      byte[] dataBytes = new byte[1024];
      int n = fis.read(dataBytes); 
      while (n != -1) {
        md.update(dataBytes, 0, n);
        n = fis.read(dataBytes);
      }
      fis.close();
      return new ByteArrayHash(md.digest());
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public Hash getInputStreamDigest (InputStream fis) {
    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      byte[] dataBytes = new byte[1024];
      int n = fis.read(dataBytes); 
      while (n != -1) {
        md.update(dataBytes, 0, n);
        n = fis.read(dataBytes);
      }
      fis.reset();
      return new ByteArrayHash(md.digest());
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public Hash getObjectDigest (Object obj) {
    DigestOutputStream dos = new DigestOutputStream(algorithm);
    dos.reset();
    try (
        ObjectOutputStream oss = new ObjectOutputStream(dos);
        ) {
      oss.writeUnshared(obj);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return new ByteArrayHash(dos.getDigest());
  }


}
