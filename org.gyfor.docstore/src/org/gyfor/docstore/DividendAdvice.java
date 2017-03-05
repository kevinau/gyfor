package org.gyfor.docstore;

import java.time.LocalDate;

import org.gyfor.math.Decimal;

public class DividendAdvice {

  private int partyId;
  
  private LocalDate declaredDate;
  
  private LocalDate paymentDate;
  
  private Decimal dividendAmount;
  
  private Decimal imputationCredit;
  
  private DividendType dividendType;
  
  
  public DividendAdvice () {
  }
  
  
  public DividendAdvice (LocalDate declaredDate, LocalDate paymentDate, Decimal dividendAmount, Decimal imputationCredit) {
    this.declaredDate = declaredDate;
    this.paymentDate = paymentDate;
    this.dividendAmount = dividendAmount;
    this.imputationCredit = imputationCredit;
  }
  
  
  public String getType () {
    return "Dividend advice";
  }
  
}
