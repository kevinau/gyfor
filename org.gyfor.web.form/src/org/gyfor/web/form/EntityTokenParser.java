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


/**
 * 'entity' token parser for the Pebble template engine.
 * <p>
 * The <code>entity</code> tag allows you to insert an entity form into the current template.
 * <code>{% entity "fully-qualified-class-name" %}
 * HTML content, including a selection of fields
 * {% endentity %}
 * </code>
 * The HTML content can be empty, in which case all fields of the class are included.  For example:
 * <p><code>{% entity "org.pennyledger.party.Party" %}{% endentity %}</code><p>
 * will include all fields of the Party class.
 * <p>
 * If the HTML content is not empty, only named fields' are included.  For example:
 * <p><code>{% entity "org.pennyledger.party.Party" %}
 * {% field "name" %}
 * {% field "telephone" %}
 * {% endentity %}</code><p>
 * will include only the name and telephone fields of the Party class.  Additional HTML and Pebble tags 
 * can surround the fields.
 * <p>
 * You can add additional variables to the context of the included template by passing a map after
 * the with keyword. The included template will have access to the same variables that the current 
 * template does plus the additional ones defined in the map passed after the <code>with</code> keyword:
 * <p><code>{% entity "org.pennyledger.party.Party" with {"country":"au"} %}
 * {% endentity %}
 * </code>
 * @author Kevin
 *
 */
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
