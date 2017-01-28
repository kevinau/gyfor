package org.gyfor.dbloader.berkeley;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.gyfor.dao.IDataAccessObject;
import org.gyfor.dao.IdValuePair;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.value.VersionValue;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.party.Party;

import au.com.bytecode.opencsv.CSVReader;

@Component (immediate = true, property={"dataAccessObject.target=(name=pennyledger.party)"})
public class DatabaseAccessObjectTest {

  private IDataAccessObject<Party> dao;
  
  
  @SuppressWarnings("unchecked")
  @Reference(name="dao")
  public void setDao (IDataAccessObject<?> dao) {
    this.dao = (IDataAccessObject<Party>)dao;
  }
  
  
  public void unsetDao (IDataAccessObject<?> dao) {
    this.dao = null;
  }
  
    
  @Activate
  public void activate () {
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
    List<IdValuePair<String>> results2 = dao.getDescriptionAll();
    Collections.sort(results2);
    for (IdValuePair<String> idValue: results2) {
      System.out.println(idValue);
    }

    System.out.println("Fetch by id and update:");
    Party party4 = dao.getById(qantasId);
    System.out.println(party4);;
    party4.setFormalName(party4.getFormalName().toUpperCase());
    VersionValue v4 = dao.update(party4);
    System.out.println(v4);;
    System.out.println(party4);;

  }

}
