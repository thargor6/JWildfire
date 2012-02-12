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
package org.jwildfire.transform;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ShiftLinesTransformer extends Mesh2DTransformer {
  public enum Jitter {
    ON, POSITIVE, NEGATIVE, OFF
  }

  public enum Axis {
    X, Y
  }

  @Property(description = "Shift probability")
  @PropertyMin(0)
  @PropertyMax(100)
  private int probability = 100;

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed = 123;

  @Property(description = "Shift amount")
  private int shiftAmount = 24;

  @Property(description = "Jitter amount")
  private int jitterAmount = 12;

  @Property(description = "Jitter effect", editorClass = JitterEditor.class)
  private Jitter jitter = Jitter.ON;

  @Property(description = "Shift axis", editorClass = AxisEditor.class)
  private Axis axis = Axis.X;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int dx = this.shiftAmount;
    Tools.srand123(this.seed);
    double rprob = (double) ((double) 1.0 - (double) (this.probability) / (double) 100.0);
    double fdx = (double) this.jitterAmount;

    if (axis == Axis.Y) {
      srcImg = srcImg.clone();
      TurnTransformer tT = new TurnTransformer();
      tT.setAngle(TurnTransformer.Angle._90);
      tT.setDirection(TurnTransformer.Direction.RIGHT);
      tT.transformImage(srcImg);
      tT.setAngle(TurnTransformer.Angle._90);
      tT.setDirection(TurnTransformer.Direction.RIGHT);
      tT.transformImage(img);
    }

    shiftLines: {
      int width = pImg.getImageWidth();
      int height = pImg.getImageHeight();
      int line[] = new int[width];
      if (this.jitter == Jitter.OFF) {
        if (dx == 0) {
          break shiftLines;
        }
        int adx2 = dx;
        if (adx2 < 0)
          adx2 = 0 - adx2;
        if (adx2 >= width) {
          img.fillBackground(0, 0, 0);
          break shiftLines;
        }
        int dx2 = dx;
        int sx = width - adx2;

        for (int i = 0; i < height; i++) {
          clearLine(line);
          if (dx2 > 0) {
            copyLine(line, adx2, srcImg.getLine(i), 0, sx);
          }
          else {
            copyLine(line, 0, srcImg.getLine(i), adx2, sx);
          }
          img.setLine(i, line);
          dx2 = 0 - dx2;
        }
      }
      else {
        for (int i = 0; i < height; i++) {
          int dx3;
          if ((probability >= 100) || (Tools.drand() >= rprob)) {
            dx3 = (int) (Tools.drand() * fdx + 0.5);
            if (this.jitter == Jitter.POSITIVE) {
              if (dx3 < 0)
                dx3 = 0 - dx3;
            }
            else if (this.jitter == Jitter.NEGATIVE) {
              if (dx3 > 0)
                dx3 = 0 - dx3;
            }
            else {
              if (Tools.drand() > 0.5)
                dx3 = 0 - dx3;
            }
          }
          else
            dx3 = 0;
          dx3 += this.shiftAmount;

          if (dx3 != 0) {
            int adx2 = dx3;
            if (adx2 < 0)
              adx2 = 0 - adx2;
            int dx2 = dx3;
            int sx = width - adx2;

            clearLine(line);
            if (adx2 < width) {
              if (dx2 > 0) {
                copyLine(line, adx2, srcImg.getLine(i), 0, sx);
              }
              else {
                copyLine(line, 0, srcImg.getLine(i), adx2, sx);
              }
            }
            img.setLine(i, line);
          }
        }
      }
    }
    if (axis == Axis.Y) {
      TurnTransformer tT = new TurnTransformer();
      tT.setAngle(TurnTransformer.Angle._90);
      tT.setDirection(TurnTransformer.Direction.LEFT);
      tT.transformImage(img);
    }
  }

  private void clearLine(int line[]) {
    for (int i = 0; i < line.length; i++)
      line[i] = 0;
  }

  private void copyLine(int[] dst, int dstOff, int[] src, int srcOff, int size) {
    for (int i = 0; i < size; i++) {
      dst[i + dstOff] = src[i + srcOff];
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    probability = 100;
    seed = 123;
    shiftAmount = 24;
    jitterAmount = 12;
    jitter = Jitter.ON;
    axis = Axis.X;
  }

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y });
    }
  }

  public static class JitterEditor extends ComboBoxPropertyEditor {
    public JitterEditor() {
      super();
      setAvailableValues(new Jitter[] { Jitter.ON, Jitter.POSITIVE, Jitter.NEGATIVE, Jitter.OFF });
    }
  }

  public int getProbability() {
    return probability;
  }

  public void setProbability(int probability) {
    this.probability = probability;
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

  public int getShiftAmount() {
    return shiftAmount;
  }

  public void setShiftAmount(int shiftAmount) {
    this.shiftAmount = shiftAmount;
  }

  public int getJitterAmount() {
    return jitterAmount;
  }

  public void setJitterAmount(int jitterAmount) {
    this.jitterAmount = jitterAmount;
  }

  public Jitter getJitter() {
    return jitter;
  }

  public void setJitter(Jitter jitter) {
    this.jitter = jitter;
  }

  public Axis getAxis() {
    return axis;
  }

  public void setAxis(Axis axis) {
    this.axis = axis;
  }
}
