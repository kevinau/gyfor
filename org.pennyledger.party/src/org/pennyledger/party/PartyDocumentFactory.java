package org.pennyledger.party;

import org.gyfor.object.IEntityFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;


@Component(configurationPolicy=ConfigurationPolicy.REQUIRE)
public class PartyDocumentFactory implements IEntityFactory {

  @SuppressWarnings("unchecked")
  @Override
  public PartyDocument newEntityInstance() {
    return new PartyDocument();
  }

  @Override
  public Class<?> getEntityClass() {
    return PartyDocument.class;
  }

}
