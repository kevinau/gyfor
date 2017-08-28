package org.gyfor.dao.test;

import org.gyfor.dao.mapping.RowAdder;
import org.gyfor.dao.mapping.TableCreator;
import org.gyfor.dao.mapping.TableDropper;
import org.gyfor.dao.test.data.SimpleEntity;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IConnectionFactory;
import org.gyfor.sql.RowNotFoundException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate=true)
public class TableDropCreate {

  private final Logger logger = LoggerFactory.getLogger(TableDropCreate.class);
  
  @Reference
  private IConnectionFactory connFactory;


  @Activate
  public void activate() {
    String schema = "public";

    PlanFactory planFactory = new PlanFactory();
    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(SimpleEntity.class);

    logger.info("Starting test of: {}", entityPlan.getClassName());

    try (IConnection conn = connFactory.getIConnection()) {
      conn.setAutoCommit(false);
      TableDropper tableDropper = new TableDropper(conn);
      tableDropper.dropEntityTable(schema, entityPlan);
      conn.commit();
      logger.info("Dropped table: {}", entityPlan.getName());
    }

    try (IConnection conn = connFactory.getIConnection()) {
      conn.setAutoCommit(false);
      TableCreator tableCreator = new TableCreator(conn);
      tableCreator.createEntityTable(schema, entityPlan);
      conn.commit();
      logger.info("Created table: {}", entityPlan.getName());
    }

    SimpleEntity[] testData = new SimpleEntity[] { 
        new SimpleEntity("QAN", "Qantas Airways"),
        new SimpleEntity("CBA", "Commonwealth Bank of Australia"), 
        new SimpleEntity("TLS", "Telstra"), 
    };

    try (IConnection conn = connFactory.getIConnection()) {
      RowAdder rowAdder = new RowAdder(conn, schema, entityPlan);

      for (SimpleEntity entityValue : testData) {
        conn.setAutoCommit(false);
        rowAdder.addEntityRow(entityValue);
        conn.commit();
        logger.info("Entity row added to table: {}", entityValue);
      }
    }
    
    try (IConnection conn = connFactory.getIConnection()) {
      RowFetcher rowFetcher = new RowFetcher(conn, schema, entityPlan);

      conn.setAutoCommit(false);
      try {
        IEntityModel model = rowFetcher.fetchRowById("public", 2, entityPlan);
        logger.info("Fetched object with id: " + 2);
        Object value = model.getValue();
        logger.info("Fetched object has " + value.getClass());
        SimpleEntity value2 = (SimpleEntity)value;
        logger.info("Fetched object is: " + value2.toString());
        conn.commit();
      } catch (RowNotFoundException ex) {
        logger.info(ex.toString());
      }
    }

  }

}
