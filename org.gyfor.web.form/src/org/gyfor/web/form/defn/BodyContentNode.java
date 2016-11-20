package org.gyfor.web.form.defn;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;


public class BodyContentNode extends AbstractRenderableNode {

  private final Loader loader;
  private final MapExpression mapExpression;
  
  private PebbleTemplate compiledTemplate;


  public BodyContentNode(int lineNumber, Loader loader, MapExpression mapExpression) {
    super(lineNumber);
    this.loader = loader;
    this.mapExpression = mapExpression;
    // Lazily load template
  }


  private void loadTemplate () {
    // Initialize the template engine.
    Builder builder = new PebbleEngine.Builder();

    // Add bundle specific loader
    builder.loader(loader);

    // Add page extensions
    Extension pageExtension = new AbstractExtension() {
      @Override
      public List<TokenParser> getTokenParsers() {
        List<TokenParser> parsers = new ArrayList<>();
        parsers.add(new Entity2TokenParser());
        //parsers.add(new EndEntityTokenParser());
        return parsers;
      }
    };
    builder.extension(pageExtension);
    
    // Build the Pebble engine
    PebbleEngine engine = builder.build();

    // And compile the one and only template.  TODO Should there be a Pebble engine service
    // against which all templates can be compiled.
    try {
      compiledTemplate = engine.getTemplate("body");
    } catch (PebbleException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException, IOException {
    if (compiledTemplate == null) {
      // Lazily create template
      loadTemplate();
    }
    Map<String, Object> map = Collections.emptyMap();
    if (this.mapExpression != null) {
      map = (Map<String, Object>)this.mapExpression.evaluate(self, context);
    }
    compiledTemplate.evaluate(writer, map);
  }


  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
