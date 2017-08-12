package org.gyfor.dao.mapping;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IConnectionFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
public class RowAddFetchTest {

  private final Logger logger = LoggerFactory.getLogger(RowAddFetchTest.class);
  
  @Reference
  private IConnectionFactory connFactory;
  
  
  @Activate
  public void activate () {
    String schema = "public";
    
    PlanFactory planFactory = new PlanFactory();
    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(SimpleEntity.class);

    logger.info("Starting test of: {}", entityPlan.getClassName());
    
    try (IConnection conn = connFactory.getIConnection())
    {
      conn.setAutoCommit(false);
      TableDropper tableDropper = new TableDropper(conn);
      tableDropper.dropEntityTable(schema, entityPlan);
      conn.commit();
      logger.info("Dropped table: {}", entityPlan.getName());
    }
    
    try (IConnection conn = connFactory.getIConnection()) 
    {
      conn.setAutoCommit(false);
      TableCreator tableCreator = new TableCreator(conn);
      tableCreator.createEntityTable(schema, entityPlan);
      conn.commit();
      logger.info("Created table: {}", entityPlan.getName());
    }
    
    logger.info("Model created and value set");
    
    SimpleEntity[] testData = new SimpleEntity[] {
        new SimpleEntity("QAN", "Qantas Airways"),
        new SimpleEntity("CBA", "Commonwealth Bank of Australia"),
        new SimpleEntity("TLS", "Telstra"),
    };
    
    try (IConnection conn = connFactory.getIConnection()) 
    {
      RowAdder rowAdder = new RowAdder(conn, schema, entityPlan);
    
      for (SimpleEntity entityValue : testData) {
        conn.setAutoCommit(false);
        rowAdder.addEntityRow(entityValue);
        conn.commit();
        logger.info("Entity row added to table: {}", entityValue);
      }
    }
  }

}
