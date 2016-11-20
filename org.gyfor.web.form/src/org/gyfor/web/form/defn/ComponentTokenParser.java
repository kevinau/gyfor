package org.gyfor.web.form.defn;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;


public class ComponentTokenParser extends AbstractTokenParser {

  private final String tagName;
  private final String templateName;


  public ComponentTokenParser(String tagName, String templateName) {
    this.tagName = tagName;
    this.templateName = templateName;
  }


  @Override
  public String getTag() {
    return tagName;
  }

  
  @Override
  public RenderableNode parse(Token token, Parser parser) throws ParserException {

    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the tag token (typically headcontent or bodycontent)
    stream.next();

    Token current = stream.current();
    MapExpression mapExpression = null;

    // We check if there is an optional 'with' parameter on the tag.
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

    stream.expect(Token.Type.EXECUTE_END);

    return new ComponentNode(lineNumber, templateName, mapExpression);
  }

}
