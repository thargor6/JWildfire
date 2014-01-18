/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.create.GradientCreator;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public abstract class AbstractColorMapWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_SCALEX = "scale_x";
  public static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_SCALEZ = "scale_z";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_OFFSETZ = "offset_z";
  private static final String PARAM_TILEX = "tile_x";
  private static final String PARAM_TILEY = "tile_y";
  private static final String PARAM_RESETZ = "reset_z";

  private static final String RESSOURCE_IMAGE_FILENAME = "image_filename";
  public static final String RESSOURCE_INLINED_IMAGE = "inlined_image";
  public static final String RESSOURCE_IMAGE_SRC = "image_src";
  public static final String RESSOURCE_IMAGE_DESC_SRC = "image_desc_src";

  private static final String[] paramNames = { PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_TILEX, PARAM_TILEY, PARAM_RESETZ };
  private static final String[] ressourceNames = { RESSOURCE_IMAGE_FILENAME, RESSOURCE_INLINED_IMAGE, RESSOURCE_IMAGE_DESC_SRC, RESSOURCE_IMAGE_SRC };

  private double scaleX = 1.0;
  private double scaleY = 1.0;
  private double scaleZ = 0.0;
  private double offsetX = 0.0;
  private double offsetY = 0.0;
  private double offsetZ = 0.0;
  private int tileX = 1;
  private int tileY = 1;
  private int resetZ = 1;
  private String imageFilename = null;
  private byte[] inlinedImage = null;
  private String imageDescSrc = null;
  private String imageSrc = null;
  private int inlinedImageHash = 0;

  // derived params
  private int imgWidth, imgHeight;
  private Pixel toolPixel = new Pixel();
  private float[] rgbArray = new float[3];

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount, double pInputX, double pInputY) {
    double x = (pInputX - (offsetX + 0.5) + 1.0) / scaleX * (double) (imgWidth - 1);
    double y = (pInputY - (offsetY + 0.5) + 1.0) / scaleY * (double) (imgHeight - 1);
    int ix = Tools.FTOI(x);
    int iy = Tools.FTOI(y);
    if (this.tileX == 1) {
      if (ix < 0) {
        int nx = ix / imgWidth - 1;
        ix -= nx * imgWidth;
      }
      else if (ix >= imgWidth) {
        int nx = ix / imgWidth;
        ix -= nx * imgWidth;
      }
    }
    if (this.tileY == 1) {
      if (iy < 0) {
        int ny = iy / imgHeight - 1;
        iy -= ny * imgHeight;
      }
      else if (iy >= imgHeight) {
        int ny = iy / imgHeight;
        iy -= ny * imgHeight;
      }
    }

    if (ix >= 0 && ix < imgWidth && iy >= 0 && iy < imgHeight) {
      if (colorMap instanceof SimpleImage) {
        toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
            ix, iy));
        pVarTP.rgbColor = true;
        pVarTP.redColor = toolPixel.r;
        pVarTP.greenColor = toolPixel.g;
        pVarTP.blueColor = toolPixel.b;
      }
      else {
        ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
        pVarTP.rgbColor = true;
        pVarTP.redColor = rgbArray[0];
        pVarTP.greenColor = rgbArray[0];
        pVarTP.blueColor = rgbArray[0];
      }
    }
    else {
      pVarTP.rgbColor = true;
      pVarTP.redColor = 0;
      pVarTP.greenColor = 0;
      pVarTP.blueColor = 0;
    }
    double dz = this.offsetZ;
    if (fabs(scaleZ) > EPSILON) {
      double intensity = (0.299 * pVarTP.redColor + 0.588 * pVarTP.greenColor + 0.113 * pVarTP.blueColor) / 255.0;
      dz += scaleZ * intensity;
    }
    if (resetZ != 0) {
      pVarTP.z = dz;
    }
    else {
      pVarTP.z += dz;
    }
    pVarTP.color = getColorIdx(pVarTP.redColor, pVarTP.greenColor, pVarTP.blueColor);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, tileX, tileY, resetZ };
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
    else if (PARAM_TILEX.equalsIgnoreCase(pName))
      tileX = Tools.FTOI(pValue);
    else if (PARAM_TILEY.equalsIgnoreCase(pName))
      tileY = Tools.FTOI(pValue);
    else if (PARAM_RESETZ.equalsIgnoreCase(pName))
      resetZ = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  private WFImage colorMap;
  private RenderColor[] renderColors;
  private Map<RenderColor, Double> colorIdxMap = new HashMap<RenderColor, Double>();

  private double getColorIdx(double pR, double pG, double pB) {
    RenderColor pColor = new RenderColor(pR, pG, pB);
    Double res = colorIdxMap.get(pColor);
    if (res == null) {

      int nearestIdx = 0;
      RenderColor color = renderColors[0];
      double dr, dg, db;
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      double nearestDist = sqrt(dr * dr + dg * dg + db * db);
      for (int i = 1; i < renderColors.length; i++) {
        color = renderColors[i];
        dr = (color.red - pR);
        dg = (color.green - pG);
        db = (color.blue - pB);
        double dist = sqrt(dr * dr + dg * dg + db * db);
        if (dist < nearestDist) {
          nearestDist = dist;
          nearestIdx = i;
        }
      }
      res = (double) nearestIdx / (double) (renderColors.length - 1);
      colorIdxMap.put(pColor, res);
    }
    return res;
  }

  /*
    private double getColorIdx(double pR, double pG, double pB) {
      int nearestIdx = 0;
      RenderColor color = renderColors[0];
      double dr, dg, db;
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      double nearestDist = sqrt(dr * dr + dg * dg + db * db);
      for (int i = 1; i < renderColors.length; i++) {
        color = renderColors[i];
        dr = (color.red - pR);
        dg = (color.green - pG);
        db = (color.blue - pB);
        double dist = sqrt(dr * dr + dg * dg + db * db);
        if (dist < nearestDist) {
          nearestDist = dist;
          nearestIdx = i;
        }
      }
      return (double) nearestIdx / (double) (renderColors.length - 1);
    }
  */
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMap = null;
    renderColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    if (inlinedImage != null) {
      try {
        colorMap = RessourceManager.getImage(inlinedImageHash, inlinedImage);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    else if (imageFilename != null && imageFilename.length() > 0) {
      try {
        colorMap = RessourceManager.getImage(imageFilename);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (colorMap == null) {
      colorMap = getDfltImage();
    }
    imgWidth = colorMap.getImageWidth();
    imgHeight = colorMap.getImageHeight();
  }

  private static SimpleImage dfltImage = null;

  private synchronized SimpleImage getDfltImage() {
    if (dfltImage == null) {
      GradientCreator creator = new GradientCreator();
      dfltImage = creator.createImage(256, 256);
    }
    return dfltImage;
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] { (imageFilename != null ? imageFilename.getBytes() : null), inlinedImage, (imageDescSrc != null ? imageDescSrc.getBytes() : null), (imageSrc != null ? imageSrc.getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
      imageFilename = pValue != null ? new String(pValue) : "";
      colorMap = null;
      colorIdxMap.clear();
    }
    else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
      inlinedImage = pValue;
      inlinedImageHash = RessourceManager.calcHashCode(inlinedImage);
      colorMap = null;
      colorIdxMap.clear();
    }
    else if (RESSOURCE_IMAGE_DESC_SRC.equalsIgnoreCase(pName)) {
      imageDescSrc = pValue != null ? new String(pValue) : "";
    }
    else if (RESSOURCE_IMAGE_SRC.equalsIgnoreCase(pName)) {
      imageSrc = pValue != null ? new String(pValue) : "";
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    }
    else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILE;
    }
    else if (RESSOURCE_IMAGE_DESC_SRC.equalsIgnoreCase(pName)) {
      return RessourceType.HREF;
    }
    else if (RESSOURCE_IMAGE_SRC.equalsIgnoreCase(pName)) {
      return RessourceType.HREF;
    }
    else
      throw new IllegalArgumentException(pName);
  }

}
