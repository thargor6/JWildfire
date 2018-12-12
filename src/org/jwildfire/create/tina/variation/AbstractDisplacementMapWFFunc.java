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

public abstract class AbstractDisplacementMapWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_MODE = "mode";
  private static final String PARAM_COLOR_MODE = "color_mode";
  private static final String PARAM_BIAS = "bias";
  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_TILEX = "tile_x";
  private static final String PARAM_TILEY = "tile_y";

  private static final String RESSOURCE_INLINED_IMAGE = "inlined_image";
  private static final String RESSOURCE_IMAGE_FILENAME = "image_filename";

  private static final String[] paramNames = {PARAM_MODE, PARAM_COLOR_MODE, PARAM_BIAS, PARAM_SCALEX, PARAM_SCALEY, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_TILEX, PARAM_TILEY};
  private static final String[] ressourceNames = {RESSOURCE_IMAGE_FILENAME, RESSOURCE_INLINED_IMAGE};

  private static final int MODE_ROTATE = 0;
  private static final int MODE_TRANSLATE = 1;
  private static final int MODE_SCALE = 2;
  private static final int MODE_SCISSOR = 3;

  private static final int COLOR_MODE_IGNORE = 0;
  private static final int COLOR_MODE_INHERIT = 1;

  private int mode = MODE_ROTATE;
  private int colorMode = COLOR_MODE_IGNORE;
  private double bias = 0.0;
  private double scaleX = 1.0;
  private double scaleY = 1.0;
  private double offsetX = 0.0;
  private double offsetY = 0.0;
  private int tileX = 1;
  private int tileY = 1;
  private String imageFilename = null;
  private byte[] inlinedImage = null;
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
        toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                ix, iy));
        r = (double) toolPixel.r / 255.0;
        g = (double) toolPixel.g / 255.0;
        b = (double) toolPixel.b / 255.0;
      } else {
        ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
        r = rgbArray[0];
        g = rgbArray[1];
        b = rgbArray[2];
      }
    } else {
      return;
    }

    switch (mode) {
      case MODE_TRANSLATE: {
        double amountX = (r - 0.5) * pAmount;
        double amountY = (g - 0.5) * pAmount;
        pVarTP.x += amountX;
        pVarTP.y += amountY;
      }
      break;
      case MODE_SCALE: {
        double intensity = calcIntensity(r, g, b) - bias;
        if (intensity > 0.0) {
          double scl = 1.0 + (intensity - 0.5) * pAmount;
          pVarTP.x *= scl;
          pVarTP.y *= scl;
        }
      }
      break;
      case MODE_SCISSOR: {
        double amountX = (r - 0.5) * pAmount;
        double amountY = (g - 0.5) * pAmount;
        double newx = pVarTP.x * amountX * amountY + pVarTP.y * amountY;
        double newy = pVarTP.x * amountY - pVarTP.y * amountX * amountY;
        pVarTP.x = newx;
        pVarTP.y = newy;
      }
      break;
      case MODE_ROTATE:
      default: {
        double intensity = calcIntensity(r, g, b) - bias;
        if (intensity > 0.0) {
          double angle = intensity * M_2PI * pAmount;
          double sina = sin(angle);
          double cosa = cos(angle);
          double xnew = pVarTP.x * cosa - pVarTP.y * sina;
          double ynew = pVarTP.x * sina + pVarTP.y * cosa;
          pVarTP.x = xnew;
          pVarTP.y = ynew;
        }
      }
    }

    switch (colorMode) {
      case COLOR_MODE_INHERIT: {
        pVarTP.rgbColor = true;
        pVarTP.redColor = r * 255;
        pVarTP.greenColor = g * 255;
        pVarTP.blueColor = b * 255;
        pVarTP.color = getColorIdx(r, g, b);
      }
      break;
      default: // nothing to do
        break;
    }

  }

  private double calcIntensity(double red, double green, double blue) {
    return (0.299 * red + 0.588 * green + 0.113 * blue);
  }

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
    //    double nearestDist = sqrt(dr * dr + dg * dg + db * db);
    double nearestDist = fabs(dr) + fabs(dg) + fabs(db);
    for (int i = 1; i < renderColors.length; i++) {
      color = renderColors[i];
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      //      double dist = sqrt(dr * dr + dg * dg + db * db);
      double dist = fabs(dr) + fabs(dg) + fabs(db);
      if (dist < nearestDist) {
        nearestDist = dist;
        nearestIdx = i;
      }
    }
    return (double) nearestIdx / (double) (renderColors.length - 1);

  }
  */

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{mode, colorMode, bias, scaleX, scaleY, offsetX, offsetY, tileX, tileY};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MODE.equalsIgnoreCase(pName))
      mode = Tools.FTOI(pValue);
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName))
      colorMode = Tools.FTOI(pValue);
    else if (PARAM_BIAS.equalsIgnoreCase(pName))
      bias = pValue;
    else if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scaleX = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaleY = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offsetX = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offsetY = pValue;
    else if (PARAM_TILEX.equalsIgnoreCase(pName))
      tileX = Tools.FTOI(pValue);
    else if (PARAM_TILEY.equalsIgnoreCase(pName))
      tileY = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  private WFImage colorMap;
  private RenderColor[] renderColors;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMap = null;
    //    renderColors = pContext.getFlameRenderer().getColorMap();
    // TODO optimize
    renderColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    if (inlinedImage != null) {
      try {
        colorMap = RessourceManager.getImage(inlinedImageHash, inlinedImage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (imageFilename != null && imageFilename.length() > 0) {
      try {
        colorMap = RessourceManager.getImage(imageFilename);
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
      colorMap = null;
      colorIdxMap.clear();
    } else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
      inlinedImage = pValue;
      inlinedImageHash = RessourceManager.calcHashCode(inlinedImage);
      if (inlinedImage != null) imageFilename = null;
      colorMap = null;
      colorIdxMap.clear();
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
