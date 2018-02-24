package org.gyfor.object.path.test;

import java.util.List;
import java.util.function.Consumer;

import org.gyfor.object.Entity;
import org.gyfor.object.INode;
import org.gyfor.object.IOField;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.path2.IPathExpression;
import org.gyfor.object.path2.PathParser;
import org.gyfor.object.plan.PlanFactory;


public class PathTests {

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
  }
  
  public static void main (String[] args) throws Exception {
    PlanFactory planFactory = new PlanFactory();
    
    ModelFactory modelFactory = new ModelFactory(planFactory);
    IEntityModel model1 = modelFactory.buildEntityModel(Test1.class);
    Test1 test1 = new Test1();
    test1.field1 = "Kevin";
    test1.field2 = "Holloway";
    Inner inner = new Inner();
    inner.inner1 = "0447 252 976";
    test1.inner = inner;
    model1.setValue(test1);

    Object[] tests = {
        "field1", 1,
        "field2", 1,
        "*", 3,
        "**", 4,
        "inner/inner1", 1 + 1,
        "inner/*", 1 + 1,
        "*/inner1", 1 + 1,
        "**/inner1", 1 + 1,
    };
    
    final int[] count = new int[1];
    for (int i = 0; i < tests.length; i += 2) {
      String test = (String)tests[i];
      int expected = (Integer)tests[i + 1];
      count[0] = 0;
      System.out.println();
      System.out.println("Testing: " + test);
      IPathExpression<INode> pathExpr1 = PathParser.parse(test);
      pathExpr1.dump();
      pathExpr1.matches(model1, null, new Consumer<INode>() {
 
        @Override
        public void accept(INode node) {
          System.out.println("Visiting: " + node.getName());
          count[0]++;
        }
      
      });
      //System.out.println("Found " + count[0] + " when expecting " + expected);
      
      List<IItemModel> nodes = model1.selectItemModels(test);
      for (IItemModel node : nodes) {
        model1.dump();
        pathExpr1.dump();
        boolean isMatched = node.matches(model1, pathExpr1);
        System.out.println(node + " matches expression " + isMatched);
      }
    }
  }
  
  
//  public static void main (String[] args) throws Exception {
//    RootModel rootModel = new RootModel();
//    PlanContext context = new PlanContext();
//    
//    Test1 entity1 = new Test1();
//    
//    IEntityPlan<Test1> plan1 = EntityPlanFactory.getEntityPlan(context, entity1);
//    EntityModel model1 = new EntityModel(rootModel, plan1);
//
//    IPathExpression pathExpr1 = new PlanPathParser().parse("field1");
//    pathExpr1.dump();
//    pathExpr1.matches(model1, null, new INodeVisitable() {
//
//      @Override
//      public void visit(NodeModel model) {
//        System.out.println("Visiting: " + model);
//      }
//      
//    });
//  }

}
