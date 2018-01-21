package org.pennyledger.party;

import java.sql.Timestamp;

import org.gyfor.doc.Document;
import org.gyfor.doc.IDocumentContents;
import org.gyfor.object.ManyToOne;


public class PartyDocument extends Document {

  public PartyDocument(String hashCode, Timestamp originTime, String originName, String originExtension,
      Timestamp importTime, IDocumentContents contents) {
    super(hashCode, originTime, originName, originExtension, importTime, contents);
  }

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
