package org.gyfor.object.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IValidationMethod;
import org.gyfor.object.type.IType;


public class ItemModel extends NodeModel implements EffectiveModeListener {

  private static class ErrorInstance {
    private ItemModel[] models;
    private UserEntryException exception;
  
    private ErrorInstance (ItemModel[] models, UserEntryException exception) {
      this.models = models;
      this.exception = exception;
    }
  }

  private final Map<Object, ErrorInstance> validationErrors = new HashMap<Object, ErrorInstance>();

  private final IValueReference valueRef;
  private final IItemPlan<?> itemPlan;
  private final IType<Object> type;

  private Object defaultValue;
  private String defaultSource = "";
  private boolean isComparedValueEqual = true;
  private boolean isComparedSourceEqual = true;
  private boolean allowEntryEvents = false;

  private Object referenceValue;
  private String referenceSource = "";
  
  private ComparisonBasis comparisonBasis = ComparisonBasis.DEFAULT;
  
  //private Object parentInstance;
  
  private Object itemValue = null;
  private String itemSource = "";
  private boolean itemValueInError = true;
  
  
  @SuppressWarnings("unchecked")
  public ItemModel (RootModel rootModel, ContainerModel parent, int id, IValueReference valueRef, IItemPlan<?> itemPlan) {
//  public ItemModel (IContainerModel parentModel, IContainerObject container, IItemPlan<?> itemPlan) {
    super (rootModel, parent, id);
    this.itemPlan = itemPlan;
    this.valueRef = valueRef;
    this.type = (IType<Object>)itemPlan.getType();
    
    // Add event listener so this field can react to effective mode changes
    addEffectiveModeListener(this);
    setInitialDefaultValue();
  }

  
  public ItemModel (RootModel rootModel, ContainerModel parent, int id, IValueReference valueRef, IItemPlan<?> itemPlan, Object value) {
    this (rootModel, parent, id, valueRef, itemPlan);
    setValue(value);
  }

  
  @Override
  public INodePlan getPlan () {
    return itemPlan;
  }


  @Override
  public boolean isItem () {
    return true;
  }
  
  
  /**
   * Add a FieldChangeListener.  
   * //If the conditions are right, fire off an event immediately.
   */
  public void addFieldEventListener (ItemEventListener x) {
    super.addEffectiveModeListener(x);
    super.addItemEventListener(x);
//    
//    if (isEventsActive()) {
//      // Fire all applicable events
//      x.sourceChange(this);
//      if (allowEntryEvents) {
//        x.comparisonBasisChange(this);
//        x.compareShowingChange(this, false);
//        x.compareEqualityChange(this);
//        if (validationErrors.isEmpty()) {
//          x.valueChange(this);
//          x.errorCleared(this);
//        } else {
//          for (ErrorInstance ei : validationErrors.values()) {
//            x.errorNoted(this, ei.exception);
//          }
//        }
//      } else {
//        x.valueChange(this);
//      }
//    }
  }
  
  
  /**
   * Clear an error identified by the sourceRef.  This method should only
   * be called if the Mode for the field allows entry events.
   */
  public void clearError (Object sourceRef) {
    ErrorInstance error = validationErrors.remove(sourceRef);
    if (error != null && validationErrors.isEmpty()) {
      getRoot().fireErrorCleared(this);
    }
  }


  private void clearDependentValidationErrors (Object sourceRef) {
    ErrorInstance error = validationErrors.remove(sourceRef);
    if (error != null) {
      ItemModel[] mx = error.models;
      if (mx == null) {
        clearError(sourceRef);
      } else {
        for (ItemModel m : mx) {
          m.clearError(sourceRef);
        }
      }
    }
  }


  /**
   * Note a validation error. This method should only be called if the Mode for
   * the field allows entry events.
   */
 public void noteValidationError (UserEntryException error) {
    ItemModel[] mx = {this};
    noteValidationError(this, mx, error);
  }


