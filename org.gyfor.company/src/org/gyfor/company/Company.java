package org.gyfor.company;


public class Company {

  private final String companyNumber;
  
  private final String formalName;
  
  public Company (String companyNumber, String formalName) {
    this.companyNumber = companyNumber;
    this.formalName = formalName;
  }
  
  public String getCompanyNumber () {
    return companyNumber;
  }
  
  public String getFormalName () {
    return formalName;
  }
  
}
