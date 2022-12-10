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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.mutagen.PainterlyStyleMutation;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.create.tina.variation.AbstractBrushStrokeWFFunc.*;

public class BrushStrokeDOFBlurShape extends AbstractDOFBlurShape {
  private XYZPoint s, d;
  private VariationFunc fnc;
  private XForm xform;

  @Override
  public void applyDOFAndCamera(XYZPoint pSrc, XYZPoint pDest, double pZDist, double pZR) {
    s.assign(pSrc);
    d.assign(pSrc);
    double dr = camDOF_10 * pZDist * scale * doFade();
    fnc.transform(flameTransformationContext, xform, s, d, dr);
    rotate(s, d);
    pDest.x = d.x / pZR;
    pDest.y = d.y / pZR;
  }

  @Override
  public void prepare(FlameTransformationContext pFlameTransformationContext, AbstractRandomGenerator pRandGen, double pCamDOF_10) {
    super.prepare(pFlameTransformationContext, pRandGen, pCamDOF_10);
    s = new XYZPoint();
    d = new XYZPoint();
    xform = new XForm();
    fnc = new PostBrushStrokeWFFunc();
    fnc.setParameter(AbstractBrushStrokeWFFunc.PARAM_BLEND, 0.0);
    List<String> brushes = new ArrayList<>();
    addBrush(brushes, params.get(PARAM_BRUSH1));
    addBrush(brushes, params.get(PARAM_BRUSH2));
    addBrush(brushes, params.get(PARAM_BRUSH3));
    if(!brushes.isEmpty()) {
      fnc.setRessource(
         AbstractBrushStrokeWFFunc.RESSOURCE_BRUSH_PRESETS,
         String.join(", ", brushes).getBytes(StandardCharsets.UTF_8));
    }
    for (String paramName : getParamNames()) {
      if (!PARAM_BRUSH1.equals(paramName)
          && !PARAM_BRUSH2.equals(paramName)
          && !PARAM_BRUSH3.equals(paramName)) {
        fnc.setParameter(paramName, params.get(paramName));
      }
    }
    fnc.initOnce(pFlameTransformationContext, new Layer(), xform, 1.0);
    fnc.init(pFlameTransformationContext, new Layer(), xform, 1.0);
  }

  private void addBrush(List<String> brushes, Double brushId) {
    if(brushId!=null) {
      int brushIntId = Tools.FTOI(brushId);
      if(brushIntId>0 && brushIntId<=PainterlyStyleMutation.MAX_BRUSH) {
        brushes.add(PainterlyStyleMutation.formatBrushId(brushIntId));
      }
    }
  }

  private static final String PARAM_BRUSH1 = "BRUSH1";
  private static final String PARAM_BRUSH2 = "BRUSH2";
  private static final String PARAM_BRUSH3 = "BRUSH3";
  private static final List<String> paramNames =
      new ArrayList<String>(
          Arrays.asList(
              new String[] {PARAM_BRUSH1, PARAM_BRUSH2, PARAM_BRUSH3, PARAM_VARIATION_ROTATION, PARAM_VARIATION_ZOOM, PARAM_VARIATION_POSITION}));

  @Override
  public List<String> getParamNames() {
    return paramNames;
  }

  @Override
  public void setDefaultParams(Flame pFlame) {
    pFlame.setCamDOFFade(0.0);
    pFlame.setCamDOFScale(1.0);
    pFlame.setCamDOFAngle(0.0);
    pFlame.setCamDOFParam1(PainterlyStyleMutation.getRandomBrushId());// brush1
    pFlame.setCamDOFParam2(Math.random()>0.25 ? PainterlyStyleMutation.getRandomBrushId() : 0);// brush2
    pFlame.setCamDOFParam3(Math.random()>0.75 ? PainterlyStyleMutation.getRandomBrushId() : 0);// brush3
    pFlame.setCamDOFParam4(2.0 * M_PI);// variation_rotation
    pFlame.setCamDOFParam5(0.8);// variation_zoom
    pFlame.setCamDOFParam6(0.5);// variation_position
  }

  @Override
  public double getScaleModifier() {
    return 10.0;
  }

  @Override
  public boolean isUseColor() {
    return false;
  }

}
