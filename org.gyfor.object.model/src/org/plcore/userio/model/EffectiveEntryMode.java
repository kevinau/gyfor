package org.plcore.userio.model;

import org.plcore.userio.EntryMode;


public enum EffectiveEntryMode {

  ENABLED,
  DISABLED,
  VIEW,
  HIDDEN;


  public static EffectiveEntryMode getEffective (EffectiveEntryMode parent, EntryMode entryMode) {
    switch (parent) {
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
  

  public static EffectiveEntryMode toEffective (EntryMode entryMode) {
    if (entryMode == EntryMode.VIEW) {
      return EffectiveEntryMode.VIEW;
    } else if (entryMode == EntryMode.HIDDEN) {
      return EffectiveEntryMode.HIDDEN;
    } else if (entryMode == EntryMode.DISABLED) {
      return EffectiveEntryMode.DISABLED;
    } else {
      return EffectiveEntryMode.ENABLED;
    }
  }

}
