package org.gyfor.web.form;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.StoppingCondition;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;


public class EntityTokenParser extends AbstractTokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) throws ParserException {

    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the 'entity' token
    stream.next();

    Expression<?> entityNameExpression;
    Token current = stream.current();
    if (current.getType().equals(Token.Type.OPERATOR) && current.getValue().equals("*")) {
      entityNameExpression = null;
      // Skip over '*'
      stream.next();
    } else {
      entityNameExpression = parser.getExpressionParser().parseExpression();
    }
    
    BodyNode body = null;
    MapExpression mapExpression = null;
    
    current = stream.current();
    // We check if there is an optional 'with' parameter on the entity tag.
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
    // Skip over the %} of the entity tag
    stream.expect(Token.Type.EXECUTE_END);

    body = parser.subparse(testEntityEnd);

    // Skip over the endentity token
    stream.next();

    // Skip over the %} of the endentity token
    stream.expect(Token.Type.EXECUTE_END);
    
    return new EntityNode(lineNumber, entityNameExpression, body, mapExpression);
  }


  @Override
  public String getTag() {
    return "entity";
  }
  
  
  private StoppingCondition testEntityEnd = new StoppingCondition() {
    @Override
    public boolean evaluate(Token token) {
      return token.test(Token.Type.NAME, "endentity");
    }
  };

}
