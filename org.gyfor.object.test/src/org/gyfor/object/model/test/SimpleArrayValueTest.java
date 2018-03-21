package org.gyfor.object.model.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.plan.PlanFactory;


public class SimpleArrayValueTest {

  public static class Zoo {
    @IOField
    private String[] names;
    
    public Zoo (String... names) {
      this.names = names;
    }

    public String[] getNames() {
      return names;
    }
    
    public void setNames(String[] names) {
      this.names = names;
    }

  }
  
  @Test
  public void testElementValues() {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel entity = modelFactory.buildEntityModel(Zoo.class);
    
    Zoo zoo = new Zoo("Lion", "Elephant", "Otter", "Bear");
    entity.setValue(zoo);
    
    List<IItemModel> elemModels = entity.selectItemModels("**");
    Assert.assertEquals(4, elemModels.size());
    
    IItemModel elemModel = entity.selectItemModel("names/0");
    Assert.assertNotNull(elemModel);
    String elemValue = elemModel.getValue();
    Assert.assertEquals("Lion", elemValue);
    
    elemModel = entity.selectItemModel("names/-1");
    Assert.assertNotNull(elemModel);
    elemValue = elemModel.getValue();
    Assert.assertEquals("Bear", elemValue);

    IItemModel elem2Model = entity.selectItemModel("names/2");
    Assert.assertNotNull(elem2Model);
    String elem2Value = elem2Model.getValue();
    Assert.assertEquals("Otter", elem2Value);
    
    // Change the entity model value
    elem2Model.setValue("Seal");
    Assert.assertEquals("Seal", zoo.names[2]);
    
  }

}
