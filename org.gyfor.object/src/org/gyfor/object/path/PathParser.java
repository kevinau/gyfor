package org.gyfor.object.path;

import org.gyfor.object.INode;
import org.gyfor.object.path.parser.ObjectPathParser;

public class PathParser {

  @SuppressWarnings("unchecked")
  public static <X extends INode> IPathExpression<X> parse (String s) throws ParseException {
    IPathExpression<X> expr;
    try {
      expr = new ObjectPathParser().parse(s);
    } catch (org.gyfor.object.path.parser.ParseException ex) {
      throw new ParseException(ex);
    }
    return expr;
  }
  
}
