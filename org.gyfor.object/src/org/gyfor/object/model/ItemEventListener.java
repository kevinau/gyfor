package org.gyfor.object.model;

import java.util.EventListener;

import org.gyfor.object.UserEntryException;

/**
 * @author Kevin Holloway
 * 
 */
public interface ItemEventListener extends EventListener {

  /** 
   * Return a string that describes the origin of this event listener.  This is used for debugging only.
   */
  public String getOrigin();
  
  /**
   * The field or default values have changed so they are now equal, or no
   * longer equal.
   * <p>
   * This method will only be called if there are no errors. If there are
   * errors, this method will be called when the error is cleared (reflecting
   * the equality of the field and default value).
   * 
   */
  public void valueEqualityChange(IItemModel model);

  /**
   * The field or default <b>source</b> values have changed so they are now equal, or no
   * longer equal.
   * <p>
   * This method <b>will</b> be called regardless of whether ther are errors or not. If there are
   * errors, equality will be determined by the source of the value, rather
   * than the value itself.
   * 
   */
  public void sourceEqualityChange(IItemModel model);

  /**
   * The field value has changed, either by user data entry or by the
   * application setting the field value.
   * <p>
   * This method will only be called if there are no errors. If there are
   * errors, this method will be called when the error is cleared.
   * 
   * @param model
   * @param value
   */
  public void valueChange(IItemModel model);

  /**
   * The control has left the error state--all previously noted errors have been
   * cleared.
   * <p>
   * This event is only fired when all previously noted errors have been
   * cleared. It a control is in the error state because of 2 noted errors, when
   * one is cleared this event does NOT fire. It would fire when the second of
   * the 2 noted errors has been cleared.
   * <p>
   * If the field, default or reference values have changed since the control
   * entered the error state, this event will be followed by the appropriate
   * equality shown or equality changed events.
   * 
   * @param ev
   */
  public void errorCleared(IItemModel model);

  /**
   * The control has gone into an error state. This is either because the user
   * entry is wrong, or some other validation method has noted an error on this
   * control.
   * <p>
   * This event is only fired when the control first goes into the error state.
   * Subsequent error notifications will not cause this event to fire again.
   * 
   */
  public void errorNoted(IItemModel model, UserEntryException ex);

  /**
   * An attempt was made to change the field value by the user, but no change
   * was made because the user entry was in error for some reason. This event
   * will be fired regardless of error status of the control.
   * 
   */
  public void sourceChange(IItemModel model);

  /**
   * The comparison basis of the object model has changed.
   */
  public void comparisonBasisChange (IItemModel model);
  
}
