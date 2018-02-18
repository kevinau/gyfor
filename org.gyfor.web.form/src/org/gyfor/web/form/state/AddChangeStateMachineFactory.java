package org.gyfor.web.form.state;

import java.util.Map;

import org.gyfor.object.EntryMode;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ItemEventAdapter;
import org.osgi.service.component.annotations.Reference;

// TODO Change this class to extend AddOnlyStatemachineFactory
public class AddChangeStateMachineFactory implements IStateMachineFactory {

  @Reference 
  private IModelFactory modelFactory;
  
  
  protected enum Action {
    START_FETCH,
    
    CONFIRM_FETCH,
    
    @ActionLabel(label="New", description="Create a new {}")
    START_ADD, 
    
    @ActionLabel(label="Add", description="Save the {}")
    @RequiresValidEntry
    CONFIRM_ADD,
    
    START_EDIT,
    
    @RequiresValidEntry
    START_CHANGE,
    
    CONFIRM_CHANGE,
    
    START_REMOVE,
    
    CONFIRM_REMOVE,
    
    CLEAR, 
    
    CANCEL;
  };

  protected enum State {
    CLEAR,
    FETCHING,
    ADDING,
    SHOWING,
    EDITING,
    CHANGING,
    REMOVING;
  };

  private class Search {
    @FormField()
    public void setSearch (String search) {
      this.search = search;
    }
  }
  @SuppressWarnings("unchecked")
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
    
    IItemModel searchModel = modelFactory.buildItemModel(this.getClass(), "search")
    searchModel = (IItemModel)args[1];
    
    TransitionFunction<State> startAdding = () -> {
      entityModel.setEntryMode(EntryMode.ENABLED);
      //Object newInstance = entityModel.newInstance();
      //entityModel.setValue(newInstance);
      return State.ADDING;
    };
    
    TransitionFunction<State> startEditing = () -> {
      entityModel.setEntryMode(EntryMode.ENABLED);
      //Object newInstance = entityModel.newInstance();
      //entityModel.setValue(newInstance);
      return State.EDITING;
    };
    
    TransitionFunction<State> startChanging = () -> {
      entityModel.setEntryMode(EntryMode.ENABLED);
      //Object newInstance = entityModel.newInstance();
      //entityModel.setValue(newInstance);
      return State.CHANGING;
    };
    
    TransitionFunction<State> startRemoving = () -> {
      entityModel.setEntryMode(EntryMode.ENABLED);
      //Object newInstance = entityModel.newInstance();
      //entityModel.setValue(newInstance);
      return State.REMOVING;
    };
    
    TransitionFunction<State> confirmAdd = () -> {
      entityModel.setEntryMode(EntryMode.DISABLED);
      Object instance = entityModel.getValue();
      System.out.println("       adding " + instance);
      return State.SHOWING;
    };
    
    TransitionFunction<State> clearForm = () -> {
      entityModel.setEntryMode(EntryMode.HIDDEN);
      entityModel.setValue(entityModel.newInstance());
      return State.CLEAR;
    };
    
    StateMachine<State, Action> sm = new StateMachine<State, Action>(State.class, Action.class, clearForm);
    sm.addTransition(State.CLEAR, Action.START_ADD, startAdding);
    sm.addTransition(State.CLEAR, Action.START_FETCH, startFetch);
    sm.addTransition(State.FETCHING, Action.CONFIRM_FETCH, confirmFetch);
    sm.addTransition(State.FETCHING, Action.CLEAR, clearForm);
    sm.addTransition(State.ADDING, Action.CANCEL, clearForm);
    sm.addTransition(State.ADDING, Action.CONFIRM_ADD, confirmAdd);
    sm.addTransition(State.SHOWING, Action.CLEAR, clearForm);
    sm.addTransition(State.SHOWING, Action.START_ADD, startAdding);
    sm.addTransition(State.SHOWING, Action.START_EDIT, startEditing);
    sm.addTransition(State.EDITING, Action.START_REMOVE, startRemove);
    sm.addTransition(State.EDITING, Action.CANCEL, resetShowing);
    sm.addTransition(State.CHANGING, Action.CONFIRM_CHANGE, confirmChange);
    sm.addTransition(State.CHANGING, Action.CANCEL, resetShowing);
    sm.addTransition(State.REMOVING, Action.CONFIRM_REMOVE, confirmRemove);
    sm.addTransition(State.REMOVING, Action.CANCEL, resetShowing);

    searchModel.addItemEventListener(new ItemEventAdapter() {
      @Override
      public void valueChange(INodeModel nodeModel) {
        String value = nodeModel.getValue();


      }
    });

    entityModel.addItemEventListener(new ItemEventAdapter() {
      @Override
      public void valueEqualityChange(INodeModel nodeModel, boolean equal) {
        if (equal) {
          sm.transition(State.EDITING, startChanging);
        } else {
          sm.transition(State.CHANGING, resetEditing);
        }
      }
    });

    return sm;
  }

}
