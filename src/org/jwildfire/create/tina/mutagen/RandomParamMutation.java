/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.mutagen;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.VariationFunc;

public class RandomParamMutation implements Mutation {

  @Override
  public void execute(Flame pFlame) {
    setRandomFlameProperty(pFlame, 6.0 * (0.25 + 0.75 * Math.random()));
    setRandomFlameProperty(pFlame, 5.0 * (0.25 + 0.75 * Math.random()));
    setRandomFlameProperty(pFlame, 2.0 * (0.25 + 0.75 * Math.random()));
  }

  private void setRandomFlameProperty(Flame pFlame, double pAmount) {
    List<VariationFunc> variations = new ArrayList<VariationFunc>();

    for (XForm xForm : pFlame.getXForms()) {
      addVariations(variations, xForm);
    }
    for (XForm xForm : pFlame.getFinalXForms()) {
      addVariations(variations, xForm);
    }

    if (variations.size() > 0) {
      int idx = (int) (Math.random() * variations.size());
      VariationFunc var = variations.get(idx);
      int pIdx = (int) (Math.random() * var.getParameterNames().length);
      Object oldVal = var.getParameterValues()[pIdx];
      if (oldVal instanceof Integer) {
        int o = (Integer) oldVal;
        System.out.print("SETI(" + var.getParameterNames()[pIdx] + ") " + o + "->");
        int da = Tools.FTOI(pAmount);
        if (da < 1) {
          da = 1;
        }
        o += Math.random() < 0.5 ? da : -da;
        System.out.println(o);
        var.setParameter(var.getParameterNames()[pIdx], o);
      }
      else if (oldVal instanceof Double) {
        double o = (Double) oldVal;
        System.out.print("SETF(" + var.getParameterNames()[pIdx] + ") " + o + "->");
        if (o < EPSILON || Math.random() < 0.3) {
          o += 0.1 * (Math.random() < 0.5 ? pAmount : -pAmount);
        }
        else {
          o += o / 100.0 * (Math.random() < 0.5 ? pAmount : -pAmount);
        }
        System.out.println(o);
        var.setParameter(var.getParameterNames()[pIdx], o);
      }
    }
  }

  private void addVariations(List<VariationFunc> pVariations, XForm pXForm) {
    if (pXForm.getVariationCount() > 0) {
      for (int i = 0; i < pXForm.getVariationCount(); i++) {
        VariationFunc var = pXForm.getVariation(i).getFunc();
        if (var.getParameterNames() != null && var.getParameterNames().length > 0) {
          pVariations.add(var);
        }
      }
    }
  }

}
