package org.gyfor.doc;

import org.osgi.service.component.annotations.Component;


@Component(immediate = true, property = "documentTypeId=nothing")
public class NothingDocumentType implements IDocumentType {

  public static class Unclassified {
  }

  @Override
  public Class<?> getDataClass() {
    return Unclassified.class;
  }


  @Override
  public String getTypeName() {
    return "Nothing";
  }

}
