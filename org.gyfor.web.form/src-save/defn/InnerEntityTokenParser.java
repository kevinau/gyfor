package org.gyfor.web.form.defn;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;


public class InnerEntityTokenParser extends AbstractTokenParser {

  @Override
  public String getTag() {
    return "endentity";
  }


  @Override
  public RenderableNode parse(Token token, Parser parser) throws ParserException {
    TokenStream stream = parser.getStream();
    int lineNumber = token.getLineNumber();

    // skip the "endentity" token
    stream.next();

    // expect to see "%}"
    stream.expect(Token.Type.EXECUTE_END);

    return new InnerEntityNode(lineNumber);
  }

}