  /**
   * Note a validation error identified by sourceRef. This method should only be called if the Mode for
   * the field allows entry events.
   */
  public void noteValidationError (Object sourceRef, Exception ex) {
    ItemModel[] mx = {this};
    UserEntryException userError = new UserEntryException(ex.getMessage());
    noteValidationError(sourceRef, mx, userError);
  }


  /**
   * Note an error for this object.  The error is reported on the model that is associated with this
   * object.
   */
  public void noteValidationError (Object sourceRef, UserEntryException userError) {
    ItemModel[] mx = {this};
    noteValidationError(sourceRef, mx, userError);
  }


  /**
   * Note an error on an array of field models.
   * @param source - the source of the error.  This uniquely identifies this error.  The same object
   * must be used to clear the error.
   * @param models - an array of field models on which the error is noted.
   * @param userError - the error that is noted.
   */
  public void noteValidationError (Object sourceRef, ItemModel[] mx, UserEntryException userError) {
    ErrorInstance error = new ErrorInstance(mx, userError);
    validationErrors.put(sourceRef, error);
    
    if (allowEntryEvents) {
      getRoot().fireErrorNoted(this, userError);
    }
  }

  
  public void noteConversionError (Object source, UserEntryException userError) {
    ItemModel[] mx = {this};
    ErrorInstance error = new ErrorInstance(mx, userError);
    validationErrors.put(source, error);
    Object[] sources = validationErrors.keySet().toArray();
    for (Object ss : sources) {
      //if (ss.equals(source)) {
      //  // Leave this conversion error
      //} else
      if (ss instanceof IValidationMethod) {
        clearDependentValidationErrors(ss);
      }
    }
    getRoot().fireErrorNoted(this, userError);
  }


  public Object getDefaultValue() {
    return defaultValue;
  }


