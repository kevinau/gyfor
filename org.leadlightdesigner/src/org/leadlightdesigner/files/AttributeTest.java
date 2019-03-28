package org.leadlightdesigner.files;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.leadlightdesigner.AttributeHandler;
import org.leadlightdesigner.guide.GuideAttributes;

import georegression.struct.point.Point2D_F64;

public class AttributeTest {

  public static void main (String[] args) throws IOException {
    Path path = Paths.get("workspace/guides/topCentrePanel.png");
 
    GuideAttributes attribs = new GuideAttributes(Paths.get("../17 Burwood Ave.jpg"));
    
    AttributeHandler.saveAttributes(path, "design.guide", attribs);

    GuideAttributes guideAttributes = AttributeHandler.getAttributes(path, "design.guide", GuideAttributes.class);
    System.out.println(guideAttributes.getSource());
    System.out.println(guideAttributes.getTargetWidth());
    System.out.println(guideAttributes.getTargetHeight());
    System.out.println(guideAttributes.getCoords().length);
     
    Path path2 = Paths.get("workspace/guides/aaa.png");
    GuideAttributes guideAttributes2 = AttributeHandler.getAttributes(path2, "design.guide", GuideAttributes.class);
    System.out.println(guideAttributes2.getSource());
    System.out.println(guideAttributes2.getTargetWidth());
    System.out.println(guideAttributes2.getTargetHeight());
    System.out.println(guideAttributes2.getCoords().length);
    for (Point2D_F64 d : guideAttributes2.getCoords()) {
      System.out.println(d);
    }
     
  }
}
