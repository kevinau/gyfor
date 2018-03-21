package org.pennyledger.party;

import org.osgi.service.component.annotations.Component;
import org.plcore.userio.IEntityFactory;


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
