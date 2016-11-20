package org.gyfor.web.form;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;


public class FieldTokenParser extends AbstractTokenParser {

  @Override
  public String getTag() {
    return "field";
  }


  @Override
  public RenderableNode parse(Token token, Parser parser) throws ParserException {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the 'field' token
    stream.next();

    Expression<?> fieldNameExpression = parser.getExpressionParser().parseExpression();
    MapExpression mapExpression = null;
    
    // We check if there is an optional 'with' parameter on the entity tag.
    Token current = stream.current();
    if (current.getType().equals(Token.Type.NAME) && current.getValue().equals("with")) {
      // Skip over 'with'
      stream.next();

      Expression<?> parsedExpression = parser.getExpressionParser().parseExpression();

      if (parsedExpression instanceof MapExpression) {
        mapExpression = (MapExpression)parsedExpression;
      } else {
        throw new ParserException(null,
            String.format("Unexpected expression '%1s'.", parsedExpression.getClass().getCanonicalName()),
            token.getLineNumber(), stream.getFilename());
      }
    }
    
    // Skip over the %} of the field tag
    stream.expect(Token.Type.EXECUTE_END);
    
    return new FieldNode(lineNumber, fieldNameExpression, mapExpression);
  }

}
