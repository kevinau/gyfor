package org.gyfor.dao.test.data;

import java.math.BigDecimal;

import org.gyfor.object.ManyToOne;

public class GLAllocation {
  
  @ManyToOne
  private GLTransaction parent;
  
  @ManyToOne
  private GLAccount account;
  
  private BigDecimal debit;
  
  private BigDecimal credit;
  
  public GLAllocation (GLTransaction parent, GLAccount account, BigDecimal debit, BigDecimal credit) {
    this.parent = parent;
    this.account = account;
    this.debit = debit;
    this.credit = credit;
  }
}
