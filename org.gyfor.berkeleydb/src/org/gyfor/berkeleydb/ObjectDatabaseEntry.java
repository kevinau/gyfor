package org.gyfor.berkeleydb;

import org.gyfor.object.EntityPlanFactory;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.impl.PlanContext;
import org.gyfor.object.type.IType;
import org.gyfor.todo.NotYetImplementedException;
import org.gyfor.util.SimpleBuffer;

import com.sleepycat.je.DatabaseEntry;


public class ObjectDatabaseEntry extends DatabaseEntry {

  private static final long serialVersionUID = 1L;

  private final IEntityPlan<?> entityPlan;
  
  private int[] fieldFlags;


  public ObjectDatabaseEntry(IPlanContext envmt, Class<?> klass) {
    entityPlan = EntityPlanFactory.getEntityPlan(envmt, klass);
  }


  public ObjectDatabaseEntry(IEntityPlan<?> entityPlan) {
    this.entityPlan = entityPlan;
  }


  @SuppressWarnings("unchecked")
  public <X> X getValue() {
    Object instance = entityPlan.newInstance();
    populateEntityValue(instance);
    return (X)instance;
  }


  private void populateEntityValue(Object instance) {
    SimpleBuffer b = new SimpleBuffer(getData());

    Object value = null;

    INodePlan[] nodePlans = entityPlan.getMemberPlans();
    for (INodePlan nodePlan : nodePlans) {
      switch (nodePlan.getStructure()) {
      case ARRAY :
        throw new NotYetImplementedException();
      case EMBEDDED :
        throw new NotYetImplementedException();
      case ENTITY :
        throw new RuntimeException("Entity cannot contain entity members");
      case INTERFACE :
        throw new NotYetImplementedException();
      case ITEM :
        IItemPlan<?> itemPlan = (IItemPlan<?>)nodePlan;
        IType<?> itemType = itemPlan.getType();
        value = itemType.getFromBuffer(b);
        break;
      case LIST :
        throw new NotYetImplementedException();
      case MAP :
        throw new NotYetImplementedException();
      case REFERENCE :
        throw new NotYetImplementedException();
      case SET :
        throw new NotYetImplementedException();
      }
      nodePlan.setValue(instance, value);
    }
  }


  public void setValue(Object instance) {
    SimpleBuffer b = new SimpleBuffer();

    INodePlan[] nodePlans = entityPlan.getMemberPlans();
    for (INodePlan nodePlan : nodePlans) {
      Object value = nodePlan.getValue(instance);

      switch (nodePlan.getStructure()) {
      case ARRAY :
        throw new NotYetImplementedException();
      case EMBEDDED :
        throw new NotYetImplementedException();
      case ENTITY :
        throw new RuntimeException("Entity cannot contain entity members");
      case INTERFACE :
        throw new NotYetImplementedException();
      case ITEM :
        @SuppressWarnings("unchecked")
        IItemPlan<Object> itemPlan = (IItemPlan<Object>)nodePlan;
        IType<Object> itemType = itemPlan.getType();
        itemType.putToBuffer(b, value);
        break;
      case LIST :
        throw new NotYetImplementedException();
      case MAP :
        throw new NotYetImplementedException();
      case REFERENCE :
        throw new NotYetImplementedException();
      case SET :
        throw new NotYetImplementedException();
      }
    }
  }


  public void setValue(String[] itemStrings) throws UserEntryException {
    int[] index = new int[1];
    setValue(itemStrings, index);
  }


  public void buildFieldFlags() {
    if (fieldFlags == null) {
      int[] count = new int[1];
      countFields(entityPlan, count);

      fieldFlags = new int[count[0]];
      int[] i = new int[1];
      buildFieldFlags(entityPlan, fieldFlags, i);
    }
  }


  private void countFields(IClassPlan<?> classPlan, int[] count) {
    INodePlan[] nodePlans = entityPlan.getMemberPlans();

    for (INodePlan nodePlan : nodePlans) {
      switch (nodePlan.getStructure()) {
      case EMBEDDED :
        IClassPlan<?> embeddedPlan = (IClassPlan<?>)nodePlan;
        countFields(embeddedPlan, count);
        break;
      case ITEM :
        count[0]++;
        break;
      default :
        break;
      }
    }
  }


  private void buildFieldFlags(IClassPlan<?> classPlan, int[] flags, int[] i) {
    INodePlan[] nodePlans = entityPlan.getMemberPlans();

    for (INodePlan nodePlan : nodePlans) {
      switch (nodePlan.getStructure()) {
      case EMBEDDED :
        IClassPlan<?> embeddedPlan = (IClassPlan<?>)nodePlan;
        buildFieldFlags(embeddedPlan, flags, i);
        break;
      case ITEM :
        IItemPlan<?> itemPlan = (IItemPlan<?>)nodePlan;
        IType<?> itemType = itemPlan.getType();
        flags[i[0]] = itemType.getBufferSize();
        i[0]++;
        break;
      default :
        break;
      }
    }
  }


  private void setValue(String[] itemStrings, int[] index) throws UserEntryException {
    SimpleBuffer b = new SimpleBuffer();

    INodePlan[] nodePlans = entityPlan.getMemberPlans();

    // First pass: items and embedded only
    for (INodePlan nodePlan : nodePlans) {
      switch (nodePlan.getStructure()) {
      case ARRAY :
        break;
      case EMBEDDED :
        throw new NotYetImplementedException();
      case ENTITY :
        throw new RuntimeException("Entity cannot contain entity members");
      case INTERFACE :
        // TODO may be moved to the first pass
        break;
      case ITEM :
        @SuppressWarnings("unchecked")
        IItemPlan<Object> itemPlan = (IItemPlan<Object>)nodePlan;
        IType<Object> itemType = itemPlan.getType();
        Object value = itemType.createFromString(itemStrings[index[0]]);
        itemType.putToBuffer(b, value);
        index[0]++;
        break;
      case LIST :
        break;
      case MAP :
        break;
      case REFERENCE :
        // TODO may be moved to the first pass
        break;
      case SET :
        break;
      }
    }

    // Second pass: all other members (not items or embedded)
    for (INodePlan nodePlan : nodePlans) {
      switch (nodePlan.getStructure()) {
      case ARRAY :
        throw new NotYetImplementedException();
      case EMBEDDED :
        break;
      case ENTITY :
        break;
      case INTERFACE :
        throw new NotYetImplementedException();
      case ITEM :
        break;
      case LIST :
        throw new NotYetImplementedException();
      case MAP :
        throw new NotYetImplementedException();
      case REFERENCE :
        throw new NotYetImplementedException();
      case SET :
        throw new NotYetImplementedException();
      }
    }

    super.setData(b.bytes(), 0, b.size());
  }

}
