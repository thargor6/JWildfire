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
package org.jwildfire.envelope;

import java.awt.Rectangle;

public class EnvelopeView {
  private final int width;
  private final int height;
  private final int envelopeLeft;
  private final int envelopeTop;
  private final int envelopeBottom;
  private final int envelopeRight;
  private final double envelopeXScale;
  private final double envelopeYScale;
  private final double envelopeXTrans;
  private final double envelopeYTrans;

  public EnvelopeView(EnvelopePanel pEnvelopePanel) {
    Rectangle bounds = pEnvelopePanel.getBounds();
    width = bounds.width;
    height = bounds.height;
    Envelope envelope = pEnvelopePanel.getEnvelope();
    final int border = 1;
    envelopeLeft = border;
    envelopeRight = width - 1 - 2 * border;
    envelopeTop = border;
    envelopeBottom = height - 1 - 2 * border;
    if (envelope != null) {
      envelopeXScale = (double) (width - 2 * border)
          / (double) (envelope.getViewXMax() - envelope.getViewXMin());
      envelopeYScale = (double) (height - 2 * border)
          / (envelope.getViewYMin() - envelope.getViewYMax());
      envelopeXTrans = envelope.getViewXMin() * envelopeXScale - (double) envelopeLeft;
      envelopeYTrans = envelope.getViewYMin() * envelopeYScale - (double) envelopeBottom;
    }
    else {
      envelopeXScale = envelopeYScale = 1.0;
      envelopeXTrans = envelopeYTrans = 0.0;
    }
  }

  public int getEnvelopeLeft() {
    return envelopeLeft;
  }

  public int getEnvelopeTop() {
    return envelopeTop;
  }

  public int getEnvelopeBottom() {
    return envelopeBottom;
  }

  public int getEnvelopeRight() {
    return envelopeRight;
  }

  public double getEnvelopeXScale() {
    return envelopeXScale;
  }

  public double getEnvelopeYScale() {
    return envelopeYScale;
  }

  public double getEnvelopeXTrans() {
    return envelopeXTrans;
  }

  public double getEnvelopeYTrans() {
    return envelopeYTrans;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

}
