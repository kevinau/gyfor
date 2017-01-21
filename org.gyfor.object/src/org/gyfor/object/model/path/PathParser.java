package org.gyfor.object.model.path;

import org.gyfor.object.model.path.ParseException;
import org.gyfor.object.model.path.parser.PlanPathParser;

public class PathParser {

  public static IPathExpression parse (String s) throws ParseException {
    IPathExpression expr;
    try {
      expr = new PlanPathParser().parse(s);
    } catch (org.gyfor.object.model.path.parser.ParseException ex) {
      throw new ParseException(ex);
    }
    
    return expr;
  }
  
}
