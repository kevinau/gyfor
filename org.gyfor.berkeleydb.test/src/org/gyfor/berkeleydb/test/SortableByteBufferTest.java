package org.gyfor.berkeleydb.test;

import java.util.Random;

import org.gyfor.berkeleydb.SortableByteBuffer;
import org.gyfor.math.Decimal;
import org.junit.Assert;
import org.junit.Test;



public class SortableByteBufferTest {

  @Test
  public void testIntPutGet() {
    Random random = new Random(0);
    
    for (int i = 0; i < 1000; i++) {
      int x0 = random.nextInt();
      SortableByteBuffer buffer = new SortableByteBuffer(4);
      buffer.putInt(x0);
      buffer.flip();
      int x1 = buffer.getInt();
      Assert.assertEquals(x1, x0);
    }
  }

  
  @Test
  public void testLongPutGet() {
    Random random = new Random(0);
    
    for (int i = 0; i < 1000; i++) {
      long x0 = random.nextLong();
      SortableByteBuffer buffer = new SortableByteBuffer(4);
      buffer.putLong(x0);
      buffer.flip();
      long x1 = buffer.getLong();
      Assert.assertEquals(x1, x0);
    }
  }

  
  @Test
  public void testFloatPutGet() {
    Random random = new Random(0);
    
    for (int i = 0; i < 1000; i++) {
      float x0 = random.nextFloat();
      SortableByteBuffer buffer = new SortableByteBuffer(4);
      buffer.putFloat(x0);
      buffer.flip();
      float x1 = buffer.getFloat();
      Assert.assertEquals(x1, x0, 0);
    }
  }

  
  @Test
  public void testDoublePutGet() {
    Random random = new Random(0);
    
    for (int i = 0; i < 1000; i++) {
      double x0 = random.nextDouble();
      SortableByteBuffer buffer = new SortableByteBuffer(4);
      buffer.putDouble(x0);
      buffer.flip();
      double x1 = buffer.getDouble();
      Assert.assertEquals(x1, x0, 0);
    }
  }

  
  @Test
  public void testDecimalPutGet() {
    Random random = new Random(0);
    
    for (int i = 0; i < 10000; i++) {
      Decimal d0;
      
      switch (i) {
      case 0 :
        d0 = Decimal.ZERO;
        break;
      case 1 :
        d0 = Decimal.ONE;
        break;
      case 2 :
        d0 = Decimal.ONE.negate();
        break;
      case 3 :
        d0 = Decimal.TEN;
        break;
      case 4 :
        d0 = Decimal.TEN.negate();
        break;
      case 5 :
        d0 = Decimal.HUNDRED;
        break;
      case 6 :
        d0 = Decimal.HUNDRED.negate();
        break;
      case 7 :
        d0 = new Decimal(1.5);
        break;
      case 8 :
        d0 = new Decimal(-1.5);
        break;
      default :
        long x0 = random.nextInt(200000) - 10000;
        int scale = random.nextInt(4);
        d0 = new Decimal(x0, scale);
        break;
      }

      SortableByteBuffer buffer = new SortableByteBuffer(5);
      buffer.putDecimal(d0);
      buffer.flip();
      Decimal d1 = buffer.getDecimal();
      Assert.assertEquals(d0, d1);
    }
  }

  
  @Test
  public void testIntComapreTo() {
    Random random = new Random(0);
    
    for (int i = 0; i < 1000; i++) {
      int x0 = random.nextInt();
      SortableByteBuffer buffer0 = new SortableByteBuffer(4);
      buffer0.putInt(x0);
      buffer0.flip();
      
      int x1 = random.nextInt();
      SortableByteBuffer buffer1 = new SortableByteBuffer(4);
      buffer1.putInt(x1);
      buffer1.flip();
      
      int n = buffer0.compareTo(buffer1);
      if (n < 0) {
        // Buffer0 (this) is less than buffer1 (other)
        if (x0 >= x1) {
          Assert.fail("Text " + i + ": " + x0 + " (" + Integer.toHexString(x0) + ") sorted after " + x1 + " (" + Integer.toHexString(x1) + ")");          
        }
      } else if (n > 0) {
        // Buffer0 (this) is greater than buffer1 (other)
        if (x0 <= x1) {
          Assert.fail("Text " + i + ": " + x0 + " (" + Integer.toHexString(x0) + ") sorted before " + x1 + " (" + Integer.toHexString(x1) + ")");
        }
      } else {
        // Buffer0 (this) is equal to buffer1 (other)
        Assert.fail("Text " + i + ": " + x0 + " (" + Integer.toHexString(x0) + ") not sorted equal to " + x1 + " (" + Integer.toHexString(x1) + ")");
      }
    }
  }

