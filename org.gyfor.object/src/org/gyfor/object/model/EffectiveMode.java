package org.gyfor.object.model;

import org.gyfor.object.EntryMode;


public enum EffectiveMode {

  ENABLED (true),
  DISABLED (true),
  VIEW (true),
  HIDDEN (false);

  private final boolean allowEntryEvents;
  
  private EffectiveMode (boolean allowEntryEvents) {
    this.allowEntryEvents = allowEntryEvents;
  }
  
  
  public boolean allowEntryEvents () {
    return allowEntryEvents;
  }
  
  
  public EffectiveMode getEffective (EntryMode entryMode) {
    EffectiveMode parentMode = this;
    switch (parentMode) {
    case ENABLED :
      if (entryMode == EntryMode.HIDDEN) {
        return HIDDEN;
      } else if (entryMode == EntryMode.VIEW) {
        return VIEW;
      } else if (entryMode == EntryMode.DISABLED) {
        return DISABLED;
      } else {
        return ENABLED;
      }
    case DISABLED :
      if (entryMode == EntryMode.HIDDEN) {
        return HIDDEN;
      } else if (entryMode == EntryMode.VIEW) {
        return VIEW;
      } else {
        return DISABLED;
      }
    case VIEW :
      if (entryMode == EntryMode.HIDDEN) {
        return HIDDEN;
      } else {
        return VIEW;
      }
    default :
      return HIDDEN;
    }
  }
  

  public static EffectiveMode toEffective (EntryMode entryMode) {
    if (entryMode == EntryMode.VIEW) {
      return EffectiveMode.VIEW;
    } else if (entryMode == EntryMode.HIDDEN) {
      return EffectiveMode.HIDDEN;
    } else if (entryMode == EntryMode.DISABLED) {
      return EffectiveMode.DISABLED;
    } else {
      return EffectiveMode.ENABLED;
    }
  }

}
