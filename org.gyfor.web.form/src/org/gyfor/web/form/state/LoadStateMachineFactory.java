package org.gyfor.web.form.state;

import java.util.Map;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component
public class LoadStateMachineFactory implements IStateMachineFactory {

  @Reference 
  private IModelFactory modelFactory;
  
  
  private enum Action {
    @ActionLabel(label="New", description="Create a new {}")
    START_ADD, 
    
    @ActionLabel(label="Add", description="Save the {}")
    @RequiresValidEntry
    CONFIRM_ADD,
    
    CANCEL;
  };


  private enum State {
    ADDING;
  };

  
  @Override
  public StateMachine<State, Action> getStateMachine(Map<String, Object> props) {
    String entityClassName = (String)props.get("entity");
    if (entityClassName == null) {
      throw new IllegalArgumentException("No 'entity' property");
    }
    IEntityModel entityModel;
    try {
      entityModel = modelFactory.buildEntityModel(entityClassName);
    } catch (ClassNotFoundException ex) {
      throw new IllegalArgumentException(ex);
    }
    
    TransitionFunction<State> startAdding = () -> {
      System.out.println("startAdding ..........");
      entityModel.setValue(entityModel.newInstance());
      return State.ADDING;
    };
    
    TransitionFunction<State> confirmAdd = () -> {
      System.out.println("confirmAdd ..........");
      Object instance = entityModel.getValue();
      System.out.println("       adding " + instance);
      entityModel.setValue(entityModel.newInstance());
      return State.ADDING;
    };
    
    StateMachine<State, Action> sm = new StateMachine<State, Action>(State.class, Action.class, startAdding);
    sm.addTransition(State.ADDING, Action.CONFIRM_ADD, confirmAdd);
    sm.addTransition(State.ADDING, Action.CANCEL, startAdding);
    return sm;
  }

}
