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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.TextTransformer;
import org.jwildfire.transform.TextTransformer.FontStyle;
import org.jwildfire.transform.TextTransformer.HAlignment;
import org.jwildfire.transform.TextTransformer.Mode;
import org.jwildfire.transform.TextTransformer.VAlignment;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.jwildfire.base.Tools.FTOI;
import static org.jwildfire.base.mathlib.MathLib.*;

public class TextWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_FONT_SIZE = "font_size";
  private static final String PARAM_ANTIALIAS = "antialias";
  private static final String PARAM_BASELINE = "baseline";

  private static final String RESSOURCE_TEXT = "text";
  private static final String RESSOURCE_FONT_NAME = "font_name";

  private static final String[] paramNames = {PARAM_FONT_SIZE, PARAM_ANTIALIAS, PARAM_SCALEX, PARAM_SCALEY, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_BASELINE};
  private static final String[] ressourceNames = {RESSOURCE_TEXT, RESSOURCE_FONT_NAME};

  private int font_size = 320;
  private double antialias = 0.5;
  private double scale_x = 1.0;
  private double scale_y = 1.0;
  private double offset_x = 0.0;
  private double offset_y = 0.0;
  private int baseline = 0;
  private String text = "JWildfire";
  private String font_name = "Arial";

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{font_size, antialias, scale_x, scale_y, offset_x, offset_y, baseline};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FONT_SIZE.equalsIgnoreCase(pName))
      font_size = FTOI(pValue);
    else if (PARAM_ANTIALIAS.equalsIgnoreCase(pName))
      antialias = pValue;
    else if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scale_x = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scale_y = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offset_x = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offset_y = pValue;
    else if (PARAM_BASELINE.equalsIgnoreCase(pName))
      baseline = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  private static class Point {
    private final double x, y;

    public Point(double pX, double pY) {
      x = pX;
      y = pY;
    }

    public double getX() {
      return x;
    }

    public double getY() {
      return y;
    }

  }

  private List<Point> _points;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
  }

  private String makeRessourceKey() {
    return getName() + "#" + text + "#" + "#" + font_name + "#" + font_size + "#" + baseline;
  }

  @SuppressWarnings("unchecked")
  private List<Point> getPoints() {
    if (_points == null) {
      String key = makeRessourceKey();
      _points = (List<Point>) RessourceManager.getRessource(key);
      if (_points == null) {
        TextTransformer txt = new TextTransformer();
        txt.setText1(text);
        txt.setAntialiasing(false);
        txt.setColor(Color.WHITE);
        txt.setMode(Mode.NORMAL);
        txt.setFontStyle(FontStyle.PLAIN);
        txt.setFontName(font_name);
        txt.setFontSize(font_size);
        txt.setHAlign(HAlignment.CENTRE);
        txt.setVAlign(VAlignment.CENTRE);
        txt.setBaseLineOffset(baseline);

        Dimension dim = txt.calculateTextSize();
        int imgWidth = (int) (dim.getWidth() + 2 * font_size);
        int imgHeight = (int) (dim.getHeight() + 2 * font_size);
        SimpleImage imgMap = new SimpleImage(imgWidth, imgHeight);

        txt.transformImage(imgMap);
        _points = new ArrayList<Point>();
        double w2 = (double) imgMap.getImageWidth() / 2.0;
        double h2 = (double) imgMap.getImageHeight() / 2.0;
        for (int i = 0; i < imgMap.getImageHeight(); i++) {
          for (int j = 0; j < imgMap.getImageWidth(); j++) {
            int argb = imgMap.getARGBValue(j, i);
            if (argb != 0) {
              double x = ((double) j - w2) / (double) imgMap.getImageWidth();
              double y = ((double) i - h2) / (double) imgMap.getImageHeight();
              _points.add(new Point(x, y));
            }
          }
        }
        //        System.out.println("IMG: " + imgWidth + "x" + imgHeight + ", POINTS: " + _points.size());
        RessourceManager.putRessource(key, _points);
      }
    }
    return _points;
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(text != null ? text.getBytes() : null), (font_name != null ? font_name.getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_TEXT.equalsIgnoreCase(pName)) {
      text = pValue != null ? new String(pValue) : "";
      _points = null;
    } else if (RESSOURCE_FONT_NAME.equalsIgnoreCase(pName)) {
      font_name = pValue != null ? new String(pValue) : "";
      _points = null;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_TEXT.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else if (RESSOURCE_FONT_NAME.equalsIgnoreCase(pName)) {
      return RessourceType.FONT_NAME;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    List<Point> points = getPoints();
    final double DFLT_SCALE = 3.0;
    if (points.size() > 1) {
      Point point = points.get(pContext.random(points.size()));
      double rawX = point.getX();
      double rawY = point.getY();
      if (antialias > 0.01) {
        double dr = (exp(antialias * sqrt(-log(pContext.random()))) - 1.0) * 0.001;
        double da = pContext.random() * 2.0 * M_PI;
        rawX += dr * cos(da);
        rawY += dr * sin(da);
      }
      pVarTP.x += (rawX * scale_x + offset_x) * DFLT_SCALE;
      pVarTP.y += (rawY * scale_y + offset_y) * DFLT_SCALE;
    } else {
      pVarTP.x += pContext.random();
      pVarTP.y += pContext.random();
      pVarTP.rgbColor = true;
      pVarTP.redColor = 0;
      pVarTP.greenColor = 0;
      pVarTP.blueColor = 0;
    }
  }

  @Override
  public String getName() {
    return "text_wf";
  }

}
