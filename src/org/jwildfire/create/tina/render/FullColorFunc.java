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

public class FullColorFunc implements ColorFunc {
  private AbstractRandomGenerator randGen;
  private Envelope rrEnvelope;
  private Envelope rgEnvelope;
  private Envelope rbEnvelope;
  private Envelope grEnvelope;
  private Envelope ggEnvelope;
  private Envelope gbEnvelope;
  private Envelope brEnvelope;
  private Envelope bgEnvelope;
  private Envelope bbEnvelope;

  @Override
  public double mapRGBToR(double pR, double pG, double pB) {
    return (rrEnvelope.evaluate(pR) + rgEnvelope.evaluate(pG) + rbEnvelope.evaluate(pB)) * noise();
  }

  @Override
  public double mapRGBToG(double pR, double pG, double pB) {
    return (grEnvelope.evaluate(pR) + ggEnvelope.evaluate(pG) + gbEnvelope.evaluate(pB)) * noise();
  }

  @Override
  public double mapRGBToB(double pR, double pG, double pB) {
    return (brEnvelope.evaluate(pR) + bgEnvelope.evaluate(pG) + bbEnvelope.evaluate(pB)) * noise();
  }

  @Override
  public void prepare(Flame pFlame, AbstractRandomGenerator pRandGen) {
    randGen = pRandGen;
    rrEnvelope = pFlame.getMixerRRCurve().toEnvelope();
    rgEnvelope = pFlame.getMixerRGCurve().toEnvelope();
    rbEnvelope = pFlame.getMixerRBCurve().toEnvelope();
    grEnvelope = pFlame.getMixerGRCurve().toEnvelope();
    ggEnvelope = pFlame.getMixerGGCurve().toEnvelope();
    gbEnvelope = pFlame.getMixerGBCurve().toEnvelope();
    brEnvelope = pFlame.getMixerBRCurve().toEnvelope();
    bgEnvelope = pFlame.getMixerBGCurve().toEnvelope();
    bbEnvelope = pFlame.getMixerBBCurve().toEnvelope();
  }

  private double noise() {
    return (1.03 - randGen.random() * 0.06);
  }
}
