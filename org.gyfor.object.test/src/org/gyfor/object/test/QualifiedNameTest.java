package org.gyfor.object.test;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.PlanFactory;
import org.junit.Assert;
import org.junit.Test;


public class QualifiedNameTest {
  
  @Test
  public void testQualifiedNames() {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel entity = modelFactory.buildEntityModel(Party.class);
    entity.setValue(new Party("Kevin Holloway", "17", "Burwood Avenue", "Nailsworth"));
    
    IItemModel nameModel = entity.selectItemModel("name");
    Assert.assertNotNull(nameModel);
    String qname = nameModel.getQualifiedName();
    Assert.assertEquals("org.gyfor.object.test.Party#name", qname);
    
    IItemModel homeModel = entity.selectItemModel("home.suburb");
    Assert.assertNotNull(homeModel);
    qname = homeModel.getQualifiedName();
    Assert.assertEquals("org.gyfor.object.test.Party#home.suburb", qname);
    
    IItemModel suburbModel = entity.selectItemModel("locations.*.*.number");
    Assert.assertNotNull(suburbModel);
    qname = suburbModel.getQualifiedName();
    Assert.assertEquals("org.gyfor.object.test.Party#locations.street.number", qname);
  }

}
