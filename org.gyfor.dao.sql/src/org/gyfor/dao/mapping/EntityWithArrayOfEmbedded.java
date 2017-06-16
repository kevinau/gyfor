package org.gyfor.dao.mapping;

import org.gyfor.object.value.VersionTime;

@SuppressWarnings("unused")
public class EntityWithArrayOfEmbedded {

  private int id;
  
  private VersionTime version;
  
  private String code;
  
  private EmbeddedClass[] embedded;
  
}
