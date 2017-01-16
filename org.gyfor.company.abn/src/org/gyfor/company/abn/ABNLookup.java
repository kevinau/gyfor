package org.gyfor.company.abn;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.gyfor.company.Company;
import org.gyfor.company.ICompanyLookup;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.xml.sax.SAXException;


@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ABNLookup implements ICompanyLookup {

  private static final Pattern abnDigits = Pattern.compile("[1-9]\\d \\d{3} \\d{3} \\d{3}");

  @Configurable(required = true)
  private String authenticationGUID;


  @Activate
  public void activate(ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
  }


  public boolean inApplicable(String companyNumber) {
    Matcher matcher = abnDigits.matcher(companyNumber);
    return matcher.matches();
  }


  @Override
  public Company getCompany(String companyNumber) throws IOException {
    ABNSearchResult result;
    try {
      result = searchByABN(authenticationGUID, companyNumber, false);

      if (result.isException()) {
        String ex = result.getExceptionDescription();
        if (ex.equals("No records found")) {
          return null;
        } else {
          throw new IOException(ex);
        }
      } else {
        return new Company(companyNumber, result.getOrganisationName());
      }
    } catch (URISyntaxException | SAXException | ParserConfigurationException | FactoryConfigurationError | SecurityException | IllegalArgumentException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  private static final String UTF_8 = StandardCharsets.UTF_8.name();


  public static void main(String[] args) {
    try {
      String guid = "6256afdb-ec0d-42cd-893e-eb96cc72fe80";
      String abn = "69 410 335 356";

      ABNSearchResult result = searchByABN(guid, abn, false);

      if (!result.isException()) {
        System.out
            .println("ABN search for ABN [" + abn + "] returned business name [" + result.getOrganisationName() + "]");
      } else {
        System.out
            .println("ABN search for ABN [" + abn + "] returned exception [" + result.getExceptionDescription() + "]");
      }
    } catch (Exception e) {
      System.err.println("Caught exception : " + e);
      e.printStackTrace(System.err);
    }
  }


  public static ABNSearchResult searchByABN(String guid, String abn, boolean includeHistorical)
      throws URISyntaxException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
    ABNSearchResult results = null;

    String params = "";
    params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
    params += "&searchString=" + URLEncoder.encode(abn, UTF_8);

    results = doRequest(guid, "ABRSearchByABN", params);

    return results;
  }


  private static ABNSearchResult doRequest(String guid, String service, String parameters)
      throws URISyntaxException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
    ABNSearchResult result = null;

    URL url = new URL("http://abr.business.gov.au/abrxmlsearch/ABRXMLSearch.asmx/" + service + "?authenticationGuid="
        + URLEncoder.encode(guid, UTF_8) + parameters);

    HttpURLConnection connection = (HttpURLConnection)url.openConnection();

    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "text/xml; charset-utf-8");
    connection.connect();

    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
      result = new ABNSearchResult(XMLUtils.DOMParseXML(connection.getInputStream()).getDocumentElement());
    }
    connection.disconnect();

    return result;
  }


  private static String encodeBooleanParam(boolean value) {
    if (value) {
      return "Y";
    } else {
      return "N";
    }
  }

}
