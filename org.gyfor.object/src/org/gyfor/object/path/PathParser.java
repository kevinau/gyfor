package org.gyfor.object.path;

import org.gyfor.object.path.ParseException;
import org.gyfor.object.path.parser.ObjectPathParser;

public class PathParser {

  public static IPathExpression parse (String s) throws ParseException {
    IPathExpression expr;
    try {
      expr = new ObjectPathParser().parse(s);
    } catch (org.gyfor.object.path.parser.ParseException ex) {
      throw new ParseException(ex);
    }
    return expr;
  }
  
}
