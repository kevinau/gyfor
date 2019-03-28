package org.leadlightdesigner.guide;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.leadlightdesigner.GuidePoint;

/*
 * Copyright (c) 2011-2016, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import boofcv.alg.distort.RemovePerspectiveDistortion;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import georegression.struct.point.Point2D_F64;

/**
 * Certain image processing techniques, such as Optical Character Recognition (OCR), can be performed better if
 * perspective distortion is remove from an image.  In this example a homography is computed from the four corners
 * of a bulletin board and the image is projected into a square image without perspective distortion.  The
 * {@link RemovePerspectiveDistortion} class is used to perform the distortion.  The class is easy to understand
 * if you know what a homography is, you should look at it!
 *
 * @author Peter Abeles
 */
public class ImageDeskewer {
  
  public static BufferedImage deskew (Path sourcePath, int targetWidth, int targetHeight, List<GuidePoint> points) throws IOException {
    // Specify the corners in the input image of the region.
    // Order matters! top-left, top-right, bottom-right, bottom-left
    GuidePoint[] sortedPoints = GuidePoints.sortByAngle(points);
    
    Point2D_F64[] point2ds = new Point2D_F64[4];
    int i = 0;
    for (GuidePoint p : sortedPoints) {
      point2ds[i] = new Point2D_F64(p.getX(), p.getY());
      i++;
    };
    
    // load a color image
    System.out.println("=============== " + sourcePath.toAbsolutePath());
    
    BufferedImage buffered = ImageIO.read(sourcePath.toFile());
    
    Planar<GrayF32> input = ConvertBufferedImage.convertFromPlanar(buffered, null, true, GrayF32.class);

    RemovePerspectiveDistortion<Planar<GrayF32>> removePerspective =
        new RemovePerspectiveDistortion<>(targetWidth, targetHeight, ImageType.pl(3, GrayF32.class));

    if (!removePerspective.apply(input, point2ds[0], point2ds[1], point2ds[2], point2ds[3])) {
      throw new RuntimeException("Failed!?!?");
    }

    Planar<GrayF32> output = removePerspective.getOutput();

    BufferedImage flat = ConvertBufferedImage.convertTo_F32(output,null,true);
    return flat;
  }
  
  
  public static void main(String[] args) throws IOException {
    // load a color image
    //BufferedImage buffered = UtilImageIO.loadImage(UtilIO.pathExample("goals_and_stuff.jpg"));
    List<GuidePoint> points = new ArrayList<>();
    points.add(new GuidePoint(612, 783, 1.0)); 
    points.add(new GuidePoint(2214, 516, 1.0));
    points.add(new GuidePoint(2244, 1467, 1.0)); 
    points.add(new GuidePoint(524, 1597, 1.0));

    File sourceFile = new File("workspace/17 Burwood Ave.jpg");

    BufferedImage buffered = ImageIO.read(sourceFile);
    BufferedImage flat = ImageDeskewer.deskew(sourceFile.toPath(), 841, 465, points);
    
    ShowImages.showWindow(buffered,"Original Image",true);
    ShowImages.showWindow(flat,"Without Perspective Distortion",true);
    
    File guideFile = new File("workspace/guides/topCentrePanel.png");
    ImageIO.write(flat, "png", guideFile);
    
//    // Add file attributes
//    GuideAttributes guideAttributes = new GuideAttributes(sourceFile, targetWidth, targetHeight, coords);
//    AttributeHandler.saveAttributes(guideFile, "design.guide", guideAttributes);
  }
}
