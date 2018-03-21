package org.plcore.osgi;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

public class EventHelper {

  public static void sendEvent (BundleContext bundleContext, String topic, Dictionary<String, ?> props) {
    @SuppressWarnings("unchecked")
    ServiceReference<EventAdmin> ref = (ServiceReference<EventAdmin>)bundleContext.getServiceReference(EventAdmin.class.getName());
    if (ref != null) {
      EventAdmin eventAdmin = bundleContext.getService(ref);
      Event reportGeneratedEvent = new Event(topic, props);
      eventAdmin.sendEvent(reportGeneratedEvent);
    }
  }
}
