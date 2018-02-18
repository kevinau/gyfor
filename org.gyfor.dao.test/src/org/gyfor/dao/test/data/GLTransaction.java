package org.gyfor.dao.test.data;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import org.gyfor.value.EntityLife;
import org.gyfor.value.VersionTime;


public class GLTransaction {

  private int id;
  
  private VersionTime version;
  
  private EntityLife entityLife;
  
  private LocalDate date;
  
  private String narrative;
  
  private GLAllocation[] allocations = new GLAllocation[0];
  
  
  public void addAllocation (GLAccount account, BigDecimal debit, BigDecimal credit) {
    int n = allocations.length;
    allocations = Arrays.copyOf(allocations, n + 1);
    allocations[n - 1] = new GLAllocation(this, account, debit, credit);
  }
  
  
  public static void main (String[] args) {
    Field[] fields = GLAllocation.class.getDeclaredFields();
    for (Field field : fields) {
      System.out.println(field);
      System.out.println(field.getName());
    }
  }
}
