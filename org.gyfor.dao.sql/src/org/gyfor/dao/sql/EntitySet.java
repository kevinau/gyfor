package org.gyfor.dao.sql;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.dao.DescriptionChangeListener;
import org.gyfor.dao.EntityDescription;
import org.gyfor.dao.IEntitySet;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;


@Component(configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
public class EntitySet implements IEntitySet {

  @Configurable(name="class", required=true)
  private String className;
  
  private IEntityPlan<?> plan;
  
  @Reference
  private PlanFactory planFactory;
  
  
  protected void activate (ComponentContext context) {
    ComponentConfiguration.load(this, context);
    plan = planFactory.getEntityPlan(className);  
    List<IItemPlan<?>> descPlans = plan.getDescriptionPlans();
    for (IItemPlan<?> descPlan : descPlans) {
      System.out.println("++++ " + descPlan);
    }
  }
  
  
  @Override
  public List<EntityDescription> getAllDescriptions() {
    return new ArrayList<>(0);
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
