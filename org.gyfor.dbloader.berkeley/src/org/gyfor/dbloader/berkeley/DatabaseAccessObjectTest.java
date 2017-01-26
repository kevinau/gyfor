package org.gyfor.dbloader.berkeley;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.gyfor.dao.IDataAccessObject;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.plan.IEntityPlan;
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
    System.out.println("activate CVS database loader.................." + dao);
    
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
        } catch (UserEntryException ex) {
          System.err.println(ex);
        }
//         int id = Integer.parseInt(line[0]);
//         data.setValue(line);
//         das.addOrUpdate(data);
             
        line = reader.readNext();
        lineNo++;
      }
      reader.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
      
    // Read the database: first in primary key order
    System.out.println("Primary key order:");
    List<?> results = dao.getAll();
    for (Object entity: results) {
      System.out.println(entity);
    }
  }

}
