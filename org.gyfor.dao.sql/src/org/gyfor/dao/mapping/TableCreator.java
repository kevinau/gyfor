package org.gyfor.dao.mapping;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.IEmbeddedPlan;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.type.IType;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.todo.NotYetImplementedException;

public class TableCreator extends TableManipulation {

  private final IConnection conn;
  
  private final Set<String> createdTables = new HashSet<>();
  
  
  private class ElementTable {
    private INodePlan elementPlan; 
    
    private String schema;
    
    private String parentTableName;
    
    private int dimension;
    
    private ElementTable (INodePlan elementPlan, String schema, String parentTableName, int dimension) {
      this.elementPlan = elementPlan;
      this.schema = schema;
      this.parentTableName = parentTableName;
      this.dimension = dimension;
    }
    
    public void createElementTable (Stack<ElementTable> queuedElementTables) {
      String tableName;
      switch (dimension) {
      case 0 :
        tableName = parentTableName + '_' + elementPlan.getName();
        break;
      case 1 :
        tableName = parentTableName + '_' + dimension;
        break;
      default :
        int n = parentTableName.lastIndexOf('_');
        tableName = parentTableName.substring(0, n) + '_' + dimension;
        break;
      }

      if (!createdTables.contains(tableName)) {
        createdTables.add(tableName);

        StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE TABLE ");
        buffer.append(tableName);
        buffer.append(" (" + NL);
      
        buffer.append("id SERIAL PRIMARY KEY");
        buffer.append("," + NL + "parent_id INTEGER REFERENCES ");
        buffer.append(parentTableName);
        buffer.append("(id)");
      
        int[] index = new int[1];
        index[0] = 1;

        switch (elementPlan.getStructure()) {
        case ARRAY :
        case LIST :
          IRepeatingPlan elementPlan2 = (IRepeatingPlan)elementPlan;
          ElementTable elementTable = new ElementTable(elementPlan2.getElementPlan(), schema, tableName, elementPlan2.getDimension());
          queuedElementTables.add(elementTable);
          break;
        case EMBEDDED :
          addMemberNodes(buffer, schema, tableName, null, index, (IEmbeddedPlan<?>)elementPlan, "", queuedElementTables);
          break;
        case ENTITY :
          throw new IllegalArgumentException("IEntityPlan cannot be a child of a element");
        case ITEM :
          buildItemSQL (buffer, null, index, (IItemPlan<?>)elementPlan, "");
          break;
        case REFERENCE :
          buildReferenceSQL (buffer, schema, index, (IReferencePlan<?>)elementPlan, "");
          break;
        default :
          throw new NotYetImplementedException();
        }
        buffer.append(");" + NL);
      
        System.out.println(buffer);
        IPreparedStatement stmt = conn.prepareStatement(buffer);
        stmt.executeUpdate();
      }
    }
  }
  
  
  public TableCreator (IConnection conn) {
    this.conn = conn;
  }
  
  
  private static void buildItemSQL (StringBuilder buffer, IItemPlan<?> entityIdPlan, int[] index, IItemPlan<?> itemPlan, String prefix) {
    if (index[0] > 0) {
      buffer.append(',');
      buffer.append(NL);
    }
    index[0]++;
    
    IType<?> itemType = itemPlan.getType();
    buffer.append(prefix);
    buffer.append(itemPlan.getName());
    buffer.append(' ');
    if (itemPlan == entityIdPlan) {
      buffer.append("SERIAL PRIMARY KEY");
    } else {
      buffer.append(itemType.getSQLType());
      if (itemPlan.isNullable() == false) {
        buffer.append(" NOT NULL");
      }
    }
  }
  
  
  private static void buildReferenceSQL (StringBuilder buffer, String schema, int[] index, IReferencePlan<?> referencePlan, String prefix) {
    if (index[0] > 0) {
      buffer.append(',');
      buffer.append(NL);
    }
    index[0]++;
    
    buffer.append(prefix);
    buffer.append(referencePlan.getName());
    buffer.append(" INTEGER REFERENCES ");
    IEntityPlan<?> referencedEntity = referencePlan.getReferencedPlan();
    buffer.append(getTableName(schema, referencedEntity));
    buffer.append("(");
    buffer.append(referencedEntity.getIdPlan().getName());
    buffer.append(")");
  }
  
  
  private void addMemberNode (StringBuilder buffer, String schema, String parentTableName, IItemPlan<?> entityIdPlan, int[] index, INodePlan nodePlan, String prefix, Stack<ElementTable> queuedElementTables) {
    switch (nodePlan.getStructure()) {
    case ARRAY :
    case LIST :
      IRepeatingPlan elementPlan2 = (IRepeatingPlan)nodePlan;
      ElementTable elementTable = new ElementTable(elementPlan2.getElementPlan(), schema, parentTableName, elementPlan2.getDimension());
      queuedElementTables.add(elementTable);
      break;
    case EMBEDDED :
      IEmbeddedPlan<?> embeddedPlan = (IEmbeddedPlan<?>)nodePlan;
      addMemberNodes (buffer, schema, parentTableName + '_' + nodePlan.getName(), entityIdPlan, index, embeddedPlan, prefix + embeddedPlan.getName() + '_', queuedElementTables);
      break;
    case ENTITY :
      throw new IllegalArgumentException("IEntityPlan cannot be a child of an entity");
    case INTERFACE :
      throw new NotYetImplementedException();
    case ITEM :
      buildItemSQL (buffer, entityIdPlan, index, (IItemPlan<?>)nodePlan, prefix);
      break;
    case MAP :
      throw new NotYetImplementedException();
    case REFERENCE :
      buildReferenceSQL (buffer, schema, index, (IReferencePlan<?>)nodePlan, prefix);
      break;
    case SET :
      throw new NotYetImplementedException();
    }
  }
  
  
  private void addMemberNodes (StringBuilder buffer, String schema, String parentTableName, IItemPlan<?> entityIdPlan, int[] index, IClassPlan<?> embeddedPlan, String prefix, Stack<ElementTable> queuedElementTables) {
    for (INodePlan nodePlan : embeddedPlan.getMembers()) {
      addMemberNode (buffer, schema, parentTableName, entityIdPlan, index, nodePlan, prefix, queuedElementTables);
    }
  }
  
  
//  public void createTableSQL (StringBuilder buffer, String schema, IItemPlan<?> entityIdPlan, IEntityPlan<?> entityPlan) {
//    buffer.append("CREATE TABLE ");
//    appendTableName(buffer, schema, entityPlan);
//    buffer.append(" (" + NL);
//    int[] index = new int[1];
//    addMemberNodes(buffer, schema, entityIdPlan, index, entityPlan, "");
//    buffer.append(");" + NL);
//  }

  
  public void createElementTable (INodePlan elementPlan, String schema, String parentTableName, IItemPlan<?> entityIdPlan, int dimension, String prefix, Stack<ElementTable> queuedElementTables) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("CREATE TABLE ");
    buffer.append(parentTableName + '_' + dimension);
    buffer.append(" (" + NL);
    
