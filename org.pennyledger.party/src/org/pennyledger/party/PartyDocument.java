package org.pennyledger.party;

import java.io.Serializable;

import org.plcore.userio.ManyToOne;


public class PartyDocument implements Serializable {

  private static final long serialVersionUID = 1L;

  private Party party;

  public Party getParty() {
    return party;
  }

  @ManyToOne(optional=false)
  public void setParty(Party party) {
    this.party = party;
  }

}
