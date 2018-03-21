package org.plcore.docstore.parser.impl;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.plcore.docstore.segment.SegmentMatcherList;
import org.plcore.srcdoc.PartialSegment;
import org.plcore.srcdoc.SourceDocumentContents;


class PDFTextStripper3 extends PDFTextStripper {
  
  private SourceDocumentContents docContents;
  
  
  public PDFTextStripper3() throws IOException {
    docContents = new SourceDocumentContents();
  }

  
  @Override
  protected void writeCharacters (TextPosition tp) throws IOException {
    System.out.println("====> " + tp.getX() + " '" + tp.getUnicode() + "' " + tp.getWidth());
    super.writeCharacters(tp);
  }
  
  
  @Override
  protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
    // Starting position
    TextPosition posn0 = textPositions.get(0);
    float x0 = posn0.getX();
    float y0 = posn0.getY() - posn0.getHeight();
//    System.out.println(">>>> " + text + ":  " + posn0.getFont() + " " + posn0.getFontSize() + " " + posn0.getFontSizeInPt());

    // Ending position
    TextPosition posn1 = textPositions.get(textPositions.size() - 1);
    float x1 = posn1.getX() + posn1.getWidth();
    float y1 = posn1.getY();
    float height = Float.max(posn0.getHeight(), posn1.getHeight());
    float heightAdj = (float)(height * 0.3);
    
    PartialSegment partialSegment = new PartialSegment(getCurrentPageNo() - 1, x0, y0 - heightAdj, x1, y1 + heightAdj, text, posn0.getFontSize());
    for (TextPosition p : textPositions) {
      partialSegment.add(new PartialSegment.Nibble(p.getX() - x0, p.getWidth(), p.getUnicode()));
    }
    docContents.add(SegmentMatcherList.matchers, partialSegment);
    
    super.writeString(text, textPositions);
  }
  
  
  public int getWordCount () {
    return docContents.size();
  }
  
  
  public SourceDocumentContents getDocumentContents () {
    return docContents;
  }
  
}
