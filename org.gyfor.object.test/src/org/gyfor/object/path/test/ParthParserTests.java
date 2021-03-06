package org.gyfor.object.path.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.path.ParseException;
import org.plcore.userio.path.PathParser;

public class ParthParserTests {

  @Test
  public void simpleValidPath() {
    IPathExpression expr = PathParser.parse("inner/0/-1/field/*/**");
    Assert.assertNotNull(expr);
  }
  
  @Test(expected = ParseException.class)
  public void simpleInvalidPath() {
    PathParser.parse("////inner/field");
  }
  
  @Test(expected = ParseException.class)
  public void lexerInvalidPath() {
    PathParser.parse("@inner/field");
  }

  @Test
  public void arrayOfPaths() {
    String[] paths = {
        "inner",
        "inner/field",
    };
    IPathExpression[] expr = PathParser.parse(paths);
    Assert.assertNotNull(expr);
  }
  
  @Test
  public void listOfPaths() {
    String[] paths = {
        "inner",
        "inner/field",
    };
    List<String> list = Arrays.asList(paths);
    IPathExpression[] expr = PathParser.parse(list);
    Assert.assertNotNull(expr);
  }
  

  @Test
  public void dumpComplexPath() {
    // Used for coverage only
    IPathExpression expr = PathParser.parse("inner/0/-1/field/*/**");
    expr.dump();
  }
}