  @Test
  public void testLongComapreTo() {
    Random random = new Random(0);
    
    for (int i = 0; i < 1000; i++) {
      long x0 = random.nextLong();
      SortableByteBuffer buffer0 = new SortableByteBuffer(8);
      buffer0.putLong(x0);
      buffer0.flip();
      
      long x1 = random.nextLong();
      SortableByteBuffer buffer1 = new SortableByteBuffer(8);
      buffer1.putLong(x1);
      buffer1.flip();
      
      int n = buffer0.compareTo(buffer1);
      if (n < 0) {
        // Buffer0 (this) is less than buffer1 (other)
        if (x0 >= x1) {
          Assert.fail("Text " + i + ": " + x0 + " (" + Long.toHexString(x0) + ") sorted after " + x1 + " (" + Long.toHexString(x1) + ")");          
        }
      } else if (n > 0) {
        // Buffer0 (this) is greater than buffer1 (other)
        if (x0 <= x1) {
          Assert.fail("Text " + i + ": " + x0 + " (" + Long.toHexString(x0) + ") sorted before " + x1 + " (" + Long.toHexString(x1) + ")");
        }
      } else {
        // Buffer0 (this) is equal to buffer1 (other)
        Assert.fail("Text " + i + ": " + x0 + " (" + Long.toHexString(x0) + ") not sorted equal to " + x1 + " (" + Long.toHexString(x1) + ")");
      }
    }
  }
  

  @Test
  public void testFloatComapreTo() {
    Random random = new Random(0);
    
    for (int i = 0; i < 1000; i++) {
      float x0 = random.nextFloat();
      SortableByteBuffer buffer0 = new SortableByteBuffer(4);
      buffer0.putFloat(x0);
      buffer0.flip();
      
      float x1 = random.nextFloat();
      SortableByteBuffer buffer1 = new SortableByteBuffer(4);
      buffer1.putFloat(x1);
      buffer1.flip();
      
      int n = buffer0.compareTo(buffer1);
      if (n < 0) {
        // Buffer0 (this) is less than buffer1 (other)
        if (x0 >= x1) {
          Assert.fail("Text " + i + ": " + x0 + " sorted after " + x1);          
        }
      } else if (n > 0) {
        // Buffer0 (this) is greater than buffer1 (other)
        if (x0 <= x1) {
          Assert.fail("Text " + i + ": " + x0 + " sorted before " + x1);
        }
      } else {
        // Buffer0 (this) is equal to buffer1 (other)
        Assert.fail("Text " + i + ": " + x0 + " not sorted equal to " + x1);
      }
    }
  }

  @Test
  public void testDoubleComapreTo() {
    Random random = new Random(0);
    
    for (int i = 0; i < 1000; i++) {
      double x0 = random.nextDouble();
      SortableByteBuffer buffer0 = new SortableByteBuffer(8);
      buffer0.putDouble(x0);
      buffer0.flip();
      
      double x1 = random.nextDouble();
      SortableByteBuffer buffer1 = new SortableByteBuffer(8);
      buffer1.putDouble(x1);
      buffer1.flip();
      
      int n = buffer0.compareTo(buffer1);
      if (n < 0) {
        // Buffer0 (this) is less than buffer1 (other)
        if (x0 >= x1) {
          Assert.fail("Text " + i + ": " + x0 + " sorted after " + x1);          
        }
      } else if (n > 0) {
        // Buffer0 (this) is greater than buffer1 (other)
        if (x0 <= x1) {
          Assert.fail("Text " + i + ": " + x0 + " sorted before " + x1);
        }
      } else {
        // Buffer0 (this) is equal to buffer1 (other)
        Assert.fail("Text " + i + ": " + x0 + " not sorted equal to " + x1);
      }
    }
  }

  
  @Test
  public void testDecimalComapreTo() {
    Random random = new Random(0);
    
    for (int i = 0; i < 10000; i++) {
      long j = random.nextInt(200000) - 100000;
      int scale = random.nextInt(4);
      Decimal x0 = new Decimal(j, scale);

      j = random.nextInt(200000) - 100000;
      scale = random.nextInt(4);
      Decimal x1 = new Decimal(j, scale);
      
      SortableByteBuffer buffer0 = new SortableByteBuffer(8);
      buffer0.putDecimal(x0);
      buffer0.flip();
      
      SortableByteBuffer buffer1 = new SortableByteBuffer(8);
      buffer1.putDecimal(x1);
      buffer1.flip();
      
      int n = buffer0.compareTo(buffer1);
      if (n < 0) {
        // Buffer0 (this) is less than buffer1 (other)
        if (x0.compareTo(x1) >= 0) {
          Assert.fail("Text " + i + ": " + x0 + " sorted after " + x1);          
        }
      } else if (n > 0) {
        // Buffer0 (this) is greater than buffer1 (other)
        if (x0.compareTo(x1) <= 0) {
          Assert.fail("Text " + i + ": " + x0 + " sorted before " + x1);
        }
      } else if (x0.compareTo(x1) != 0) {
        // Buffer0 (this) is equal to buffer1 (other)
        Assert.fail("Text " + i + ": " + x0 + " not sorted equal to " + x1);
      }
    }
  }

  
  @Test
  public void testUTF8 () {
    int[] values = {
        0x0024,
        0x00A2,
        0x20AC,
        0x10348,
    };
    
    for (int j = 0; j < values.length; j++) {
      SortableByteBuffer buffer = new SortableByteBuffer(4);
      buffer.putUTF8(values[j]);
      buffer.flip();
      int x1 = buffer.getUTF8();
      Assert.assertEquals(values[j], x1);
    }
    
    for (int x0 = 0; x0 < 0xfffff; x0++) {
      SortableByteBuffer buffer = new SortableByteBuffer(4);
      buffer.putUTF8(x0);
      buffer.flip();
      int x1 = buffer.getUTF8();
      Assert.assertEquals(x0, x1);
    }

  }
  
}
