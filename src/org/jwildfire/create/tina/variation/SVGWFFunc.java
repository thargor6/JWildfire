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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_JWILDFIRE;

import java.awt.Graphics2D;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

public class SVGWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_ANTIALIAS = "antialias";
  private static final String PARAM_DIRECT_COLOR = "direct_color";

  private static final String RESSOURCE_SVG = "svg";

  private static final String[] paramNames = { PARAM_ANTIALIAS, PARAM_SCALEX, PARAM_SCALEY, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_DIRECT_COLOR };
  private static final String[] ressourceNames = { RESSOURCE_SVG };

  private double antialias = 0.5;
  private double scale_x = 1.0;
  private double scale_y = 1.0;
  private double offset_x = 0.0;
  private double offset_y = 0.0;
  private int direct_color = 1;
  private String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
      " <!DOCTYPE svg>\r\n" +
      " <svg xmlns=\"http://www.w3.org/2000/svg\"\r\n" +
      "      width=\"304\" height=\"290\">\r\n" +
      "   <path d=\"M2,111 h300 l-242.7,176.3 92.7,-285.3 92.7,285.3 z\" \r\n" +
      "      style=\"fill:#FB2;stroke:#BBB;stroke-width:15;stroke-linejoin:round\"/>\r\n" +
      "</svg>";

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { antialias, scale_x, scale_y, offset_x, offset_y, direct_color };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANTIALIAS.equalsIgnoreCase(pName))
      antialias = pValue;
    else if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scale_x = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scale_y = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offset_x = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offset_y = pValue;
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName))
      direct_color = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  private class Point {
    private final double x, y;
    private final int r, g, b;

    public Point(double pX, double pY, int pR, int pG, int pB) {
      x = pX;
      y = pY;
      r = pR;
      g = pG;
      b = pB;
    }

    public double getX() {
      return x;
    }

    public double getY() {
      return y;
    }

    public int getR() {
      return r;
    }

    public int getG() {
      return g;
    }

    public int getB() {
      return b;
    }

  }

  private List<Point> _points;

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
  }

  private String makeRessourceKey() {
    return getName() + "#" + svg;
  }

  @SuppressWarnings("unchecked")
  private List<Point> getPoints() {
    if (_points == null) {
      String key = makeRessourceKey();
      _points = (List<Point>) RessourceManager.getRessource(key);
      if (_points == null) {
        try {
          int imgWidth = 400;
          int imgHeight = 400;
          SimpleImage imgMap = new SimpleImage(imgWidth, imgHeight);

          StringReader reader = new StringReader(svg);

          SVGUniverse svgUniverse = new SVGUniverse();
          SVGDiagram diagram = svgUniverse.getDiagram(svgUniverse.loadSVG(reader, "svgImage"));
          Graphics2D g = imgMap.getBufferedImg().createGraphics();
          diagram.render(g);

          Pixel pixel = new Pixel();
          _points = new ArrayList<Point>();
          double w2 = (double) imgMap.getImageWidth() / 2.0;
          double h2 = (double) imgMap.getImageHeight() / 2.0;
          for (int i = 0; i < imgMap.getImageHeight(); i++) {
            for (int j = 0; j < imgMap.getImageWidth(); j++) {
              int argb = imgMap.getARGBValue(j, i);
              if (argb != 0) {
                double x = ((double) j - w2) / (double) imgMap.getImageWidth();
                double y = ((double) i - h2) / (double) imgMap.getImageHeight();
                pixel.setARGBValue(argb);
                _points.add(new Point(x, y, pixel.r, pixel.g, pixel.b));
              }
            }
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
          _points = new ArrayList<Point>();
        }
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
    return new byte[][] { (svg != null ? svg.getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_SVG.equalsIgnoreCase(pName)) {
      svg = pValue != null ? new String(pValue) : "";
      _points = null;
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_SVG.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    }
    else
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
      if (direct_color == 1) {
        pVarTP.rgbColor = true;
        pVarTP.redColor = point.r;
        pVarTP.greenColor = point.g;
        pVarTP.blueColor = point.b;
      }
    }
    else {
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
    return "svg_wf";
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE;
  }
}
