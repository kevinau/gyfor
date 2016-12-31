package org.gyfor.company.abn;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gyfor.company.Company;
import org.gyfor.company.ICompanyLookup;
import org.osgi.service.component.annotations.Component;

@Component
public class ABNLookup implements ICompanyLookup {

  private static final Pattern abnDigits = Pattern.compile("[1-9]\\d \\d{3} \\d{3} \\d{3}");
  
  
  public boolean inApplicable(String companyNumber) {
    Matcher matcher = abnDigits.matcher(companyNumber);
    return matcher.matches();
  }

  
  @Override
  public Company getCompany(String companyNumber) throws IOException {
    switch (companyNumber) {
    case "16 009 661 901" :
      return new Company(companyNumber, "Qantas Airways");
    default :
      return null;
    }
  }

}
