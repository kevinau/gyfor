package org.plcore.osgi.eventlogger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;


@Component(property = EventConstants.EVENT_TOPIC + "=" + "org/plcore/*")
public class EventLogger implements EventHandler {
 
    @Override
    public void handleEvent(Event event) {
      System.out.println("Event seen:");
      for (String propName : event.getPropertyNames()) {
        Object propValue = event.getProperty(propName);
        System.out.println("  " + propName + ": " + propValue);
      }
      System.out.println("---");
    }
 
}