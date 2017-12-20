package org.gyfor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;

public class CRC64DigestFactory implements DigestFactory {

  /*
   * ECMA: 0x42F0E1EBA9EA3693 / 0xC96C5795D7870F42 / 0xA17870F5D4F51B49
   */
  private static final long POLY64 = 0x42F0E1EBA9EA3693L;
  private static final long[] LOOKUPTABLE;

  static {
    LOOKUPTABLE = new long[0x100];
    for (int i = 0; i < 0x100; i++) {
      long crc = i;
      for (int j = 0; j < 8; j++) {
        if ((crc & 1) == 1) {
          crc = (crc >>> 1) ^ POLY64;
        } else {
          crc = (crc >>> 1);
        }
      }
      LOOKUPTABLE[i] = crc;
    }
  }
  

//  /**
//   * The checksum of the data
//   * @param   data    The data to checksum
//   * @return  The checksum of the data
//   */
//  private static long digest(final byte[] data, int length, long checksum) {
//    for (int i = 0; i < length; i++) {
//      final int lookupidx = ((int) checksum ^ data[i]) & 0xff;
//      checksum = (checksum >>> 8) ^ LOOKUPTABLE[lookupidx];
//    }
//    return checksum;
//  }


//  public static CRC64 getFileDigest (File file) {
//    long checksum = 0L;
//    try {
//      RandomAccessFile aFile = new RandomAccessFile(file, "r");
//      FileChannel inChannel = aFile.getChannel();
//      MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
//      buffer.load();
//      for (int i = 0; i < buffer.limit(); i++) {
//        int bx = buffer.get();
//        int lookupidx = ((int) checksum ^ bx) & 0xff;
//        checksum = (checksum >>> 8) ^ LOOKUPTABLE[lookupidx];
//      }
//      buffer.clear(); // do something with the data and clear/compact it.
//      inChannel.close();
//      //aFile.close();
//    } catch (IOException ex) {
//      throw new RuntimeException(ex);
//    }
//    return new CRC64(checksum);
//  }
  
  
  @Override
  public Digest getFileDigest (File file) {
    try (FileInputStream fis = new FileInputStream(file)) {
      return getInputStreamDigest(fis);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }


  @Override
  public Digest getFileDigest (Path path) {
    return getFileDigest(path.toFile());
  }


  @Override
  public Digest getFileDigest (URL url) {
    try {
      return getInputStreamDigest(url.openStream());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public Digest getInputStreamDigest (InputStream fis) {
    try {
      long checksum = 0L;
      byte[] dataBytes = new byte[4096];
      int n = fis.read(dataBytes); 
      while (n != -1) {
        for (int i = 0; i < n; i++) {
          int bx = dataBytes[i];
          int lookupidx = ((int) checksum ^ bx) & 0xff;
          checksum = (checksum >>> 8) ^ LOOKUPTABLE[lookupidx];
        }
        n = fis.read(dataBytes);
      }
      return new CRC64Digest(checksum);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }


  private static long getCRCValue (byte[] dataBytes, int n, long checksum) {
    for (int i = 0; i < n; i++) {
      int bx = dataBytes[i];
      int lookupidx = ((int) checksum ^ bx) & 0xff;
      checksum = (checksum >>> 8) ^ LOOKUPTABLE[lookupidx];
    }
    return checksum;
  }


  public static long getCRCValue (String s) {
    byte[] dataBytes = s.getBytes();
    return getCRCValue(dataBytes, dataBytes.length, 0L);
  }
  
  
  @Override
  public Digest getObjectDigest(Object obj) {
    throw new RuntimeException("Method not implemented");
  }

}
