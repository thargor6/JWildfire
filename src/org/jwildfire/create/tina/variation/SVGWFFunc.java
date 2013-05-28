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
import java.awt.RenderingHints;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGRoot;
import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.animation.AnimationElement;

public class SVGWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_RESOLUTION_MULTIPLIER = "resolution_multiplier";
  private static final String PARAM_ANTIALIAS = "antialias";
  private static final String PARAM_TRUE_COLOR = "true_color";

  private static final String RESSOURCE_SVG = "svg";

  private static final String[] paramNames = { PARAM_ANTIALIAS, PARAM_RESOLUTION_MULTIPLIER, PARAM_TRUE_COLOR, PARAM_SCALEX, PARAM_SCALEY, PARAM_OFFSETX, PARAM_OFFSETY };
  private static final String[] ressourceNames = { RESSOURCE_SVG };

  private double antialias = 0.15;
  private double resolution_multiplier = 3.0;
  private double scale_x = 1.0;
  private double scale_y = 1.0;
  private double offset_x = 0.0;
  private double offset_y = 0.0;
  private int true_color = 1;
  private String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
      +
      "<!-- Created with Inkscape (http://www.inkscape.org/) -->\r\n"
      +
      "\r\n"
      +
      "<svg\r\n"
      +
      "   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n"
      +
      "   xmlns:cc=\"http://creativecommons.org/ns#\"\r\n"
      +
      "   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n"
      +
      "   xmlns:svg=\"http://www.w3.org/2000/svg\"\r\n"
      +
      "   xmlns=\"http://www.w3.org/2000/svg\"\r\n"
      +
      "   xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\"\r\n"
      +
      "   xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\"\r\n"
      +
      "   width=\"744.09448819\"\r\n"
      +
      "   height=\"1052.3622047\"\r\n"
      +
      "   id=\"svg3073\"\r\n"
      +
      "   version=\"1.1\"\r\n"
      +
      "   inkscape:version=\"0.48.2 r9819\"\r\n"
      +
      "   sodipodi:docname=\"New document 2\">\r\n"
      +
      "  <defs\r\n"
      +
      "     id=\"defs3075\" />\r\n"
      +
      "  <sodipodi:namedview\r\n"
      +
      "     id=\"base\"\r\n"
      +
      "     pagecolor=\"#ffffff\"\r\n"
      +
      "     bordercolor=\"#666666\"\r\n"
      +
      "     borderopacity=\"1.0\"\r\n"
      +
      "     inkscape:pageopacity=\"0.0\"\r\n"
      +
      "     inkscape:pageshadow=\"2\"\r\n"
      +
      "     inkscape:zoom=\"0.35\"\r\n"
      +
      "     inkscape:cx=\"-67.857143\"\r\n"
      +
      "     inkscape:cy=\"520\"\r\n"
      +
      "     inkscape:document-units=\"px\"\r\n"
      +
      "     inkscape:current-layer=\"layer1\"\r\n"
      +
      "     showgrid=\"false\"\r\n"
      +
      "     inkscape:window-width=\"1103\"\r\n"
      +
      "     inkscape:window-height=\"779\"\r\n"
      +
      "     inkscape:window-x=\"50\"\r\n"
      +
      "     inkscape:window-y=\"50\"\r\n"
      +
      "     inkscape:window-maximized=\"0\" />\r\n"
      +
      "  <metadata\r\n"
      +
      "     id=\"metadata3078\">\r\n"
      +
      "    <rdf:RDF>\r\n"
      +
      "      <cc:Work\r\n"
      +
      "         rdf:about=\"\">\r\n"
      +
      "        <dc:format>image/svg+xml</dc:format>\r\n"
      +
      "        <dc:type\r\n"
      +
      "           rdf:resource=\"http://purl.org/dc/dcmitype/StillImage\" />\r\n"
      +
      "        <dc:title></dc:title>\r\n"
      +
      "      </cc:Work>\r\n"
      +
      "    </rdf:RDF>\r\n"
      +
      "  </metadata>\r\n"
      +
      "  <g\r\n"
      +
      "     inkscape:label=\"Layer 1\"\r\n"
      +
      "     inkscape:groupmode=\"layer\"\r\n"
      +
      "     id=\"layer1\">\r\n"
      +
      "    <path\r\n"
      +
      "       sodipodi:type=\"spiral\"\r\n"
      +
      "       style=\"fill:none;stroke:#ca0000;stroke-width:16.5;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:4;stroke-opacity:1;stroke-dasharray:none\"\r\n"
      +
      "       id=\"path3083\"\r\n"
      +
      "       sodipodi:cx=\"151.42857\"\r\n"
      +
      "       sodipodi:cy=\"240.93361\"\r\n"
      +
      "       sodipodi:expansion=\"1\"\r\n"
      +
      "       sodipodi:revolution=\"5\"\r\n"
      +
      "       sodipodi:radius=\"189.90868\"\r\n"
      +
      "       sodipodi:argument=\"-30.630527\"\r\n"
      +
      "       sodipodi:t0=\"0\"\r\n"
      +
      "       d=\"m 151.42857,240.93361 c 4.03972,4.03973 -3.45241,7.43852 -6.71429,6.71428 -8.83948,-1.96265 -10.26047,-13.28493 -6.71427,-20.14286 6.34334,-12.26725 22.81557,-13.70434 33.57144,-6.71426 15.78467,10.25822 17.25027,32.4755 6.71424,47.00001 -14.04286,19.35888 -42.18132,20.83969 -60.42857,6.71423 -22.962309,-17.77545 -24.451617,-51.90903 -6.71422,-73.85715 21.48235,-26.58212 61.64905,-28.07656 87.28571,-6.71421 30.21193,25.17479 31.70969,71.39676 6.7142,100.71429 -28.85828,33.84826 -81.14958,35.34827 -114.14286,6.71419 -37.489056,-32.53587 -38.990671,-90.90602 -6.714174,-127.57144 36.209354,-41.13305 100.665074,-42.63584 141.000004,-6.71416 44.77941,39.87987 46.28308,110.42612 6.71415,154.42858 -43.54818,48.42756 -120.18868,49.93192 -167.857146,6.71414 C 12.065671,281.00445 10.560779,198.2668 57.428657,146.93353 108.30875,91.205765 197.14583,89.700442 252.14295,140.21941 c 59.3793,54.54434 60.88497,149.48269 6.7141,208.14286 -58.20772,63.03157 -159.248846,64.53753 -221.571432,6.71409 -66.684436,-61.87039 -68.190635,-169.01554 -6.71408,-235 65.532497,-70.337805 178.782712,-71.844205 248.428572,-6.71407 73.9916,69.19411 75.49818,188.55026 6.71406,261.85715\"\r\n"
      +
      "       transform=\"translate(222.85714,262.85715)\" />\r\n" +
      "  </g>\r\n" +
      "</svg>\r\n" +
      "";

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { antialias, resolution_multiplier, true_color, scale_x, scale_y, offset_x, offset_y };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANTIALIAS.equalsIgnoreCase(pName))
      antialias = pValue;
    else if (PARAM_RESOLUTION_MULTIPLIER.equalsIgnoreCase(pName)) {
      resolution_multiplier = pValue;
      if (resolution_multiplier < 0.1) {
        resolution_multiplier = 0.1;
      }
      else if (resolution_multiplier > 100.0) {
        resolution_multiplier = 100.0;
      }
    }
    else if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scale_x = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scale_y = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offset_x = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offset_y = pValue;
    else if (PARAM_TRUE_COLOR.equalsIgnoreCase(pName))
      true_color = Tools.FTOI(pValue);
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

  }

  private List<Point> _points;
  private RenderColor[] renderColors;

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
    renderColors = pContext.getFlameRenderer().getColorMap();
  }

  private String makeRessourceKey() {
    return getName() + "#" + svg + "#" + resolution_multiplier;
  }

  @SuppressWarnings("unchecked")
  private List<Point> getPoints() {
    if (_points == null) {
      String key = makeRessourceKey();
      _points = (List<Point>) RessourceManager.getRessource(key);
      if (_points == null) {
        try {
          SVGUniverse svgUniverse = new SVGUniverse();
          StringReader reader = new StringReader(svg);
          SVGDiagram diagram = svgUniverse.getDiagram(svgUniverse.loadSVG(reader, "svgImage"));

          int imgWidth = Tools.FTOI(diagram.getWidth() * resolution_multiplier);
          int imgHeight = Tools.FTOI(diagram.getHeight() * resolution_multiplier);

          SVGRoot root = diagram.getRoot();
          root.setAttribute("width", AnimationElement.AT_XML, Integer.toString(imgWidth));
          root.setAttribute("height", AnimationElement.AT_XML, Integer.toString(imgHeight));
          root.build();

          SimpleImage imgMap = new SimpleImage(imgWidth, imgHeight);

          Graphics2D g = imgMap.getBufferedImg().createGraphics();
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
      double rawX = point.x;
      double rawY = point.y;
      if (antialias > 0.01) {
        double dr = (exp(antialias * sqrt(-log(pContext.random()))) - 1.0) * 0.001;
        double da = pContext.random() * 2.0 * M_PI;
        rawX += dr * cos(da);
        rawY += dr * sin(da);
      }
      pVarTP.x += (rawX * scale_x + offset_x) * DFLT_SCALE;
      pVarTP.y += (rawY * scale_y + offset_y) * DFLT_SCALE;
      if (true_color == 1) {
        pVarTP.rgbColor = true;
        pVarTP.redColor = point.r;
        pVarTP.greenColor = point.g;
        pVarTP.blueColor = point.b;
      }
      pVarTP.color = getColorIdx(point.r, point.g, point.b);
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

}
