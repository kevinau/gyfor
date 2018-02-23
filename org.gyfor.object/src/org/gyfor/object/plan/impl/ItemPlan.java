package org.gyfor.object.plan.impl;

import org.gyfor.object.EntryMode;
import org.gyfor.object.plan.MemberValueGetterSetter;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.ItemLabelGroup;
import org.gyfor.object.plan.PlanStructure;
import org.gyfor.object.type.IType;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;

public class ItemPlan<T> extends NodePlan implements IItemPlan<T> {

  //private final Field field;
  private final IType<T> type;
  //private final boolean nullable;
  private final ItemLabelGroup labels;
  //private final boolean describing;
  //private final Field lastEntryField;
  //private final Object staticDefaultValue;
  
  
  public ItemPlan (MemberValueGetterSetter field, String name, EntryMode entryMode, IType<T> type) {
    super (field, name, entryMode);
    if (type == null) { 
      throw new IllegalArgumentException("Type argument cannot be null");
    }
     
    this.type = type;

    this.labels = new ItemLabelGroup(field, name);
    
    //Describing describingAnn = field.getAnnotation(Describing.class);
    //this.describing = (describingAnn != null);

    //this.lastEntryField = lastEntryField;
    //this.staticDefaultValue = staticDefaultValue;
  }
  

  @Override
  public IType<T> getType () {
    return type;
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public ItemLabelGroup getLabels () {
    return labels;
  }
  
  
//  @Override
//  public boolean isDescribing () {
//    return describing;
//  }
  
  
//  public Field getLastEntryField () {
//    return lastEntryField;
//  }


//  @Override
//  public Object getStaticDefaultValue () {
//    return staticDefaultValue;
//  }


  @Override
  public void dump (int level) {
    indent(level);
    System.out.println("ItemPlan("  + type + "," + super.toString() + ")");
  }
  
  
  @Override
  public boolean isItem() {
    return true;
  }
  
  
//  @SuppressWarnings("unchecked")
//  @Override
//  public <X> X newInstance () {
//    return (X)staticDefaultValue;
//  }

  @Override
  public String toString() {
    return "ItemPlan[type=" + type + "," + super.toString() + "]";
  }
  
  
//  @Override
//  public IObjectModel buildModel(IForm<?> form, IObjectModel parent, IContainerReference container) {
//    return new FieldModel(form, parent, container, this);
//  }
//
//  @Override
//  public Object newValue () {
//    return type.newValue();
//  }


//  @Override
//  public void accumulateTopItemPlans(List<IItemPlan<?>> itemPlans) {
//    itemPlans.add(this);
//  }


  @Override
  public T getResultValue (IResultSet rs) {
    return type.getResultValue(rs);
  }

  
  @Override
  public void setStatementFromValue (IPreparedStatement stmt, T value) {
    type.setStatementFromValue (stmt, value);
  }
  
  
  @Override
  public PlanStructure getStructure () {
    return PlanStructure.ITEM;
  }


  @Override
  public <X> X newInstance(X fromValue) {
    return fromValue;
  }
  
}
