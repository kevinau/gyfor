package org.leadlightdesigner.design;

import java.util.concurrent.atomic.AtomicInteger;

import org.gyfor.util.EventListenerList;

public class SolderPoint {

  private static AtomicInteger idFactory = new AtomicInteger(1);

  private int id;

  private EventListenerList<MoveListener> moveListeners = new EventListenerList<>();
  
  
  public SolderPoint () {
    id = idFactory.incrementAndGet();
  }
  
  
  public int getId() {
    return id;
  }


  public DPoint getDPoint() {
    return new DPoint(0, 0);
  };


  public void addMoveListener (MoveListener x) {
    moveListeners.add(x);
  }
  
  
  public void removeMoveListener (MoveListener x) {
    moveListeners.remove(x);
  }
  
  
  protected void fireMoveListeners () {
    for (MoveListener x : moveListeners) {
      x.pointMoved();
    }
  }
  
  
  public String toString() {
    DPoint dp = getDPoint();
    return "(" + dp.getX() + "," + dp.getY() + ")";
  }
}
