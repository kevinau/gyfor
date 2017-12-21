package org.gyfor.dao.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.gyfor.dao.DescriptionChangeListener;
import org.gyfor.dao.IEntitySet;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.value.EntityDescription;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IConnectionFactory;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;


@Component(configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
public class EntitySet implements IEntitySet {

  @Configurable(name="class", required=true)
  private String className;
  
  @Configurable
  private String schema;
  
  @Reference
  public PlanFactory planFactory;
  
  @Reference
  private IConnectionFactory connFactory;

  
  private IEntityPlan<?> entityPlan;
  private SQLBuilder sqlBuilder;
  
  
  protected void activate (ComponentContext context) {
    ComponentConfiguration.load(this, context);

    try {
      entityPlan = planFactory.getEntityPlan(className);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    sqlBuilder = new SQLBuilder(entityPlan, schema);
    
    // Testing
    List<EntityDescription> descriptions = getAllDescriptions();
    System.out.println("+++++++++ " + className);
    for (EntityDescription desc : descriptions) {
      System.out.println("+++++++++ " + desc);
    }
    System.out.println("+++++++++ ");
  }
  
  
  private <R> List<R> getAll(SQLBuilder.Expression fetchSql, Function<Object, R> function) {
    List<R> results = new ArrayList<>();
    
    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt = conn.prepareStatement(fetchSql.sql())) {
       
      IResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        Object instance = entityPlan.newInstance(fetchSql.sqlPlans(), rs);
        R result = function.apply(instance);
        results.add(result);
      }
    }
    return results;
  }
  
  
  @Override
  public List<EntityDescription> getAllDescriptions() {
    SQLBuilder.Expression sql = sqlBuilder.getFetchDescriptionAllSql();
    List<EntityDescription> descriptions = getAll(sql, instance -> {
      return entityPlan.getDescription(instance);
    });
    return descriptions;
  }

  
  @Override
  public void addDescriptionChangeListener(DescriptionChangeListener x) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeDescriptionChangeListener(DescriptionChangeListener x) {
    // TODO Auto-generated method stub
    
  }

}