    buffer.append("id SERIAL PRIMARY KEY");
    buffer.append("," + NL + "parent_id INTEGER REFERENCES ");
    buffer.append(parentTableName);
    buffer.append(" (id)");
    
    int[] index = new int[1];
    index[0] = 1;

    switch (elementPlan.getStructure()) {
    case ARRAY :
    case LIST :
      IRepeatingPlan elementPlan2 = (IRepeatingPlan)elementPlan;
      ElementTable elementTable = new ElementTable(elementPlan2.getElementPlan(), schema, parentTableName, elementPlan2.getDimension());
      queuedElementTables.add(elementTable);
      break;
    case EMBEDDED :
      addMemberNodes(buffer, schema, parentTableName, entityIdPlan, index, (IEmbeddedPlan<?>)elementPlan, "", queuedElementTables);
      break;
    case ENTITY :
      throw new IllegalArgumentException("IEntityPlan cannot be a child of a element");
    case INTERFACE :
      throw new NotYetImplementedException();
    case ITEM :
      buildItemSQL (buffer, entityIdPlan, index, (IItemPlan<?>)elementPlan, "");
      break;
    case MAP :
      throw new NotYetImplementedException();
    case REFERENCE :
      buildReferenceSQL (buffer, schema, index, (IReferencePlan<?>)elementPlan, "");
      break;
    case SET :
      throw new NotYetImplementedException();
    }
    buffer.append(");" + NL);
    
    System.out.println(buffer);
    IPreparedStatement stmt = conn.prepareStatement(buffer);
    stmt.executeUpdate();
//  existingTables.add(entityPlan);
  }

  
  public void createEntityTable (String schema, IEntityPlan<?> entityPlan) {
    String tableName = getTableName(schema, entityPlan);

    if (!createdTables.contains(tableName)) {
      createdTables.add(tableName);

      StringBuilder buffer = new StringBuilder();
      buffer.append("CREATE TABLE ");
      buffer.append(tableName);
      buffer.append(" (" + NL);
    
      int[] index = new int[1];
      IItemPlan<?> entityIdPlan = entityPlan.getIdPlan();
      Stack<ElementTable> queuedElementTables = new Stack<>();
      addMemberNodes (buffer, schema, tableName, entityIdPlan, index, entityPlan, "", queuedElementTables);
      buffer.append(");" + NL);
    
      System.out.println(buffer);
      IPreparedStatement stmt = conn.prepareStatement(buffer);
      stmt.executeUpdate();
    
      while (queuedElementTables.size() > 0) {
        ElementTable elementTable = queuedElementTables.pop();
        elementTable.createElementTable(queuedElementTables);
      }
    }
  }

  
//  public void createTableCommit (IConnectionFactory connFactory, IEntityPlan<?> entityPlan) {
//    try (IConnection conn = connFactory.getIConnection()) {
//      conn.setAutoCommit(false);
//      createTable(conn, entityPlan);
//    }
//  }
  
  
  public static void main (String[] args) {
    TableDropper tableDropper = new TableDropper(null);
    TableCreator tableCreator = new TableCreator(null);
    
    PlanFactory planFactory = new PlanFactory();

//    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(EntityWithArrayOfString.class);
//    tableCreator.createEntityTable("public", entityPlan);

    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(EntityWithArrayArrayOfString.class);
    tableDropper.dropEntityTable("public", entityPlan);
    tableCreator.createEntityTable("public", entityPlan);
  }
}
