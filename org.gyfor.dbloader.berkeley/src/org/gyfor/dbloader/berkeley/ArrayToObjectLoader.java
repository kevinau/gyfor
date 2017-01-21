package org.gyfor.dbloader.berkeley;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.model.path.IPathExpression;
import org.gyfor.object.model.path.ParseException;
import org.gyfor.object.model.path.PathParser;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.type.IType;


public class ArrayToObjectLoader<T> {

  private final IEntityPlan<T> entityPlan;
  
  private final List<IItemPlan<?>> itemPlans;
  
  
  public ArrayToObjectLoader (IEntityPlan<T> entityPlan, String[] itemPaths) {
    this.entityPlan = entityPlan;
    
    itemPlans = new ArrayList<>();
    for (int i = 0; i < itemPaths.length; i++) {
      IPathExpression pathExpr1;
      try {
        pathExpr1 = PathParser.parse(itemPaths[i]);
      } catch (ParseException ex) {
        throw new RuntimeException(ex);
      }
      pathExpr1.matches(entityPlan, null, c -> {
        if (c instanceof IItemPlan) {
          itemPlans.add((IItemPlan<?>)c);
        }
      });
    }
  }
  
  
  public T getValue (int lineNo, String[] itemStrings) throws UserEntryException {
    T instance = entityPlan.newInstance();

    int i = 0;
    for (IItemPlan<?> itemPlan : itemPlans) {
      IType<?> itemType = itemPlan.getType();
      Object value;
      try {
        value = itemType.createFromString(itemStrings[i]);
      } catch (UserEntryException ex) {
        throw new UserEntryException("Line " + lineNo + ", field " + (i + 1) + ": '" + itemStrings[i] + "'", ex);
      }
      itemPlan.setValue(instance, value);
      i++;
    }
    return instance;
  }
  
}
