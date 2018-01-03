package org.gyfor.web.form.defn;

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.LiteralStringExpression;
import com.mitchellbosecke.pebble.parser.Parser;


public class ParameterValues {

  private static int findParamIndex(String[] paramNames, String name) {
    int i = 0;
    while (i < paramNames.length) {
      if (paramNames[i].equals(name)) {
        return i;
      }
      i++;
    }
    return -1;
  }


  static List<Expression<?>> parse(TokenStream stream, Parser parser, String[] paramNames, boolean varArgs) throws ParserException {
    List<Expression<?>> values = new ArrayList<>();
    int index = 0;

    // Is there a parenthesis indicating the start of a parameter list?
    if (stream.current().test(Token.Type.PUNCTUATION, "(")) {
      // Consume the '('
      stream.next();

      while (!stream.current().test(Token.Type.PUNCTUATION, ")")) {
        if (stream.current().test(Token.Type.NAME)) {
          int lineNumber = stream.current().getLineNumber();
          // Get the name
          String name = parser.getExpressionParser().parseNewVariableName();
          if (stream.current().test(Token.Type.PUNCTUATION, "=")) {
            // It is name=value pair
            int i = findParamIndex(paramNames, name);
            if (i == -1) {
              throw new RuntimeException("'" + name + "' is not an allowed parameter name");
            }
            
            // Consume the '='
            stream.next();
            // get the value
            while (i > values.size()) {
              values.add(null);
            }
            values.add(parser.getExpressionParser().parseExpression());
          } else {
            if (index > paramNames.length && !varArgs) {
              throw new RuntimeException("More than " + paramNames.length + " parameters");
            }
            // It is a positional value only, where the value is name like
            values.add(new LiteralStringExpression(name, lineNumber));
          }
        } else {
          // It is a positional, where the value is an expression
          if (index > paramNames.length  && !varArgs) {
            throw new RuntimeException("More than " + paramNames.length + " parameters");
          }
          values.add(parser.getExpressionParser().parseExpression());
        }
        if (stream.current().test(Token.Type.PUNCTUATION, ",")) {
          // Step over the ","
          stream.next();
        }
      }
      // Consume the ')'
      stream.next();
    }
    
    // Pad out the values list to its minimum size
    while (values.size() < paramNames.length) {
      values.add(null);
    }
    return values;
  }

}
