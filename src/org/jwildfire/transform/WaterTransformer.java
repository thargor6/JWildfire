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
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class WaterTransformer extends Mesh3DTransformer {

  public enum Axis {
    X, Y, XY
  };

  @Property(description = "Wavelength of the wave")
  @PropertyMin(0.0)
  private double wavelength = 150.0;

  @Property(description = "Amplitude of the wave")
  @PropertyMin(0.0)
  private double amplitude = 30.0;

  @Property(description = "Damping of the wave", category = PropertyCategory.SECONDARY)
  private double damping = -0.5;

  @Property(description = "Damping on/off", category = PropertyCategory.SECONDARY)
  private boolean damp = true;

  @Property(description = "Number of frames to describe a complete phase", category = PropertyCategory.SECONDARY)
  @PropertyMin(1)
  private int frames = 60;

  @Property(description = "Current frames", category = PropertyCategory.SECONDARY)
  @PropertyMin(1)
  private int frame = 33;

  @Property(description = "X-coordinate of the wave origin")
  private double originX = 400.0;

  @Property(description = "Y-coordinate of the wave origin")
  private double originY = 400.0;

  @Property(description = "Propagation axis of the wave", editorClass = AxisEditor.class)
  private Axis axis = Axis.XY;

  @Property(description = "Number of rings", category = PropertyCategory.SECONDARY)
  @PropertyMin(1)
  private int rings = 3;

  @Property(description = "How many times to emit", category = PropertyCategory.SECONDARY)
  @PropertyMin(1)
  private int times = 6;

  @Property(description = "Activate movement in x- and y-direction", category = PropertyCategory.SECONDARY)
  private boolean moveXY = true;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int pCount = pMesh3D.getPCount();
    int width = pImageWidth;
    int height = pImageHeight;
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();

    double twoPi = 2.0 * Math.PI;
    double centreX = this.originX - (double) width / 2;
    double centreY = this.originY - (double) height / 2;
    double amplitude = this.amplitude;
    double wavelength = this.wavelength;
    if (wavelength < 2.0)
      wavelength = 2.0;
    double damping = this.damping;
    double times = this.times;
    if (times < 1.0)
      times = 1.0;
    double limit;
    double rings = this.rings;
    if (rings >= 1.0)
      limit = twoPi * rings;
    else
      limit = twoPi;
    double am2 = this.amplitude / 2.0;
    double dt = 1.0 / (double) frames;
    double a0 = twoPi * dt * (frame + 1) * times;
    double div1 = wavelength / twoPi;
    double div2 = twoPi / wavelength;
    double number = a0 * div1 - 1.0;

    if (axis == Axis.XY) {
      if (!damp) {
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - centreX;
          double dy = y[i] - centreY;
          double dr = Math.sqrt(dx * dx + dy * dy);
          double aa;
          if (dr > 0.0) {
            aa = (number - dr + 1.0) * div2;
          }
          else {
            aa = a0;
            dr = 0.0001;
          }
          if ((aa >= 0.0) && (aa < limit)) {
            if (moveXY) {
              double cx = dx;
              double cy = dy;
              dx = dx / dr;
              dy = dy / dr;
              cx = cx - dx * am2;
              cy = cy - dy * am2;
              double rr = amplitude * Math.cos(aa);
              x[i] = cx + centreX + dx * rr;
              y[i] = cy + centreY + dy * rr;
            }
            z[i] += amplitude * Math.sin(aa);
          }
          else {
            if (moveXY) {
              double cx = dx;
              double cy = dy;
              dx = dx / dr;
              dy = dy / dr;
              cx = cx - dx * am2;
              cy = cy - dy * am2;
              double rr = amplitude * Math.cos(aa);
              x[i] = cx + centreX + dx * rr;
              y[i] = cy + centreY + dy * rr;
            }
            z[i] += 0.0;
          }
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - centreX;
          double dy = y[i] - centreY;
          double dr = Math.sqrt(dx * dx + dy * dy);
          double dl = dr / wavelength;
          double dmp = Math.exp(damping * dl);
          double aa;
          if (dr > 0.0) {
            aa = (number - dr + 1.0) * div2;
          }
          else {
            aa = a0;
            dr = 0.0001;
          }
          if ((aa >= 0.0) && (aa < limit)) {
            if (moveXY) {
              double cx = dx;
              double cy = dy;
              dx = dx / dr;
              dy = dy / dr;
              cx = cx - dx * am2;
              cy = cy - dy * am2;
              double rr = amplitude * Math.cos(aa) * dmp;
              x[i] = cx + centreX + dx * rr;
              y[i] = cy + centreY + dy * rr;
            }
            z[i] += amplitude * Math.sin(aa) * dmp;
          }
          else {
            if (moveXY) {
              double cx = dx;
              double cy = dy;
              dx = dx / dr;
              dy = dy / dr;
              cx = cx - dx * am2;
              cy = cy - dy * am2;
              double rr = amplitude * Math.cos(aa) * dmp;
              x[i] = cx + centreX + dx * rr;
              y[i] = cy + centreY + dy * rr;
            }
            z[i] += 0.0;
          }
        }
      }
    }
    else if (axis == Axis.X) {
      if (!damp) {
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - centreX;
          double dr = dx;
          if (dr < 0.0)
            dr = 0.0 - dr;
          double aa;
          if (dr > 0.0) {
            aa = (number - dr + 1.0) * div2;
          }
          else {
            aa = a0;
            dr = 0.0001;
          }
          if ((aa >= 0.0) && (aa < limit)) {
            if (moveXY) {
              double cx = dx;
              dx = dx / dr;
              cx = cx - dx * am2;
              double rr = amplitude * Math.cos(aa);
              x[i] = cx + centreX + dx * rr;
            }
            z[i] += amplitude * Math.sin(aa);
          }
          else {
            if (moveXY) {
              double cx = dx;
              dx = dx / dr;
              cx = cx - dx * am2;
              double rr = amplitude * Math.cos(aa);
              x[i] = cx + centreX + dx * rr;
            }
            z[i] += 0.0;
          }
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - centreX;
          double dr = dx;
          if (dr < 0.0)
            dr = 0.0 - dr;
          double dl = dr / wavelength;
          double dmp = Math.exp(damping * dl);
          double aa;
          if (dr > 0.0) {
            aa = (number - dr + 1.0) * div2;
          }
          else {
            aa = a0;
            dr = 0.0001;
          }
          if ((aa >= 0.0) && (aa < limit)) {
            if (moveXY) {
              double cx = dx;
              dx = dx / dr;
              cx = cx - dx * am2;
              double rr = amplitude * Math.cos(aa) * dmp;
              x[i] = cx + centreX + dx * rr;
            }
            z[i] += amplitude * Math.sin(aa) * dmp;
          }
          else {
            if (moveXY) {
              double cx = dx;
              dx = dx / dr;
              cx = cx - dx * am2;
              double rr = amplitude * Math.cos(aa) * dmp;
              x[i] = cx + centreX + dx * rr;
            }
            z[i] += 0.0;
          }
        }
      }
    }
    else { // (axis == Axis.Y)
      if (!damp) {
        for (int i = 0; i < pCount; i++) {
          double dy = y[i] - centreY;
          double dr = dy;
          if (dr < 0.0)
            dr = 0.0 - dr;
          double aa;
          if (dr > 0.0) {
            aa = (number - dr + 1.0) * div2;
          }
          else {
            aa = a0;
            dr = 0.0001;
          }
          if ((aa >= 0.0) && (aa < limit)) {
            if (moveXY) {
              double cy = dy;
              dy = dy / dr;
              cy = cy - dy * am2;
              double rr = amplitude * Math.cos(aa);
              y[i] = cy + centreY + dy * rr;
            }
            z[i] += amplitude * Math.sin(aa);
          }
          else {
            if (moveXY) {
              double cy = dy;
              dy = dy / dr;
              cy = cy - dy * am2;
              double rr = amplitude * Math.cos(aa);
              y[i] = cy + centreY + dy * rr;
            }
            z[i] += 0.0;
          }
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double dy = y[i] - centreY;
          double dr = dy;
          if (dr < 0.0)
            dr = 0.0 - dr;
          double dl = dr / wavelength;
          double dmp = Math.exp(damping * dl);
          double aa;
          if (dr > 0.0) {
            aa = (number - dr + 1.0) * div2;
          }
          else {
            aa = a0;
            dr = 0.0001;
          }
          if ((aa >= 0.0) && (aa < limit)) {
            if (moveXY) {
              double cy = dy;
              dy = dy / dr;
              cy = cy - dy * am2;
              double rr = amplitude * Math.cos(aa) * dmp;
              y[i] = cy + centreY + dy * rr;
            }
            z[i] += amplitude * Math.sin(aa) * dmp;
          }
          else {
            if (moveXY) {
              double cy = dy;
              dy = dy / dr;
              cy = cy - dy * am2;
              double rr = amplitude * Math.cos(aa) * dmp;
              y[i] = cy + centreY + dy * rr;
            }
            z[i] += 0.0;
          }
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    damp = true;
    damping = -0.1;
    amplitude = (rr) / 120.0;
    wavelength = (rr) / 12.0;
    rings = 5;
    times = 10;
    frame = 33;
    frames = 60;
    originX = width / 2;
    originY = height / 2;
    axis = Axis.XY;
    moveXY = true;
  }

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y, Axis.XY });
    }
  }

  public double getWavelength() {
    return wavelength;
  }

  public void setWavelength(double wavelength) {
    this.wavelength = wavelength;
  }

  public double getAmplitude() {
    return amplitude;
  }

  public void setAmplitude(double amplitude) {
    this.amplitude = amplitude;
  }

  public double getDamping() {
    return damping;
  }

  public void setDamping(double damping) {
    this.damping = damping;
  }

  public boolean isDamp() {
    return damp;
  }

  public void setDamp(boolean damp) {
    this.damp = damp;
  }

  public int getFrames() {
    return frames;
  }

  public void setFrames(int frames) {
    this.frames = frames;
  }

  public int getFrame() {
    return frame;
  }

  public void setFrame(int frame) {
    this.frame = frame;
  }

  public double getOriginX() {
    return originX;
  }

  public void setOriginX(double originX) {
    this.originX = originX;
  }

  public double getOriginY() {
    return originY;
  }

  public void setOriginY(double originY) {
    this.originY = originY;
  }

  public Axis getAxis() {
    return axis;
  }

  public void setAxis(Axis axis) {
    this.axis = axis;
  }

  public int getRings() {
    return rings;
  }

  public void setRings(int rings) {
    this.rings = rings;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public boolean isMoveXY() {
    return moveXY;
  }

  public void setMoveXY(boolean moveXY) {
    this.moveXY = moveXY;
  }

}
