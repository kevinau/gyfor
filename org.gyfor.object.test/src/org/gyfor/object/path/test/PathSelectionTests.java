package org.gyfor.object.path.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.path.PathParser;
import org.plcore.userio.plan.PlanFactory;


@RunWith(Parameterized.class)
public class PathSelectionTests {

  @Parameters(name= "{index}: ''{0}'' count {1}")
  public static Iterable<Object[]> data() {
      return Arrays.asList(new Object[][] {
        {"field1", 1, "Kevin"},
        {"field2", 1, "Holloway"},
        {"*", 4, null},
        {"**", 8, null},
        {"inner/inner1", 1, "0447 252 976"},
        {"inner/*", 1, "0447 252 976"},
        {"*/inner1", 1, "0447 252 976"},
        {"**/inner1", 1, "0447 252 976"},
        {"array/0", 1, "One"},
        {"array/*", 3, null},
        {"array/-1", 1, "Three"},
      });
  }

  @Entity
  public static class Inner {
    @IOField 
    private String inner1;
  }
  
  @Entity
  public static class Test1 {
    @IOField
    private String field1;
    @IOField
    private String field2;
    @IOField
    private Inner inner;
    @IOField 
    private String[] array;
  }
  
  private String expr;
  private int count;
  private String found;
  
  private IEntityModel model;
  
  public PathSelectionTests (String expr, int count, String found) {
    this.expr = expr;
    this.count = count;
    this.found = found;
  }
  
  
  @Before
  public void setup() {
    PlanFactory planFactory = new PlanFactory();
    
    ModelFactory modelFactory = new ModelFactory(planFactory);
    model = modelFactory.buildEntityModel(Test1.class);
    Test1 test1 = new Test1();
    test1.field1 = "Kevin";
    test1.field2 = "Holloway";
    Inner inner = new Inner();
    inner.inner1 = "0447 252 976";
    test1.inner = inner;
    test1.array = new String[] {
        "One",
        "Two",
        "Three",
    };
    model.setValue(test1);
  }
  
  
  @Test
  public void nodePathSelect () {
    IPathExpression pathExpr = PathParser.parse(expr);
    List<INodeModel> nodes = model.selectNodeModels(pathExpr);
    Assert.assertEquals(count, nodes.size());
    
    if (count == 1) {
      Assert.assertEquals(found, nodes.get(0).getValue());
    }
  }


  @Test
  public void nodePathMatches () {
    IPathExpression pathExpr = PathParser.parse(expr);
    List<INodeModel> nodes = model.selectNodeModels(pathExpr);
    
    if (count >= 1) {
      INodeModel node = nodes.get(0);
      boolean isMatched = node.matches(model, pathExpr);
      Assert.assertTrue("Selected node " + node + " does not match " + pathExpr, isMatched);
    }
  }

}
