package org.gyfor.object.plan.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.IPlanFactory;
import org.gyfor.object.Id;
import org.gyfor.object.UniqueConstraint;
import org.gyfor.object.Version;
import org.gyfor.object.plan.EntityLabelGroup;
import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INameMappedPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.type.IType;
import org.gyfor.object.type.builtin.EntityLifeType;
import org.gyfor.object.value.EntityLife;


public class EntityPlan<T> extends ClassPlan<T> implements IEntityPlan<T>, IClassPlan<T>, INameMappedPlan, INodePlan {

  private final Class<T> entityClass;
  private final String entityName;
  private final EntityLabelGroup labels;

  private IItemPlan<?> idPlan;
  private IItemPlan<?> versionPlan;
  private IItemPlan<?> entityLifePlan;
  private List<IItemPlan<?>[]> uniqueConstraints;

  
  public EntityPlan (IPlanFactory context, Class<T> entityClass) {
    super (context, null, entityClass, entityClass.getSimpleName(), entityEntryMode(entityClass));
    this.entityClass = entityClass;
    this.entityName = entityClass.getSimpleName();
    this.labels = new EntityLabelGroup(entityClass);
    
    findEntityItems();
    findUniqueConstraints();
  }


  @Override
  public String getEntityName() {
    return entityName;
  }


  @Override
  public EntityLabelGroup getLabels () {
    return labels;
  }
  
  
  @Override
  public IItemPlan<?> getIdPlan () {
    return idPlan;
  }
  
  @Override
  public int getId (Object instance) {
    return idPlan.getValue(instance);
  }
  
  @Override
  public void setId (Object instance, int id) {
    idPlan.setValue(instance, id);
  }
  
  @Override
  public IItemPlan<?> getVersionPlan () {
    return versionPlan;
  }
  
  @Override
  public Timestamp getVersion (Object instance) {
    return versionPlan.getValue(instance);
  }
  
  @Override
  public void setVersion (Object instance, Timestamp version) {
    if (versionPlan != null) {
      versionPlan.setValue(instance, version);
    }
  }
  
  
  @Override
  public IItemPlan<?> getEntityLifePlan () {
    return entityLifePlan;
  }
  
  
  @Override
  public EntityLife getLife (Object instance) {
    if (entityLifePlan == null) {
      return null;
    } else {
      return entityLifePlan.getValue(instance);
    }
  }
  
