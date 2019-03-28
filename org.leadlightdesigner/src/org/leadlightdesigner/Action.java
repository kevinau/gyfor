package org.leadlightdesigner; 
 
/** 
 * Represents an action to be performed, with no arguments and no return value. 
 * 
 * <p>This is a <a href="package-summary.html">functional interface</a> 
 * whose functional method is {@link #doit()}. 
 *  * 
 * @since 1.8 
 */ 
@FunctionalInterface 
public interface Action { 
 
    /** 
     * Perform the action.
     */ 
    void doit(); 
} 