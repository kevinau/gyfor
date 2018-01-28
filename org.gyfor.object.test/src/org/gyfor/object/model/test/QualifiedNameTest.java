package org.gyfor.object.model.test;

import java.util.List;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.test.data.Party;
import org.junit.Assert;
import org.junit.Test;


public class QualifiedNameTest {
  
  @Test
  public void testQualifiedNames() {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel entity = modelFactory.buildEntityModel(Party.class);
    entity.setValue(new Party("Kevin Holloway", "17", "Burwood Avenue", "Nailsworth"));
    entity.dump();
    System.out.println();
    
    StringBuilder builder = new StringBuilder();
    
    IItemModel nameModel = entity.selectItemModel("name");
    Assert.assertNotNull(nameModel);
    String qname = nameModel.getQName();
    Assert.assertEquals("name", qname);
    
    builder.setLength(0);
    IItemModel homeModel = entity.selectItemModel("home.suburb");
    Assert.assertNotNull(homeModel);
    qname = homeModel.getQName();
    Assert.assertEquals("home/suburb", qname);
    
    builder.setLength(0);
    List<INodeModel> locationsModel = entity.selectNodeModels("locations.*");
    Assert.assertNotNull(locationsModel);
    Assert.assertEquals(2, locationsModel.size());

    builder.setLength(0);
    IItemModel locationsModel2 = entity.selectItemModel("locations.*.suburb");
    Assert.assertNotNull(locationsModel2);
    qname = locationsModel2.getQName();
    Assert.assertEquals("locations/0/suburb", qname);

    builder.setLength(0);
    IItemModel suburbModel = entity.selectItemModel("locations.*.*.number");
    Assert.assertNotNull(suburbModel);
    qname = suburbModel.getQName();
    Assert.assertEquals("locations/0/street/number", qname);
  }

}
