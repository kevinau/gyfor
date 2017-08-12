package org.gyfor.dao.test;

import org.gyfor.dao.test.data.GLTransaction;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.sql.IConnectionFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate=true)
public class GLTransactionTest {

  private final Logger logger = LoggerFactory.getLogger(GLTransactionTest.class);
  
  @Reference
  private IConnectionFactory connFactory;


  @Activate
  public void activate() {
    PlanFactory planFactory = new PlanFactory();
    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(GLTransaction.class);

    logger.info("Starting test of: {}", entityPlan.getClassName());

//    try (IConnection conn = connFactory.getIConnection()) {
//      conn.setAutoCommit(false);
//      TableDropper tableDropper = new TableDropper(conn);
//      tableDropper.dropEntityTable(schema, entityPlan);
//      conn.commit();
//      logger.info("Dropped table: {}", entityPlan.getName());
//    }
//
//    try (IConnection conn = connFactory.getIConnection()) {
//      conn.setAutoCommit(false);
//      TableCreator tableCreator = new TableCreator(conn);
//      tableCreator.createEntityTable(schema, entityPlan);
//      conn.commit();
//      logger.info("Created table: {}", entityPlan.getName());
//    }

//    GLTransaction[] testData = new GLTransaction[] { 
//        new GLTransaction("QAN", "Qantas Airways"),
//        new GLTransaction("CBA", "Commonwealth Bank of Australia"), 
//        new GLTransaction("TLS", "Telstra"), 
//    };
//
//    try (IConnection conn = connFactory.getIConnection()) {
//      RowAdder rowAdder = new RowAdder(conn);
//
//      for (GLTransaction entityValue : testData) {
//        conn.setAutoCommit(false);
//        rowAdder.addEntityRow(schema, entityPlan, entityValue);
//        conn.commit();
//        logger.info("Entity row added to table: {}", entityValue);
//      }
//    }
    
//    try (IConnection conn = connFactory.getIConnection()) {
//      RowFetcher rowFetcher = new RowFetcher(conn, modelFactory);
//
//      conn.setAutoCommit(false);
//      try {
//        IEntityModel model = rowFetcher.fetchRowById("public", 2, entityPlan);
//        logger.info("Fetched object with id: " + 2);
//        Object value = model.getValue();
//        logger.info("Fetched object has " + value.getClass());
//        SimpleEntity value2 = (SimpleEntity)value;
//        logger.info("Fetched object is: " + value2.toString());
//        conn.commit();
//      } catch (RowNotFoundException ex) {
//        logger.info(ex.toString());
//      }
//    }

  }

}
