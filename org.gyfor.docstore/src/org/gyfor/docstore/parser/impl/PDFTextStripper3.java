package org.gyfor.docstore.parser.impl;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.gyfor.docstore.DocumentContents;
import org.gyfor.docstore.PartialSegment;


class PDFTextStripper3 extends PDFTextStripper {
  
  private DocumentContents docContents;
  
  
  public PDFTextStripper3() throws IOException {
    docContents = new DocumentContents();
  }

  
  @Override
  protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
    // Starting position
    TextPosition posn0 = textPositions.get(0);
    float x0 = posn0.getX();
    float y0 = posn0.getY() - posn0.getHeight();

    // Ending position
    TextPosition posn1 = textPositions.get(textPositions.size() - 1);
    float x1 = posn1.getX() + posn1.getWidth();
    float y1 = posn1.getY();
    float height = Float.max(posn0.getHeight(), posn1.getHeight());
    float heightAdj = (float)(height * 0.3);
    
    PartialSegment partialSegment = new PartialSegment(x0, y0 - heightAdj, x1, y1 + heightAdj, text);
    for (TextPosition p : textPositions) {
      partialSegment.add(new PartialSegment.Nibble(p.getX() - x0, p.getWidth(), p.getUnicode()));
    }
    docContents.add(partialSegment);
    
    super.writeString(text, textPositions);
  }
  
  
  public int getWordCount () {
    return docContents.size();
  }
  
  
  public DocumentContents getDocumentContents () {
    return docContents;
  }
  
}
