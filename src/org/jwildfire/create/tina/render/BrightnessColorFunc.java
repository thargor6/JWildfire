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
package org.jwildfire.create.tina.render;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.envelope.Envelope;

public class BrightnessColorFunc implements ColorFunc {
  private Envelope envelope;

  @Override
  public double mapRGBToR(double pR, double pG, double pB) {
    double brightness = 0.2990 * pR + 0.5880 * pG + 0.1130 * pB;
    return envelope.evaluate(brightness) / brightness * pR;
  }

  @Override
  public double mapRGBToG(double pR, double pG, double pB) {
    double brightness = 0.2990 * pR + 0.5880 * pG + 0.1130 * pB;
    return envelope.evaluate(brightness) / brightness * pG;
  }

  @Override
  public double mapRGBToB(double pR, double pG, double pB) {
    double brightness = 0.2990 * pR + 0.5880 * pG + 0.1130 * pB;
    return envelope.evaluate(brightness) / brightness * pB;
  }

  @Override
  public void prepare(Flame pFlame, AbstractRandomGenerator pRandGen) {
    envelope = pFlame.getMixerRRCurve().toEnvelope();
    envelope.setUseBisection(true);
  }

}