  public Object getReferenceValue() {
    return referenceValue;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue () {
    return (T)itemValue;
  }
  
  
  public boolean isComparedValueEqual() {
    return isComparedValueEqual;
  }


  public boolean isComparedSourceEqual() {
    return isComparedSourceEqual;
  }


  public ComparisonBasis getCompareBasis() {
    return comparisonBasis;
  }
  
  
  void setInitialDefaultValue () {
    defaultValue = valueRef.getValue();
    defaultSource = type.toEntryString(defaultValue, null);
  }
  
  
  public void setDefaultValue (Object value) {
    boolean defaultWasShowing = false;
    if (comparisonBasis == ComparisonBasis.DEFAULT) {
      defaultWasShowing = (defaultValue == null ? itemValue == null : defaultValue.equals(itemValue));
    }
    defaultValue = value;
    defaultSource = type.toEntryString(value, null);
    if (comparisonBasis == ComparisonBasis.DEFAULT) {
      if (defaultWasShowing) {
        setRawValue(value, null, true);
      } else {
        testAndFireSourceEqualityChange(false);
        if (validationErrors.isEmpty()) {
          testAndFireValueEqualityChange();
        }
      }
    }
  }

  
  public void setReferenceValue (Object value) {
    boolean referenceWasShowing = false;
    if (comparisonBasis == ComparisonBasis.REFERENCE) {
      referenceWasShowing = (referenceValue == null ? itemValue == null : referenceValue.equals(itemValue));
    }
    referenceValue = value;
    referenceSource = type.toEntryString(value, null);
    if (comparisonBasis == ComparisonBasis.REFERENCE) {
      if (referenceWasShowing) {
        setRawValue(value, null, true);
      } else {
        testAndFireSourceEqualityChange(false);
        if (validationErrors.isEmpty()) {
          testAndFireValueEqualityChange();
        }
      }
    }
  }


//  @Override
//  public void fireInitialEvents() {
//    super.fireInitialEvents();
//    if (fieldValue != IFieldModel.ERRONEOUS) {
//      fireValueChange(fieldValue, fieldSource, null);
//      fireErrorCleared();
//    } else {
//      fireValueChangeAttempt(fieldSource);
//    }
//  }
  
  
  public String getValueAsSource () {
    return itemSource;
  }
  
  
  public void setSourceFromValue (Object value) {
    if (!itemValueInError) {
      itemSource = type.toEntryString(value, null);
      setRawValue(value, null, false);
    }
  }
  
  
  @Override
  public void setValue(Object value) {
    String source = type.toEntryString(value, null);
    itemSource = source;
    if (value == null ? itemValue != null : !value.equals(itemValue)) {
      getRoot().fireValueChange(this, null);
    }
    
    testAndFireSourceEqualityChange(true);
    try {
      boolean creating = true;
      Object newValue = type.createFromString(null, itemPlan.isOptional(), creating, source);
      setRawValue(newValue, null, true);
    } catch (UserEntryException ex) {
      itemValueInError = true;
      testAndFireValueEqualityChange();
      noteValidationError (ex);
    }
  }

  
  public void resetToInitial () {
    super.resetToInitial();
    setValue (defaultValue);
  }
  
  
  public void setValueFromPrime () {
    Object primalValue = itemPlan.getType().primalValue();
    String source = type.toEntryString(primalValue, null);
    itemSource = source;
    if (primalValue == null ? itemValue != null : !primalValue.equals(itemValue)) {
      fireSourceChange(this, null);
    }
    
    testAndFireSourceEqualityChange(true);
    try {
      boolean creating = true;
      Object newValue = type.createFromString(null, itemPlan.isOptional(), creating, source);
      setRawValue(newValue, null, true);
    } catch (UserEntryException ex) {
      itemValueInError = true;
      testAndFireValueEqualityChange();
      noteValidationError (ex);
    }
  }
  
  
  public void setValueFromDefault () {
    String source = type.toEntryString(defaultValue, null);
    itemSource = source;
    if (defaultValue == null ? itemValue != null : !defaultValue.equals(itemValue)) {
      fireSourceChange(this, null);
    }
    
    testAndFireSourceEqualityChange(true);
    try {
      boolean creating = true;
      Object newValue = type.createFromString(null, itemPlan.isOptional(), creating, source);
      setRawValue(newValue, null, true);
    } catch (UserEntryException ex) {
      itemValueInError = true;
      testAndFireValueEqualityChange();
      noteValidationError (ex);
    }
  }
  
  
  public void setValueFromReference () {
    String source = type.toEntryString(referenceValue, null);
    itemSource = source;
    if (referenceValue == null ? itemValue != null : !referenceValue.equals(itemValue)) {
      fireSourceChange(this, null);
    }
    
    testAndFireSourceEqualityChange(true);
    try {
      boolean creating = true;
      Object newValue = type.createFromString(null, itemPlan.isOptional(), creating, source);
      setRawValue(newValue, null, true);
    } catch (UserEntryException ex) {
      itemValueInError = true;
      testAndFireValueEqualityChange();
      noteValidationError (ex);
    }
  }
  
  
  public void setReferenceFromValue () {
    if (!itemValueInError) {
      setReferenceValue (itemValue);
    }
  }
  
  
  public void setValueFromDefaultOrReference () {
    switch (comparisonBasis) {
    case NONE :
      setValueFromPrime();
      break;
    case DEFAULT :
      setValueFromDefault();
      break;
    case REFERENCE :
      setValueFromReference();
      break;
    }
  }

  
  public void setValueFromSource(String source, ItemEventListener self, boolean creating) {
    if (!itemSource.equals(source)) {
      itemSource = source;
      fireSourceChange(this, self);
    } else {
      itemSource = source;
    }
    
    testAndFireSourceEqualityChange(true);
    try {
      Object newValue = type.createFromString(null, itemPlan.isOptional(), creating, source);
      setRawValue (newValue, self, true);
    } catch (UserEntryException ex) {
      itemValueInError = true;
      testAndFireValueEqualityChange();
      // noteConversionError includes the processing of noteValidationError
      noteConversionError (this, ex);
    }
  }

  
  public void setValueFromSource(String source, ItemEventListener self) {
    setValueFromSource(source, null, true);
  }


  public void setValueFromSource(String source) {
    setValueFromSource(source, null, true);
  }


  private void setRawValue (Object value, ItemEventListener self, boolean fireValueChangeEvents) {
    // Clear any conversion error, but do not clear validation errors.  Validation
    // checking will not have been done as a consequence of conversion errors,
    // so we don't have to clear validation errors if we clear conversion errors.
    clearError(this);

    // Change the value in the instance
    if (itemValueInError) {
      // The value is being set when previously it was in error, so let others know
      itemValue = value;
      itemValueInError = false;
      valueRef.setValue(value);
      if (fireValueChangeEvents) {
        fireValueChange(this, self);
      }
    } else {
      if (value == null ? itemValue != null : !value.equals(itemValue)) {
        // Set the new value.
        itemValue = value;
        itemValueInError = false;
        valueRef.setValue(value);
        // Firing a value change will trigger validation
        if (fireValueChangeEvents) {
          fireValueChange(this, self);
        }
      }
    }
    testAndFireValueEqualityChange();
  }
  
  
  @Override
  public String toEntryString (Object value) {
    return type.toEntryString(value, null);
  }
  
  
  private void testAndFireValueEqualityChange () {
    if (allowEntryEvents) { 
      boolean ce;
      if (validationErrors.isEmpty()) {
        if (itemValueInError) {
          ce = true;
        } else {
          switch (comparisonBasis) {
          case DEFAULT :
            ce = defaultValue == null ? itemValue == null : defaultValue.equals(itemValue);
            break;
          case REFERENCE :
            ce = referenceValue == null ? itemValue == null : referenceValue.equals(itemValue);
            break;
          default :
            ce = true;
            break;
          }
        }
      } else {
        ce = true;
      }
      if (isComparedValueEqual != ce) {
        isComparedValueEqual = ce;
        fireCompareEqualityChange(this); 
      }
    }
  }
  
  
  private void testAndFireSourceEqualityChange (boolean isSourceTrigger) {
    if (allowEntryEvents) { 
      boolean cs;
      switch (comparisonBasis) {
      case DEFAULT :
        cs = defaultSource.equals(itemSource);
        break;
      case REFERENCE :
        cs = referenceSource.equals(itemSource);
        break;
      default :
        cs = true;
        break;
      }
      if (isComparedSourceEqual != cs) {
        isComparedSourceEqual = cs;
        fireCompareShowingChange(this, isSourceTrigger);
      }
    }
  }
  
  
  @Override
  public void modeChange (IObjectModel model) {
    boolean aee = getEffectiveMode().allowEntryEvents();
    if (allowEntryEvents != aee) {
      allowEntryEvents = aee;
      if (allowEntryEvents) {
        // Fire all initial events
        testAndFireSourceEqualityChange(false);
        testAndFireValueEqualityChange();
//        if (errorStateChange) {
//          if (validationErrors.isEmpty()) {
//            fireErrorCleared();
//          } else {
//            for (ErrorInstance ei : validationErrors.values()) {
//              fireErrorNoted(ei.exception);
//            }
//          }
//        }
      } else {
        // Clear field events
        if (!isComparedSourceEqual) {
          isComparedSourceEqual = true;
          fireCompareShowingChange(this, false);
        }
        if (!isComparedValueEqual) {
          isComparedValueEqual = true;
          fireCompareEqualityChange(this);
        }
        if (!validationErrors.isEmpty()) {
          validationErrors.clear();
          fireErrorCleared(this);
        }
      }
    }
  }
 
  
  @Override
  public void setCompareBasis (ComparisonBasis compareBasis) {

    if (this.comparisonBasis != compareBasis) {
//      // Clear any "before" showing and equality
//      if (isCompareShowing == false) {
//        isCompareShowing = true;
//        fireCompareShowingChange(isCompareShowing, false);
//      }
//      if (isCompareEqual == false) {
//        isCompareEqual = true;
//        fireCompareEqualityChange();
//      }
//      
      this.comparisonBasis = compareBasis;

      // Set the showing and equality based on the new comparison basis
      testAndFireSourceEqualityChange(false);
      testAndFireValueEqualityChange();
    }
  }
  

//  @Override
//  protected void setEffectiveMode (EffectiveMode newEffectiveMode, boolean fireEvents) {
//    EffectiveMode oldEffectiveMode = getEffectiveMode();
//    boolean postProcess = false;
//    if (newEffectiveMode != oldEffectiveMode) {
//      if (newEffectiveMode == EffectiveMode.NA) {
//        // We are changing to NA, and we want to ignore any errors (we still capture them,
//        // but we do not report them).  Ditto default showing and reference showing.
//        
//        // Clear errors
//        if (!validationErrors.isEmpty()) {
//          fireErrorCleared();
//        }
////        if (isCompareShowing) {
////          fireCompareShowingChange(false, false);
////        }
//      }
//      if (oldEffectiveMode == EffectiveMode.NA) {
//        postProcess = true;
//      }
//    }
//    super.setEffectiveMode(newEffectiveMode, fireEvents);
//    if (postProcess) {
//      // We are changing from NA to something else, so report any errors.
//      Collection<ErrorInstance> errors = validationErrors.values();
//      for (ErrorInstance error : errors) {
//        fireErrorNoted(error.exception);
//      }
////      if (isCompareShowing) {
////        fireCompareShowingChange(true, false);
////      }
//    }
//  }
  
  
  @Override
  public boolean isInError () {
    EffectiveMode effectiveMode = getEffectiveMode();
    if (effectiveMode == EffectiveMode.NA) {
      return false;
    } else {
      Collection<ErrorInstance> errors = validationErrors.values();
      for (ErrorInstance error : errors) {
        UserEntryException ev = error.exception;
        UserEntryException.Type type = ev.getType();
        if (type.isFatal()) {
          return true;
        }
      }
      return false;
    }
  }
  
  
  @Override
  public UserEntryException.Type getStatus() {
    EffectiveMode effectiveMode = getEffectiveMode();
    if (effectiveMode == EffectiveMode.NA) {
      return UserEntryException.Type.OK;
    } else {
      int incompleteCount = 0;
      int requiredCount = 0;
      int warningCount = 0;
      Collection<ErrorInstance> errors = validationErrors.values();
      for (ErrorInstance error : errors) {
        UserEntryException ev = error.exception;
        UserEntryException.Type type = ev.getType();
        switch (type) {
        case ERROR:
          return UserEntryException.Type.ERROR;
        case INCOMPLETE:
          incompleteCount++;
          break;
        case REQUIRED:
          requiredCount++;
          break;
        case WARNING:
          warningCount++;
          break;
        case OK :
          break;
        }
      }
      if (incompleteCount > 0) {
        return UserEntryException.Type.INCOMPLETE;
      }
      if (requiredCount > 0) {
        return UserEntryException.Type.REQUIRED;
      }
      if (warningCount > 0) {
        return UserEntryException.Type.WARNING;
      }
      return UserEntryException.Type.OK;
    }
  }  
  
  
  @Override
  public void fireInitialFieldEvents (ItemEventListener x, boolean isSourceTrigger) {
    x.sourceChange(this);

    if (allowEntryEvents) {
      x.comparisonBasisChange(this);
      x.compareShowingChange(this, isSourceTrigger);
      x.compareEqualityChange(this);
      if (validationErrors.isEmpty()) {
        x.valueChange(this);
        x.errorCleared(this);
      } else {
        for (ErrorInstance ei : validationErrors.values()) {
          x.errorNoted(this, ei.exception);
        }
      }
    } else {
      x.valueChange(this);
    }
  }

  
  @Override
  public UserEntryException.Type getStatus (int order) {
    int incompleteCount = 0;
    int requiredCount = 0;
    int warningCount = 0;
    for (Map.Entry<Object, ErrorInstance> entry : validationErrors.entrySet()) {
      if (entry.getKey() instanceof IValidationMethod) {
        IValidationMethod vMethod = (IValidationMethod)entry.getKey();
        if (vMethod.getOrder() >= order) {
          // Ignore errors that will be re-checked later, and, if no longer an error,
          // it will be cleared.
          continue;
        }
      }
      UserEntryException ev = entry.getValue().exception;
      UserEntryException.Type type = ev.getType();
      switch (type) {
      case ERROR :
        return UserEntryException.Type.ERROR;
      case INCOMPLETE :
        incompleteCount++;
        break;
      case REQUIRED :
        requiredCount++;
        break;
      case WARNING :
        warningCount++;
        break;
      case OK :
        break;
      }
    }
    if (incompleteCount > 0) {
      return UserEntryException.Type.INCOMPLETE;
    }
    if (requiredCount > 0) {
      return UserEntryException.Type.REQUIRED;
    }
    if (warningCount > 0) {
      return UserEntryException.Type.WARNING;
    }
    return UserEntryException.Type.OK;
  }
  
  /*
   * TODO This needs to be changed to be presentation agnostic.
   */
  @Override
  public String getStatusMessage () {
    int n = validationErrors.size();
    if (n == 0) {
      return "";
    } else if (n == 1) {
      UserEntryException ex = validationErrors.values().iterator().next().exception;
//      return ex.getType().getPrefix() + ": " + ex.getMessage();
      return ex.getMessage();
    } else {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<html>");
      UserEntryException.Type tx = null;
      for (ErrorInstance error : validationErrors.values()) {
        UserEntryException ex = error.exception;
        if (tx != ex.getType()) {
          if (tx != null) {
            buffer.append("<br>\n");
          }
//          tx = ex.getType();
//          buffer.append(tx.getPrefix());
//          buffer.append(": ");
        }
        buffer.append("<br>- ");
        buffer.append(ex.getMessage());
      }
      buffer.append("</html>");
      return buffer.toString();
    }
  }


  @Override
  public UserEntryException[] getErrors () {
    int n = validationErrors.size();
    UserEntryException[] errors = new UserEntryException[n];
    int i = 0;
    for (ErrorInstance error : validationErrors.values()) {
      errors[i++] = error.exception;
    }
    return errors;
  }


  @Override
  public void removeFieldEventListener(ItemEventListener x) {
    super.removeFieldEventListener(x);
    super.removeEffectiveModeListener(x);
    // Notify listeners if this model was in error
    for (Object source : validationErrors.values()) {
      clearError(source);
    }
  }


//  @Override
//  public void setLastEntryValue(Object value) {
//    Field lastEntryField = itemPlan.getLastEntryField();
//    if (lastEntryField != null) {
//      try {
//        lastEntryField.set(parentInstance, value);
//      } catch (IllegalArgumentException ex) {
//        throw new RuntimeException(ex);
//      } catch (IllegalAccessException ex) {
//        throw new RuntimeException(ex);
//      }
//    }
//  }


  @Override
  public void dump(int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
    System.out.println(getPathName() + " = " + getValue() + " [" + super.getEffectiveMode() + " " + itemPlan + "]");
  }


  @Override
  public Collection<IObjectModel> getChildren() {
    return Collections.emptyList();
  }


  protected void accumulateFields (List<IFieldModel> list) {
    list.add(this);
  }
  
  
  @Override
  public boolean walkFields (IFieldVisitable x) {
    return x.visit(this);
  }


  @Override
  public IType<?> getType() {
    return itemPlan.getType();
  }
  
  
  @Override
  public void revalidate () {
    fireValueChange(this, null);
  }
  
  
  @Override
  public void setEventsActive (boolean fireEvents) {
    super.setEventsActive(fireEvents);
    if (fireEvents) {
      fireSourceChange(this, null);

      if (allowEntryEvents) {
        fireComparisonBasisChange(this);
        fireCompareShowingChange(this, false);
        fireCompareEqualityChange(this);
        if (validationErrors.isEmpty()) {
          fireValueChange(this, null);
          fireErrorCleared(this);
        } else {
          for (ErrorInstance ei : validationErrors.values()) {
            fireErrorNoted(this, ei.exception);
          }
        }
      } else {
        fireValueChange(this, null);
      }

    }
  }
  
}
