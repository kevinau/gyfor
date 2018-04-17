package org.gyfor.dbloader.berkeley;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionValue;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.pennyledger.party.Party;
import org.plcore.dao.EntityDescription;
import org.plcore.dao.IDataAccessObject;

import au.com.bytecode.opencsv.CSVReader;

@Component (immediate = true, property={"dataAccessObject.target=(name=pennyledger.party)"})
public class DatabaseAccessObjectTest {

  //@Reference
//  private IDataEventRegistry dataEventRegistry;
  
  private IDataAccessObject<Party> dao;
  

//  @Reference
//  public void setDataEventRegistry (IDataEventRegistry dataEventRegistry) {
//    this.dataEventRegistry = dataEventRegistry;
//  }
//  
//  
//  public void unsetDataEventRegistry (IDataEventRegistry dataEventRegistry) {
//    this.dataEventRegistry = null;
//  }
  

  @SuppressWarnings("unchecked")
  @Reference(name="dao")
  public void setDao (IDataAccessObject<?> dao) {
    this.dao = (IDataAccessObject<Party>)dao;
  }
  
  
  public void unsetDao (IDataAccessObject<?> dao) {
    this.dao = null;
  }
  

  public class DataChangeEventHandler implements EventHandler {
    @Override
    public void handleEvent(Event event) {
      int id = (int)event.getProperty("id");
      String description = (String)event.getProperty("description");
      if (description != null) {
        System.err.println("Description changed: " + id + ": " + description);
      }
      EntityLife entityLife = (EntityLife)event.getProperty("entityLife");
      if (entityLife != null) {
        System.err.println("Entity life changed: " + id + ": " + entityLife);
      }
    }
  }

  
  @Activate
  public void activate (BundleContext bundleContext) {
//    dataEventRegistry.addDataChangeListener(dataChangeListener);
    String[] topics = new String[] {
        "org/gyfor/data/DataAccessObject/*"
    };

    Dictionary<String, Object> props = new Hashtable<>();
    props.put(EventConstants.EVENT_TOPIC, topics);
    bundleContext.registerService(EventHandler.class.getName(), new DataChangeEventHandler() , props);
    
    dao.removeAll();
    
    try {
      CSVReader reader = new CSVReader(new FileReader("C:/Users/Kevin/git/gyfor/org.gyfor.dbloader.berkeley/party.csv"));
      String[] fieldNames = reader.readNext(); 

      ArrayToObjectLoader<Party> objectLoader = new ArrayToObjectLoader<>((IEntityPlan<Party>)dao.getEntityPlan(), fieldNames);
          
      int lineNo = 1;
      String[] line = reader.readNext();
      while (line != null) {
        // line[] is an array of values from the CSV file
        System.out.println("Adding: " + line[0] + " " + line[1] + " " + line[2] + " ...");
        
        Party instance;
        try {
          instance = objectLoader.getValue(lineNo, line);
          System.out.println(instance);
          dao.add (instance);
        } catch (UserEntryException ex) {
          System.err.println(ex);
        }

        line = reader.readNext();
        lineNo++;
      }
      reader.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
      
    // Read the database: first in primary key order
    int qantasId = 0;
    System.out.println("Primary key order:");
    List<Party> results = dao.getAll();
    for (Party entity: results) {
      if (entity.getShortName().equals("QAN")) {
        qantasId = entity.getId();
      }
      System.out.println(entity);
    }
    
    System.out.println("Descriptions one-by-one");
    for (Party entity: results) {
      int id = entity.getId();
      String desc = dao.getDescriptionById(id);
      System.out.println(id + ": " + desc);
    }

    System.out.println("Fetch and sort all descriptions:");
    List<EntityDescription> results2 = dao.getDescriptionAll();
    Collections.sort(results2);
    for (EntityDescription idValue: results2) {
      System.out.println(idValue);
    }

    System.out.println("Fetch by id and update:");
    Party party4 = dao.getById(qantasId);
    Party party5 = dao.newInstance(party4);
    System.out.println(party5);
    party4.setFormalName(party5.getFormalName().toUpperCase());
    VersionValue v5 = dao.update(party4, party5);
    System.out.println(v5);
    System.out.println(party4);
    System.out.println(party5);

    System.out.println("Fetch by id and update 2:");
    Party party6 = dao.newInstance(party4);
    System.out.println(party6);
    party4.setWebPage(party6.getWebPage().toUpperCase());
    VersionValue v6 = dao.update(party4, party6);
    System.out.println(v6);
    System.out.println(party4);
    System.out.println(party6);

  }

  
  @Deactivate
  public void deactivate () {
    //dataEventRegistry.removeDataChangeListener(dataChangeListener);
  }

}
