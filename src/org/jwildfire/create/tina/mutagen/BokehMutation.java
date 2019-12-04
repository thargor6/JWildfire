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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.render.dof.DOFBlurShapeType;
import org.jwildfire.create.tina.variation.CrackleFunc;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class BokehMutation implements Mutation {

  @Override
  public void execute(Layer pLayer) {
    Flame flame = pLayer.getOwner();

    flame.setCamDOF(0.1 + Math.random() * 0.3);
    flame.setNewCamDOF(true);
    flame.setCamPitch(30 + Math.random() * 20.0);
    flame.setCamYaw(15 - Math.random() * 30.0);
    flame.setCamBank(15 - Math.random() * 30.0);
    flame.setCamPerspective(0.05 + Math.random() * 0.2);
    flame.setCamDOFArea(0.2 + Math.random() * 0.5);

    for (Layer layer : flame.getLayers()) {
      Variation crackle = null;
      for (XForm xform : layer.getXForms()) {
        for (int i = xform.getVariationCount() - 1; i >= 0; i--) {
          Variation var = xform.getVariation(i);
          if (var.getFunc().getName().equals(CrackleFunc.VAR_NAME)) {
            VariationFunc varFunc = var.getFunc();
            double scale = (Double) varFunc.getParameter(CrackleFunc.PARAM_SCALE);
            if (MathLib.fabs(scale) < MathLib.EPSILON) {
              crackle = var;
              xform.setColor(Math.random());
              break;
            }
          }
        }
      }

      if (crackle == null) {
        XForm xform = new XForm();
        xform.setWeight(0.5);
        layer.getXForms().add(xform);
        crackle = xform.addVariation(1.0, VariationFuncList.getVariationFuncInstance(CrackleFunc.VAR_NAME));
        crackle.getFunc().setParameter(CrackleFunc.PARAM_SCALE, 0.0);
      }

      crackle.setAmount(1.0 + Math.random() * 2.0);
      crackle.getFunc().setParameter(CrackleFunc.PARAM_DISTORT, 1.5 + Math.random() * 1.5);
      crackle.getFunc().setParameter(CrackleFunc.PARAM_CELLSIZE, 0.5 + Math.random() * 2.0);

      if (Math.random() < 0.33) {
        flame.setFocusX(0.33 - Math.random() * 0.66);
        flame.setFocusY(0.25 - Math.random() * 0.50);
        flame.setFocusZ(0.1 - Math.random() * 0.2);
      }
      else {
        flame.setFocusX(0.0);
        flame.setFocusY(0.0);
        flame.setFocusZ(0.0);
      }

      flame.setCamDOFScale(1.5 + Math.random() * 2.0);
      flame.setCamDOFAngle(20.0 * Math.random());
      flame.setCamDOFParam1(0);
      flame.setCamDOFParam2(0);
      flame.setCamDOFParam3(0);
      flame.setCamDOFParam4(0);
      flame.setCamDOFParam5(0);

      double rnd = Math.random();
      if (rnd < 0.08) {
        flame.setCamDOFShape(DOFBlurShapeType.BUBBLE);
        flame.setCamDOFFade(0.6 + Math.random() * 0.4);
      }
      else if (rnd < 0.16) {
        flame.setCamDOFShape(DOFBlurShapeType.HEART);
        if (Math.random() < 0.25) {
          flame.setCamDOFFade(0.2 + Math.random() * 0.8);
        }
        else {
          flame.setCamDOFFade(0.0);
        }
      }
      else if (rnd < 0.24) {
        flame.setCamDOFShape(DOFBlurShapeType.CANNABISCURVE);
        if (Math.random() < 0.25) {
          flame.setCamDOFFade(0.2 + Math.random() * 0.8);
        }
        else {
          flame.setCamDOFFade(0.0);
        }
      }
      else if (rnd < 0.32) {
        flame.setCamDOFShape(DOFBlurShapeType.NBLUR);
        flame.setCamDOFFade(0.0);
        flame.setCamDOFParam1(3 + Math.random() * 5); // num edges
        if (Math.random() < 0.33) {
          flame.setCamDOFParam2(2 + Math.random() * 5); // num stripes         
          flame.setCamDOFParam3(1);// ratio stripes
          flame.setCamDOFParam4(0);// ratio hole
          flame.setCamDOFParam5(Math.random() < 0.33 ? 1 : 0);// circum circle
        }
      }
      else if (rnd < 0.40) {
        flame.setCamDOFShape(DOFBlurShapeType.FLOWER);
        flame.setCamDOFFade(0.0);
        flame.setCamDOFParam1(0.3 + Math.random() * 0.2); // holes
        flame.setCamDOFParam2(5 + Math.random() * 5); // petals
      }
      else if (rnd < 0.48) {
        flame.setCamDOFShape(DOFBlurShapeType.CLOVERLEAF);
        if (Math.random() < 0.25) {
          flame.setCamDOFFade(0.2 + Math.random() * 0.8);
        }
        else {
          flame.setCamDOFFade(0.0);
        }
      }
      else if (rnd < 0.56) {
        flame.setCamDOFShape(DOFBlurShapeType.SINEBLUR);
        flame.setCamDOFFade(0.0);
        flame.setCamDOFParam1(1.2 + Math.random());
      }
      else if (rnd < 0.64) {
        flame.setCamDOFShape(DOFBlurShapeType.PERLIN_NOISE);
        flame.setCamDOFFade(0.0);
        flame.setCamDOFParam1(Math.random());// shape
        flame.setCamDOFParam2(1.2 + Math.random() * 1.8);// freqs
        flame.setCamDOFParam3(0.1 + Math.random() * 0.4);// amp
      }
      else if (rnd < 0.72) {
        flame.setCamDOFFade(0.2 + Math.random() * 0.8);
        flame.setCamDOFShape(DOFBlurShapeType.STARBLUR);
        flame.setCamDOFParam1(4 + Math.random() * 6);// power
        flame.setCamDOFParam2(0.40162283177245455973959534526548);// range
      }
      else if (rnd < 0.80) {
        flame.setCamDOFAngle(0.0);
        flame.setCamDOFFade(0.);
        flame.setCamDOFShape(DOFBlurShapeType.TAURUS);
        flame.setCamDOFParam1(2.5 + Math.random());// r
        flame.setCamDOFParam2(4 + Math.random() * 3.0);// n
        flame.setCamDOFParam3(1.25 * Math.random() * 0.5);// inv
        flame.setCamDOFParam4(0.9 + Math.random() * 0.2);// sor
      }
      else if (rnd < 0.9) {
        flame.setCamDOFShape(DOFBlurShapeType.RECT);
        flame.setCamDOFFade(0.0);
        flame.setCamDOFParam1(0.4 + Math.random() * 0.4);
      }
    }
  }

}
