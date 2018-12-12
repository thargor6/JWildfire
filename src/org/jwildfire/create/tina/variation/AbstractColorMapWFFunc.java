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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.GradientCreator;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import java.util.HashMap;
import java.util.Map;

import static org.jwildfire.base.mathlib.MathLib.*;

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
  private static final String PARAM_DC_COLOR = "dc_color";
  private static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  private static final String PARAM_IS_SEQUENCE = "is_sequence";
  private static final String PARAM_SEQUENCE_START = "sequence_start";
  private static final String PARAM_SEQUENCE_DIGITS = "sequence_digits";

  private static final String RESSOURCE_IMAGE_FILENAME = "image_filename";
  public static final String RESSOURCE_INLINED_IMAGE = "inlined_image";
  public static final String RESSOURCE_IMAGE_SRC = "image_src";
  public static final String RESSOURCE_IMAGE_DESC_SRC = "image_desc_src";

  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_TILEX, PARAM_TILEY, PARAM_RESETZ, PARAM_DC_COLOR, PARAM_BLEND_COLORMAP, PARAM_IS_SEQUENCE, PARAM_SEQUENCE_START, PARAM_SEQUENCE_DIGITS};
  private static final String[] ressourceNames = {RESSOURCE_IMAGE_FILENAME, RESSOURCE_INLINED_IMAGE, RESSOURCE_IMAGE_DESC_SRC, RESSOURCE_IMAGE_SRC};

  private double scaleX = 1.0;
  private double scaleY = 1.0;
  private double scaleZ = 0.0;
  private double offsetX = 0.0;
  private double offsetY = 0.0;
  private double offsetZ = 0.0;
  private int tileX = 1;
  private int tileY = 1;
  private int resetZ = 1;
  private int dc_color = 1;
  private int blend_colormap = 0;
  private String imageFilename = null;
  private byte[] inlinedImage = null;
  private String imageDescSrc = null;
  private String imageSrc = null;
  private int inlinedImageHash = 0;
  private int is_sequence = 0;
  private int sequence_start = 1;
  private int sequence_digits = 4;

  // derived params
  private int imgWidth, imgHeight;
  private Pixel toolPixel = new Pixel();
  private float[] rgbArray = new float[3];

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount, double pInputX, double pInputY) {
    double x = (pInputX - (offsetX + 0.5) + 1.0) / scaleX * (double) (imgWidth - 2);
    double y = (pInputY - (offsetY + 0.5) + 1.0) / scaleY * (double) (imgHeight - 2);
    int ix, iy;
    if (blend_colormap > 0) {
      ix = (int) MathLib.trunc(x);
      iy = (int) MathLib.trunc(y);
    } else {
      ix = Tools.FTOI(x);
      iy = Tools.FTOI(y);
    }
    if (this.tileX == 1) {
      if (ix < 0) {
        int nx = ix / imgWidth - 1;
        ix -= nx * imgWidth;
      } else if (ix >= imgWidth) {
        int nx = ix / imgWidth;
        ix -= nx * imgWidth;
      }
    }
    if (this.tileY == 1) {
      if (iy < 0) {
        int ny = iy / imgHeight - 1;
        iy -= ny * imgHeight;
      } else if (iy >= imgHeight) {
        int ny = iy / imgHeight;
        iy -= ny * imgHeight;
      }
    }

    double r, g, b;
    if (ix >= 0 && ix < imgWidth && iy >= 0 && iy < imgHeight) {
      if (colorMap instanceof SimpleImage) {
        if (blend_colormap > 0) {
          double iufrac = MathLib.frac(x);
          double ivfrac = MathLib.frac(y);
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix, iy));
          int lur = toolPixel.r;
          int lug = toolPixel.g;
          int lub = toolPixel.b;
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix + 1, iy));
          int rur = toolPixel.r;
          int rug = toolPixel.g;
          int rub = toolPixel.b;
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix, iy + 1));
          int lbr = toolPixel.r;
          int lbg = toolPixel.g;
          int lbb = toolPixel.b;
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix + 1, iy + 1));
          int rbr = toolPixel.r;
          int rbg = toolPixel.g;
          int rbb = toolPixel.b;
          r = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
          g = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
          b = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
        } else {
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix, iy));
          r = toolPixel.r;
          g = toolPixel.g;
          b = toolPixel.b;
        }
        if (dc_color > 0) {
          pVarTP.rgbColor = true;
          pVarTP.redColor = r;
          pVarTP.greenColor = g;
          pVarTP.blueColor = b;
        }
      } else {
        if (blend_colormap > 0) {
          double iufrac = MathLib.frac(x);
          double ivfrac = MathLib.frac(y);

          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
          double lur = rgbArray[0] * 255;
          double lug = rgbArray[1] * 255;
          double lub = rgbArray[2] * 255;
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix + 1, iy);
          double rur = rgbArray[0] * 255;
          double rug = rgbArray[1] * 255;
          double rub = rgbArray[2] * 255;
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy + 1);
          double lbr = rgbArray[0] * 255;
          double lbg = rgbArray[1] * 255;
          double lbb = rgbArray[2] * 255;
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix + 1, iy + 1);
          double rbr = rgbArray[0] * 255;
          double rbg = rgbArray[1] * 255;
          double rbb = rgbArray[2] * 255;
          r = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
          g = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
          b = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
        } else {
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
          r = rgbArray[0] * 255;
          g = rgbArray[1] * 255;
          b = rgbArray[2] * 255;
        }
        if (dc_color > 0) {
          pVarTP.rgbColor = true;
          pVarTP.redColor = r;
          pVarTP.greenColor = g;
          pVarTP.blueColor = b;
        }
      }
    } else {
      r = g = b = 0.0;
      if (dc_color > 0) {
        pVarTP.rgbColor = true;
        pVarTP.redColor = r;
        pVarTP.greenColor = g;
        pVarTP.blueColor = b;
      }
    }
    double dz = this.offsetZ;
    if (fabs(scaleZ) > EPSILON) {
      double intensity = (0.299 * r + 0.588 * g + 0.113 * b) / 255.0;
      dz += scaleZ * intensity;
    }
    if (resetZ != 0) {
      pVarTP.z = dz;
    } else {
      pVarTP.z += dz;
    }
    if (dc_color > 0) {
      pVarTP.color = getColorIdx(Tools.FTOI(r), Tools.FTOI(g), Tools.FTOI(b));
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, tileX, tileY, resetZ, dc_color, blend_colormap, is_sequence, sequence_start, sequence_digits};
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
    else if (PARAM_DC_COLOR.equalsIgnoreCase(pName))
      dc_color = Tools.FTOI(pValue);
    else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName))
      blend_colormap = Tools.FTOI(pValue);
    else if (PARAM_IS_SEQUENCE.equalsIgnoreCase(pName)) {
      is_sequence = Tools.FTOI(pValue);
      clearCurrColorMap();
    } else if (PARAM_SEQUENCE_START.equalsIgnoreCase(pName)) {
      sequence_start = Tools.FTOI(pValue);
      clearCurrColorMap();
    } else if (PARAM_SEQUENCE_DIGITS.equalsIgnoreCase(pName)) {
      sequence_digits = Tools.FTOI(pValue);
      clearCurrColorMap();
    } else
      throw new IllegalArgumentException(pName);
  }

  private WFImage colorMap;
  private RenderColor[] renderColors;
  private Map<RenderColor, Double> colorIdxMap = new HashMap<RenderColor, Double>();

  private double getColorIdx(int pR, int pG, int pB) {
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

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMap = null;
    renderColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    if (inlinedImage != null) {
      try {
        colorMap = RessourceManager.getImage(inlinedImageHash, inlinedImage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (imageFilename != null && imageFilename.length() > 0) {
      try {
        colorMap = RessourceManager.getImage(getCurrImageFilename(pContext));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (colorMap == null) {
      colorMap = getDfltImage();
    }
    imgWidth = colorMap.getImageWidth();
    imgHeight = colorMap.getImageHeight();
  }

  private String getCurrImageFilename(FlameTransformationContext pContext) {
    if (is_sequence > 0) {
      int frame = pContext.getFrame() - 1 + sequence_start;
      String baseFilename;
      String fileExt;
      int p = imageFilename.lastIndexOf(".");
      if (p < 0 || p <= sequence_digits || p == imageFilename.length() - 1)
        return imageFilename;
      baseFilename = imageFilename.substring(0, p - sequence_digits);
      fileExt = imageFilename.substring(p, imageFilename.length());

      String number = String.valueOf(frame);
      while (number.length() < sequence_digits) {
        number = "0" + number;
      }
      return baseFilename + number + fileExt;

    } else {
      return imageFilename;
    }
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
    return new byte[][]{(imageFilename != null ? imageFilename.getBytes() : null), inlinedImage, (imageDescSrc != null ? imageDescSrc.getBytes() : null), (imageSrc != null ? imageSrc.getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
      imageFilename = pValue != null ? new String(pValue) : "";
      if (imageFilename != null) {
        inlinedImage = null;
        inlinedImageHash = 0;
      }
      clearCurrColorMap();
    } else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
      inlinedImage = pValue;
      inlinedImageHash = RessourceManager.calcHashCode(inlinedImage);
      if (inlinedImage != null) imageFilename = null;
      clearCurrColorMap();
    } else if (RESSOURCE_IMAGE_DESC_SRC.equalsIgnoreCase(pName)) {
      imageDescSrc = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_IMAGE_SRC.equalsIgnoreCase(pName)) {
      imageSrc = pValue != null ? new String(pValue) : "";
    } else
      throw new IllegalArgumentException(pName);
  }

  private void clearCurrColorMap() {
    colorMap = null;
    colorIdxMap.clear();
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILE;
    } else if (RESSOURCE_IMAGE_DESC_SRC.equalsIgnoreCase(pName)) {
      return RessourceType.HREF;
    } else if (RESSOURCE_IMAGE_SRC.equalsIgnoreCase(pName)) {
      return RessourceType.HREF;
    } else
      throw new IllegalArgumentException(pName);
  }

}
