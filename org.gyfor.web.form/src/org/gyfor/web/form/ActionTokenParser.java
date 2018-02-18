package org.gyfor.web.form;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;


/**
 * 'option' token parser for the Pebble template engine.
 * <p>
 * The <code>option</code> tag allows you to insert an option (a.k.a. button or link) into the current template.
 * <code>{% option <option-name> %}
 * </code>
 * The option tag can take an optional parameter, 'showalways' or 'showavailable'.  
 * <code>{% option <option-name> show-always %}
 * </code>
 * or <code>{% option <option-name> show-available %}
 * </code>
 * <p>
 * You can add additional variables to the context of the included template by passing a map after
 * the 'with' keyword. The included template will have access to the same variables that the current 
 * template does plus the additional ones defined in the map passed after the <code>with</code> keyword:
 * <p><code>{% option ADD with {"label":"Add"} %}
 * {% endoption %}
 * </code>
 * Typically this is used to specifiy option labels.
 * @author Kevin
 *
 */
public class ActionTokenParser extends AbstractTokenParser {

  @Override
  public RenderableNode parse(Token token, Parser parser) throws ParserException {

    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip over the 'option' token
    stream.next();

    Expression<?> actionNameExpression = parser.getExpressionParser().parseExpression();

    // We check if there is an optional 'showalways' or 'showavailable'
    Token current = stream.current();
    boolean showAlways = true;
    if (current.getType().equals(Token.Type.NAME)) {
      if (current.getValue().equals("showalways")) {
        showAlways = true;
        // Skip over keyword
        stream.next();
      } else if (current.getValue().equals("showavailable")) {
        showAlways = false;
        // Skip over keyword
        stream.next();
      }
    }

    // We check if there is an optional 'with' parameter on the entity tag.
    MapExpression mapExpression = null;
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

    return new ActionNode(lineNumber, actionNameExpression, showAlways, mapExpression);
  }


  @Override
  public String getTag() {
    return "option";
  }
  
}
