package org.plcore.docstore.parser.impl;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;


public class PDFRendererNoText extends PDFRenderer {

  public PDFRendererNoText(PDDocument document) {
    super(document);
  }


  /**
   * Returns a new PageDrawer instance, using the given parameters. May be
   * overridden.
   */
  @Override
  protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
    return new PageDrawerNoText(parameters);
  }

}
