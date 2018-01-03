package org.gyfor.web.form.defn;

import java.util.List;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.StoppingCondition;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;


public class Entity2TokenParser extends AbstractTokenParser {

  private final Loader<?> loader;


  public Entity2TokenParser(Loader<?> loader) {
    this.loader = loader;
  }


  @Override
  public String getTag() {
    return "entity";
  }


  @Override
  public RenderableNode parse(Token token, Parser parser) throws ParserException {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // Skip the "entity" token
    stream.next();

    // Parse the fully qualified class name.
    String className = parser.getExpressionParser().parseNewVariableName();
    while (stream.current().test(Token.Type.PUNCTUATION, ".")) {
      // Skip the "." token
      stream.next();

      // Get the next name part
      String part = parser.getExpressionParser().parseNewVariableName();
      className += '.' + part;
    }

    // We check if there is an optional 'with' parameter on the include tag.
    Token current = stream.current();
    MapExpression withValues = null;

    if (current.getType().equals(Token.Type.NAME) && current.getValue().equals("with")) {
      // Skip over 'with'
      stream.next();

      Expression<?> parsedExpression = parser.getExpressionParser().parseExpression();

      if (parsedExpression instanceof MapExpression) {
        withValues = (MapExpression)parsedExpression;
      } else {
        throw new ParserException(null,
            String.format("Unexpected expression '%1s'.", parsedExpression.getClass().getCanonicalName()), 
            token.getLineNumber(), stream.getFilename());
      }
    }

    // expect to see "%}"
    stream.expect(Token.Type.EXECUTE_END);

    BodyNode body = parser.subparse(testEntityEnd);

    // skip the 'entitEnd' token
    stream.next();

    // expect to see "%}"
    stream.expect(Token.Type.EXECUTE_END);

    return new Entity2Node(lineNumber, className, withValues, body, loader);
  }

  
  private StoppingCondition testEntityEnd = new StoppingCondition() {
    @Override
    public boolean evaluate(Token token) {
      return token.test(Token.Type.NAME, "endentity");
    }
  };

}
