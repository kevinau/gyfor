package org.gyfor.dao.test;

import java.util.Timer;
import java.util.TimerTask;

import org.gyfor.dao.test.data.Party;
import org.gyfor.dao.test.data.SimpleEntity;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.dao.ConcurrentModificationException;
import org.plcore.dao.EntityData;
import org.plcore.dao.IDataAccessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class AddTest {

  private Logger logger = LoggerFactory.getLogger(AddTest.class);
  
  @Reference
  private IDataAccessObject dao;
  
  private void runTest() {
    Party party0 = new Party("Qantas", "Qantas Airways", "www.qantas.com");

    logger.info("About to add {}", party0);
    party0 = (Party)dao.add(party0);
    logger.info("Added: {}", party0);
    
    int id = party0.getId();
    logger.info("About to fetch record with id {}", id);
    Party party1 = (Party)dao.fetchById(Party.class, id);
    logger.info("Fetched: {}", party1);
    
    logger.info("About to change record with id {}", id);
    party1.setFormalName("Qantas Airways Australia");
    logger.info("New record {}", party1);
    try {
      party1 = (Party)dao.update(party1);
    } catch (ConcurrentModificationException ex) {
      logger.error("Record change failure", ex);
    }
    logger.info("Record changed: {}", party1);
    
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
