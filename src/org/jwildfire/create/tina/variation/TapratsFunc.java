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


/**
 * @author Jesus Sosa
 * @date May 15, 2018
 */


import csk.taprats.geometry.Map;
import csk.taprats.tile.Tiling;
import csk.taprats.tile.TilingViewer;
import csk.taprats.ui1.RenderStyle;
import csk.taprats.ui1.RenderVecView;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.plot.DrawFunc;

public class TapratsFunc extends DrawFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_ID = "Id";
  private static final String PARAM_STYLE = "style";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_GAP = "gap";
  private static final String PARAM_FILL = "fill";
  private static final String PARAM_RQ = "Rosette Q";
  private static final String PARAM_RS = "Rosett  S";
  private static final String PARAM_ROSETTEFLAG = "Rosette(1/0)";
  private static final String PARAM_SD = "Star D";
  private static final String PARAM_SS = "Star S";
  private static final String PARAM_INFERIR = "Infer(1/0)";

  private static final String[] paramNames = {
          PARAM_ID, PARAM_STYLE, PARAM_WIDTH, PARAM_GAP, PARAM_FILL, PARAM_RQ, PARAM_RS, PARAM_ROSETTEFLAG, PARAM_SD, PARAM_SS, PARAM_INFERIR};


  private int id = 0;
  private int style = 2;
  private int fill = 0;
  private double q = 0.0;
  private int s = 2;
  private int rosetteflag = 1;
  private double sd = 3.0;
  private int ss = 2;
  private int infer = 0;

  private double width = 0.06;
  private double gap = 0.08;

  public Tiling tiling = null;
  public Map map = null;
  public RenderStyle rstyle = null;

  public RenderVecView renderview = null;
  public TilingViewer tt = null;

  private String[] patterns = {"csk_7", "4.8^2", "10", "6", "4.6.12", "3.12^2", "csk_5", "csk_9", "18", "8.12", "9.12",
          "square_12", "16.8", "12.18", "alhambra16", "8_var", "18.9", "24.12", "14",
          "6.5x6", "3.4.6", "8 Ring", "8 and 8", "Seville Alcazar 12.8.6",
          "7.6", "Al-Mustansiriyya", "6.5 flower", "9.6", "9.5.3.6", "8 6 ~5",   //0-29
          "pbn_7", "Alcazar De Seville 12 8 - Shorter", "Green Mosque", "Ilkhanid Uljaytu",
          "Ilkhanid Uljaytu Vault", "Mamluk Quran", "Mughal I'timad", "Sultan Lodge",
          "Timurid Tumaq Aqa", /* "7.6.5" ,*/ /*"Great Mosque of Malatya" ,*/ "3-8-4-6", //39
          /* "8-6-5", */ "9.6 ~5", "Square 12.4.3", "wonka", "var_5", "9.6 Losange",
          /* "Curvy Hex" ,*/ /*"4.5" ,*/  "6.5", "6.4 Boats", "6.4 Circles", /* "PentaFlower" , */
          "4.7 Stars", /* "6 Losange" ,*/  /*"Square Wave" */};  // 0-48

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    renderview = null;
    renderview = new RenderVecView(patterns[id], style, width, gap, (fill == 1), q, s, rosetteflag, sd, ss, infer);
    primitives = renderview.getPrimitives();
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{id, style, width, gap, fill, q, s, rosetteflag, sd, ss, infer};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ID.equalsIgnoreCase(pName)) {
      id = (int) Tools.limitValue(pValue, 0, patterns.length);
    } else if (PARAM_STYLE.equalsIgnoreCase(pName)) {
      style = (int) Tools.limitValue(pValue, 0, 5);
    } else if (PARAM_WIDTH.equalsIgnoreCase(pName))
      width = pValue;
    else if (PARAM_GAP.equalsIgnoreCase(pName))
      gap = pValue;
    else if (PARAM_FILL.equalsIgnoreCase(pName))
      fill = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_RQ.equalsIgnoreCase(pName))
      q = Tools.limitValue(pValue, -1.0, 0.9);
    else if (PARAM_RS.equalsIgnoreCase(pName))
      s = (int) Tools.limitValue(pValue, 1, 2);
    else if (PARAM_ROSETTEFLAG.equalsIgnoreCase(pName))
      rosetteflag = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_SD.equalsIgnoreCase(pName))
      sd = Tools.limitValue(pValue, 1.0, 3.5);
    else if (PARAM_SS.equalsIgnoreCase(pName))
      ss = (int) Tools.limitValue(pValue, 1, 3);
    else if (PARAM_INFERIR.equalsIgnoreCase(pName))
      infer = (int) Tools.limitValue(pValue, 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "taprats";
  }

}
