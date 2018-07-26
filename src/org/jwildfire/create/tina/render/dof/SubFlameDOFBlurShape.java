/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.render.dof;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.create.tina.variation.VariationFunc;

public class SubFlameDOFBlurShape extends AbstractDOFBlurShape {
  private static final String PARAM_FLAME = "flame";
  private static final String PARAM_FLAME_COLORS = "flameColors";
  private XYZPoint s, d;
  private VariationFunc fnc;
  private XForm xform;
  private boolean useColor;

  @Override
  public void applyDOFAndCamera(XYZPoint pSrc, XYZPoint pDest, double pZDist, double pZR) {
    s.assign(pSrc);
    d.assign(pSrc);
    double dr = camDOF_10 * pZDist * scale * doFade();
    fnc.transform(flameTransformationContext, xform, s, d, 1.0);
    d.x = (d.x - pSrc.x) * Math.abs(dr) + pSrc.x;
    d.y = (d.y - pSrc.y) * Math.abs(dr) + pSrc.y;

    rotate(pSrc, d);
    pDest.x = d.x / pZR;
    pDest.y = d.y / pZR;
    if (useColor) {
      pDest.redColor = d.redColor;
      pDest.greenColor = d.greenColor;
      pDest.blueColor = d.blueColor;
      pDest.rgbColor = d.rgbColor;
      pDest.color = d.color;
    }
  }

  @Override
  public void prepare(FlameTransformationContext pFlameTransformationContext, AbstractRandomGenerator pRandGen, double pCamDOF_10) {
    super.prepare(pFlameTransformationContext, pRandGen, pCamDOF_10);
    useColor = Tools.FTOI(params.get(PARAM_FLAME_COLORS)) > 0;
    s = new XYZPoint();
    d = new XYZPoint();
    xform = new XForm();
    fnc = new SubFlameWFFunc();
    for (String paramName : getParamNames()) {
      if (!PARAM_FLAME.equals(paramName) && !PARAM_FLAME_COLORS.equals(paramName)) {
        fnc.setParameter(paramName, params.get(paramName));
      }
    }
    fnc.setRessource(SubFlameWFFunc.RESSOURCE_FLAME, getFlameXML(Tools.FTOI(params.get(PARAM_FLAME))).getBytes());
    fnc.initOnce(pFlameTransformationContext, new Layer(), xform, 1.0);
    fnc.init(pFlameTransformationContext, new Layer(), xform, 1.0);
  }

  private String getFlameXML(int pFlameId) {
    try {
      String id = String.valueOf(pFlameId);
      while (id.length() < 4) {
        id = "0" + id;
      }
      File file = new File(Prefs.getPrefs().getTinaFlamePath(), "_dof_" + id + ".flame");
      if (file.exists()) {
        return Tools.readUTF8Textfile(file.getAbsolutePath());
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return SubFlameWFFunc.DFLT_FLAME_XML;
  }

  private static final List<String> paramNames = new ArrayList<String>(Arrays.asList(new String[] { PARAM_FLAME, PARAM_FLAME_COLORS, SubFlameWFFunc.PARAM_OFFSETX, SubFlameWFFunc.PARAM_OFFSETY }));

  @Override
  public List<String> getParamNames() {
    return paramNames;
  }

  @Override
  public void setDefaultParams(Flame pFlame) {
    pFlame.setCamDOFFade(0.0);
    pFlame.setCamDOFScale(1.0);
    pFlame.setCamDOFAngle(0.0);
    pFlame.setCamDOFParam1(1);// flame
    pFlame.setCamDOFParam2(0);// colors
    pFlame.setCamDOFParam3(0.0);// offset_x
    pFlame.setCamDOFParam4(0.0);// offset_y
  }

  @Override
  public boolean isUseColor() {
    return useColor;
  }

}