  @Override
  public void setLife (Object instance, EntityLife life) {
    if (entityLifePlan != null) {
      entityLifePlan.setValue(instance, life);
    }
  }
  
  
  @Override
  public IItemPlan<?>[] getKeyItems (int index) {
    return uniqueConstraints.get(index);
  }
  
  
  @Override
  public List<INodePlan> getDataNodes (int index) {
    IItemPlan<?>[] keys = getKeyItems(index);
    IItemPlan<?> idNode = getIdPlan();
    IItemPlan<?> versionNode = getVersionPlan();
    IItemPlan<?> entityLifeNode = getEntityLifePlan();
    
    List<INodePlan> dataNodes = new ArrayList<>();
    for (INodePlan node : getMemberPlans()) {
      boolean isKey = false;
      for (IItemPlan<?> key : keys) {
        if (key.equals(node)) {
          isKey = true;
          break;
        }
      }
      if (isKey) {
        continue;
      }
      if (node.equals(idNode)) {
        continue;
      }
      if (node.equals(versionNode)) {
        continue;
      }
      if (node.equals(entityLifeNode)) {
        continue;
      }
      dataNodes.add(node);
    }
    return dataNodes;
  }
  
  
//  @Override
//  public List<IFieldPlan[]> getUniqueConstraints () {
//    return indexes;
//  }
  
  
//  private void buildEntityFields () {
//    List<IItemPlan<?>> memberPlans = new ArrayList<>();
//    getAllFieldPlans(this, memberPlans);
//    
//    List<IItemPlan<?>> keyPlans2 = new ArrayList<>();
//    NaturalKey keyAnn = entityClass.getAnnotation(NaturalKey.class);
//    if (keyAnn != null) {
//      for (String keyName : keyAnn.value()) {
//        IItemPlan<?> plan = getFieldPlan(memberPlans, keyName);
//        keyPlans2.add(plan);
//      }
//      keyPlans = keyPlans2.toArray(new IItemPlan[keyPlans2.size()]);
//    }
//
//    List<IItemPlan<?>> dataPlans2 = getDataPlans(memberPlans, keyPlans2);
//    if (keyPlans == null) {
//      keyPlans = new IItemPlan[1];
//      keyPlans[0] = dataPlans2.get(0);
//      dataPlans2.remove(0);
//    }
//    dataPlans = dataPlans2.toArray(new IItemPlan[dataPlans2.size()]);
//  }
//
//  
//  private static IItemPlan<?> getItemPlan (List<IItemPlan<?>> itemPlans, String name) {
//    for (IItemPlan<?> plan : itemPlans) {
//      if (plan.getName().equals(name)) {
//        return plan;
//      }
//    }
//    throw new IllegalArgumentException(name);
//  }
//  
//  
//  private static void getAllItemPlans (IClassPlan<?> parent, List<IItemPlan<?>> fieldPlans) {
//    for (INodePlan plan : parent.getMemberPlans()) {
//      switch (plan.kind()) {
//      case ITEM :
//        fieldPlans.add((IItemPlan<?>)plan);
//        break;
//      case EMBEDDED :
//        getAllFieldPlans((IClassPlan<?>)plan, fieldPlans);
//        break;
//      default :
//        break;
//      }
//    }
//  }
  
  
  private void findEntityItems () {
    IItemPlan<?> idPlan2 = null;
    IItemPlan<?> versionPlan2 = null;

    INodePlan[] memberPlans = getMemberPlans();
    for (INodePlan member : memberPlans) {
      if (member.isItem()) {
        IItemPlan<?> itemPlan = (IItemPlan<?>)member;
        Id idann = itemPlan.getAnnotation(Id.class);
        if (idann != null) {
          idPlan = itemPlan;
          // Id fields are not key or data columns
          continue;
        }
        String name = itemPlan.getName();
        if (name.equals("id")) {
          idPlan2 = itemPlan;
        }
        
        Version vann = itemPlan.getAnnotation(Version.class);
        if (vann != null) {
          versionPlan = itemPlan;
          // Version fields are not key or data columns
          continue;
        }
        if (name.equals("version")) {
          versionPlan2 = itemPlan;
        }
        
        IType<?> type = itemPlan.getType();
        if (EntityLifeType.class.isInstance(type)) {
          // Entity life fields are not key or data columns
          // TODO The above statement may not be correct
          entityLifePlan = itemPlan;
          continue;
        }
      }
    }
    if (idPlan == null) {
      idPlan = idPlan2;
    }
    if (versionPlan == null) {
      versionPlan = versionPlan2;
    }
 }


  @Override
  public List<IItemPlan<?>[]> getUniqueConstraints() {
    return uniqueConstraints;
  }
  
  
  private void findUniqueConstraints() {
    UniqueConstraint[] ucAnnx = entityClass.getAnnotationsByType(UniqueConstraint.class);
    uniqueConstraints = new ArrayList<>(ucAnnx.length);
    for (UniqueConstraint ucAnn : ucAnnx) {
      IItemPlan<?>[] fields = new IItemPlan[ucAnn.value().length];
      int i = 0;
      for (String name : ucAnn.value()) {
        INodePlan keyNode = getMemberPlan(name);
        if (keyNode == null) {
          throw new IllegalArgumentException("Item '" + name + "' in unique constraint on class '" + entityClass.getName() + "' does not exist");
        }
        if (!keyNode.isItem()) {
          throw new IllegalArgumentException("Node '" + name + "' in unique constraint on class '" + entityClass.getName() + "' must be an item");
        }
        fields[i] = (ItemPlan<?>)keyNode;
      }
      uniqueConstraints.add(fields);
    }
  }


  @Override
  public void indent (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }
  
  
  @Override
  public void dump (int level) {
    indent(level);
    System.out.println("EntityPlan " + getEntityName() + ":");
    //for (IRuntimeFactoryProvider factoryProvider : runtimeFactoryProviders) {
    //  indent(level + 1);
    //  System.out.println(factoryProvider);
    //}
    super.dump(level + 1);
  }


  @Override
  public boolean isNullable() {
    return false;
  }


  @Override
  public T newInstance() {
    T instance;
    try {
      instance = entityClass.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
    return instance;
  }

}