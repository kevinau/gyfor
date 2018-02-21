package org.pennyledger.party;

import org.gyfor.object.IEntityFactory;
import org.osgi.service.component.annotations.Component;


@Component
public class PartyFactory implements IEntityFactory<Party> {

  @Override
  public Party newEntityInstance() {
    return new Party();
  }

  @Override
  public Class<Party> getEntityClass() {
    return Party.class;
  }

}
