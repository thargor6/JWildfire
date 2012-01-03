/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.variation;

import javax.swing.JLabel;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;

public class PostBumpMapWFFunc extends VariationFunc {

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_SCALEZ = "scale_z";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_OFFSETZ = "offset_z";
  private static final String PARAM_RESETZ = "reset_z";
  private static final String PARAM_FILENAME = "filename";

  private static final String[] paramNames = { PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_RESETZ, PARAM_FILENAME };

  private double scaleX = 1.0;
  private double scaleY = 1.0;
  private double scaleZ = 0.2;
  private double offsetX = 0.0;
  private double offsetY = 0.0;
  private double offsetZ = 0.0;
  private double resetZ = 0.0;
  private String filename = "C:\\TMP\\wf\\male_body-1440x900.jpg";
  // derived params
  private SimpleImage bumpMap;
  private boolean clearZ;
  private double imgXScale, imgYScale;
  private double imgXOffset, imgYOffset;
  private int imgWidth, imgHeight;
  private Pixel toolPixel = new Pixel();

  static int cnt = 0;

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = pAffineTP.x;
    double y = pAffineTP.y;

    x = (x + imgXOffset + offsetX) * imgXScale * scaleX;
    y = (y + imgYOffset + offsetY) * imgYScale * scaleY;

    double dz = offsetZ;
    int ix = Tools.FTOI(x);
    int iy = Tools.FTOI(y);
    if (ix >= 0 && ix < imgWidth && iy >= 0 && iy < imgHeight) {
      toolPixel.setARGBValue(bumpMap.getARGBValue(ix, iy));
      double intensity = (0.299 * (double) toolPixel.r + 0.588 * (double) toolPixel.g + 0.113 * (double) toolPixel.b) / 255.0;
      dz += scaleZ * intensity;
    }
    if (clearZ) {
      pVarTP.z = dz;
    }
    else {
      pVarTP.z += dz;
    }

    if (cnt++ < 1000) {
      System.out.println("xy=" + pAffineTP.x + "/" + pAffineTP.y + "->" + x + "/" + y);
      System.out.println("  int xy=" + ix + "/" + ix + " " + dz);
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    // TODO
    //    return new Object[] { scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, resetZ, filename };
    return new Object[] { scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, resetZ, 0.0 };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scaleX = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaleY = pValue;
    else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
      scaleZ = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offsetX = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offsetY = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offsetZ = pValue;
    else if (PARAM_RESETZ.equalsIgnoreCase(pName))
      resetZ = pValue;
    else if (PARAM_FILENAME.equalsIgnoreCase(pName))
      ;
    // TODO
    //filename = "";
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "post_bumpmap_wf";
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm) {
    bumpMap = null;
    if (filename != null && filename.length() > 0) {
      try {
        bumpMap = new ImageReader(new JLabel()).loadImage(filename);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (filename == null) {
      bumpMap = new SimpleImage(320, 256);
    }
    clearZ = Tools.FTOI(resetZ) == 1;
    imgXOffset = 1.0;
    imgWidth = bumpMap.getImageWidth();
    imgHeight = bumpMap.getImageHeight();
    imgXScale = (double) imgWidth / 2.0;
    imgYOffset = 1.0;
    imgYScale = (double) imgHeight / 2.0;
  }
}
