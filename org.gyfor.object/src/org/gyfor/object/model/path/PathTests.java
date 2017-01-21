package org.gyfor.object.model.path;

import java.util.function.Consumer;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityPlanFactory;
import org.gyfor.object.model.path.parser.PlanPathParser;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.impl.PlanContext;


public class PathTests {

  @Entity
  @SuppressWarnings("unused")
  public static class Test1 {
    private String field1;
    private String field2;
  }
  
  public static void main (String[] args) throws Exception {
    PlanContext context = new PlanContext();
    
    Test1 entity1 = new Test1();
    
    IEntityPlan<Test1> plan1 = EntityPlanFactory.getEntityPlan(context, entity1);

    String[] tests = {
        "field1",
        "field2",
        "*",
    };
    
    for (String test : tests) {
      IPathExpression pathExpr1 = new PlanPathParser().parse(test);
      pathExpr1.dump();
      pathExpr1.matches(plan1, null, new Consumer<INodePlan>() {
 
        @Override
        public void accept(INodePlan plan) {
          System.out.println("Visiting: " + plan);
        }
      
      });
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
