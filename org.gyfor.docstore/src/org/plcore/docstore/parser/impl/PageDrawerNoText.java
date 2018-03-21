package org.plcore.docstore.parser.impl;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;


public class PageDrawerNoText extends PageDrawer {

  public PageDrawerNoText(PageDrawerParameters parameters) throws IOException {
    super(parameters);
  }


  @Override
  public void beginText() throws IOException {
    // Do nothing
  }


  @Override
  public void endText() throws IOException {
    // Do nothing
  }


  @Override
  protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode, Vector displacement) throws IOException {
    // Do nothing
  }

}
