package org.gyfor.docstore;

import java.time.LocalDate;

import org.gyfor.math.Decimal;

import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class DividendAdvice extends Document {

  private static final long serialVersionUID = 1L;

  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Party.class, onRelatedEntityDelete = DeleteAction.ABORT)
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
  
  
  @Override
  public String getType () {
    return "Dividend advice";
  }
  
}
