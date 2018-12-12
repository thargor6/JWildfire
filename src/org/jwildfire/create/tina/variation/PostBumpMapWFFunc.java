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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class PostBumpMapWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_SCALEZ = "scale_z";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_OFFSETZ = "offset_z";
  private static final String PARAM_RESETZ = "reset_z";

  private static final String RESSOURCE_IMAGE_FILENAME = "image_filename";
  private static final String RESSOURCE_INLINED_IMAGE = "inlined_image";

  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_RESETZ};
  private static final String[] ressourceNames = {RESSOURCE_IMAGE_FILENAME, RESSOURCE_INLINED_IMAGE};

  private double scaleX = 1.0;
  private double scaleY = 1.0;
  private double scaleZ = 0.2;
  private double offsetX = 0.0;
  private double offsetY = 0.0;
  private double offsetZ = 0.0;
  private int resetZ = 0;

  private String imageFilename = null;
  private byte[] inlinedImage = null;
  private int inlinedImageHash = 0;

  // derived params
  private int imgWidth, imgHeight;
  private Pixel toolPixel = new Pixel();
  private float[] rgbArray = new float[3];

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (pAffineTP.x - offsetX + 1.0) / scaleX * 0.5 * (double) (imgWidth - 1);
    double y = (pAffineTP.y - offsetY + 1.0) / scaleY * 0.5 * (double) (imgHeight - 1);
    double dz = offsetZ;
    int ix = Tools.FTOI(x);
    int iy = Tools.FTOI(y);
    if (ix >= 0 && ix < imgWidth && iy >= 0 && iy < imgHeight) {
      double intensity;
      if (bumpMap instanceof SimpleImage) {
        toolPixel.setARGBValue(((SimpleImage) bumpMap).getARGBValue(ix, iy));
        double r = toolPixel.r;
        double g = toolPixel.g;
        double b = toolPixel.b;
        intensity = (0.299 * r + 0.588 * g + 0.113 * b) / 255.0;
      } else {
        ((SimpleHDRImage) bumpMap).getRGBValues(rgbArray, ix, iy);
        double r = rgbArray[0];
        double g = rgbArray[1];
        double b = rgbArray[2];
        intensity = (0.299 * r + 0.588 * g + 0.113 * b);
      }
      dz += scaleZ * intensity;
      if (resetZ != 0) {
        pVarTP.z = dz;
      } else {
        pVarTP.z += dz;
      }
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, resetZ};
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
      resetZ = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public String getName() {
    return "post_bumpmap_wf";
  }

  private WFImage bumpMap;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    bumpMap = null;
    if (inlinedImage != null) {
      try {
        bumpMap = RessourceManager.getImage(inlinedImageHash, inlinedImage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (imageFilename != null && imageFilename.length() > 0) {
      try {
        bumpMap = RessourceManager.getImage(imageFilename);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (bumpMap == null) {
      bumpMap = new SimpleImage(320, 256);
    }
    imgWidth = bumpMap.getImageWidth();
    imgHeight = bumpMap.getImageHeight();
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(imageFilename != null ? imageFilename.getBytes() : null), inlinedImage};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
      imageFilename = pValue != null ? new String(pValue) : "";
      if (imageFilename != null) {
        inlinedImage = null;
        inlinedImageHash = 0;
      }
      bumpMap = null;
    } else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
      inlinedImage = pValue;
      inlinedImageHash = RessourceManager.calcHashCode(inlinedImage);
      if (inlinedImage != null) imageFilename = null;
      bumpMap = null;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILE;
    } else
      throw new IllegalArgumentException(pName);
  }

}
