package org.plcore.template;

import com.mitchellbosecke.pebble.tokenParser.TokenParser;

public interface ITemplateEngine {

  public void addTokenParser (TokenParser tokenParser);
  
  public ITemplate getTemplate(String templateName);

}