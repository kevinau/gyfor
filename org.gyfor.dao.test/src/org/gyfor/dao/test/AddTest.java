package org.gyfor.dao.test;

import java.util.Timer;
import java.util.TimerTask;

import org.gyfor.dao.ConcurrentModificationException;
import org.gyfor.dao.EntityData;
import org.gyfor.dao.IDataAccessObject;
import org.gyfor.dao.test.data.SimpleEntity;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class AddTest {

  private Logger logger = LoggerFactory.getLogger(AddTest.class);
  
  @Reference
  private IDataAccessObject dao;
  
  private void runTest() {
    SimpleEntity instance = new SimpleEntity("QAN", "Qantas Airways");

    logger.info("About to add {}", instance);
    EntityData entityData = dao.add(instance);
    logger.info("Added: {}", entityData);
    
    int id = entityData.getId();
    logger.info("About to fetch record with id {}", id);
    entityData = dao.fetchById(SimpleEntity.class, id);
    logger.info("Fetched: {}", entityData);
    
    logger.info("About to change record with id {}", id);
    SimpleEntity simpleEntity2 = new SimpleEntity("QAN", "Qantas Airways Australia");
    logger.info("New record {}", simpleEntity2);
    try {
      entityData = dao.change(entityData, simpleEntity2);
    } catch (ConcurrentModificationException ex) {
      logger.error("Record change failure", ex);
    }
    logger.info("Record changed");
    
//    logger.info("About to remove record with id {} and versin {}", id, entityData.getVersionTime());
//    try {
//      dao.remove(entityData);
//    } catch (ConcurrentModificationException ex) {
//      logger.error("Record removal failure", ex);
//    }
//    logger.info("Record removed");
  }

  
  @Activate
  protected void activate() {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        runTest();
        timer.cancel();
      }
    }, 10 * 1000);
  }

}
