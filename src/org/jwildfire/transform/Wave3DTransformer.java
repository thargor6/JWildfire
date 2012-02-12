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

public class Wave3DTransformer extends Mesh3DTransformer {

  public enum Axis {
    X, Y, XY, RADIAL
  };

  @Property(description = "Damping of the wave", category = PropertyCategory.SECONDARY)
  private double damping = -0.5;

  @Property(description = "Wavelength of the wave")
  @PropertyMin(0.0)
  private double wavelength = 150.0;

  @Property(description = "Amplitude of the wave")
  @PropertyMin(0.0)
  private double amplitude = 30.0;

  @Property(description = "Damping on/off", category = PropertyCategory.SECONDARY)
  private boolean damp = true;

  @Property(description = "Phase shift of the Wave", category = PropertyCategory.SECONDARY)
  private double phase = 30.0;

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

  @Property(description = "Z-coordinate of the wave origin")
  private double originZ = 0.0;

  @Property(description = "Propagation axis of the wave", editorClass = AxisEditor.class)
  private Axis axis = Axis.XY;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int pCount = pMesh3D.getPCount();
    int width = pImageWidth;
    int height = pImageHeight;
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();
    if (this.axis == Axis.X) {
      double amplitude = this.amplitude;
      double phase = Math.PI * this.phase / 180.0;
      double t = (double) this.frame / (double) this.frames;
      double twoPi = 2.0 * Math.PI;
      double centreX = this.originX - (double) width / 2.0;
      double wavelength = this.wavelength;
      if (wavelength < 2.0)
        wavelength = 2.0;
      double damping = this.damping;

      if (!this.damp) {
        double amp = amplitude;
        for (int i = 0; i < pCount; i++) {
          double rr = Math.abs(x[i] - centreX);
          double dl = rr / wavelength;
          z[i] -= amp * (double) Math.sin(twoPi * (t - dl) + phase);
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double rr = Math.abs(x[i] - centreX);
          double dl = rr / wavelength;
          double dmp = dl * damping;
          double amp = amplitude * Math.exp(dmp);
          z[i] -= amp * (double) Math.sin(twoPi * (t - dl) + phase);
        }
      }
    }
    else if (this.axis == Axis.Y) {
      double amplitude = this.amplitude;
      double phase = Math.PI * this.phase / 180.0;
      double t = (double) this.frame / (double) this.frames;
      double twoPi = 2.0 * Math.PI;
      double centreY = this.originY - (double) height / 2.0;
      double wavelength = this.wavelength;
      if (wavelength < 2.0)
        wavelength = 2.0;
      double damping = this.damping;

      if (!this.damp) {
        double amp = amplitude;
        for (int i = 0; i < pCount; i++) {
          double rr = Math.abs(y[i] - centreY);
          double dl = rr / wavelength;
          z[i] -= amp * (double) Math.sin(twoPi * (t - dl) + phase);
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double rr = Math.abs(y[i] - centreY);
          double dl = rr / wavelength;
          double dmp = dl * damping;
          double amp = amplitude * Math.exp(dmp);
          z[i] -= amp * (double) Math.sin(twoPi * (t - dl) + phase);
        }
      }
    }
    else if (this.axis == Axis.XY) {
      double amplitude = this.amplitude;
      double phase = Math.PI * this.phase / 180.0;
      double t = (double) this.frame / (double) this.frames;
      double twoPi = 2.0 * Math.PI;
      double centreX = this.originX - (double) width / 2.0;
      double centreY = this.originY - (double) height / 2.0;
      double wavelength = this.wavelength;
      if (wavelength < 2.0)
        wavelength = 2.0;
      double damping = this.damping;

      if (!this.damp) {
        double amp = amplitude;
        for (int i = 0; i < pCount; i++) {
          double xx = x[i] - centreX;
          double yy = y[i] - centreY;
          double rr = Math.sqrt(xx * xx + yy * yy);
          double dl = rr / wavelength;
          z[i] -= amp * (double) Math.sin(twoPi * (t - dl) + phase);
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double xx = x[i] - centreX;
          double yy = y[i] - centreY;
          double rr = Math.sqrt(xx * xx + yy * yy);
          double dl = rr / wavelength;
          double dmp = dl * damping;
          double amp = amplitude * Math.exp(dmp);
          z[i] -= amp * (double) Math.sin(twoPi * (t - dl) + phase);
        }
      }
    }
    else { /* radial */
      double amplitude = this.amplitude;
      double phase = Math.PI * this.phase / 180.0;
      double t = (double) this.frame / (double) this.frames;
      double twoPi = 2.0 * Math.PI;
      double centreX = this.originX - (double) width / 2.0;
      double centreY = this.originY - (double) height / 2.0;
      double centreZ = this.originZ;
      double wavelength = this.wavelength;
      if (wavelength < 2.0)
        wavelength = 2.0;
      double damping = this.damping;

      if (!this.damp) {
        double amp = amplitude;
        for (int i = 0; i < pCount; i++) {
          double xx = x[i] - centreX;
          double yy = y[i] - centreY;
          double zz = z[i] - centreZ;
          double rr = Math.sqrt(xx * xx + yy * yy + zz * zz);
          double dl = rr / wavelength;
          double amount = amp * (double) Math.sin(twoPi * (t - dl) + phase);
          double vx, vy, vz;
          if (rr > 0.00001) {
            vx = xx / rr;
            vy = yy / rr;
            vz = zz / rr;
          }
          else {
            vx = vy = 0.0;
            vz = 1.0;
          }
          x[i] += vx * amount;
          y[i] += vy * amount;
          z[i] += vz * amount;
        }
      }
      else {
        for (int i = 0; i < pCount; i++) {
          double xx = x[i] - centreX;
          double yy = y[i] - centreY;
          double zz = z[i] - centreZ;
          double rr = Math.sqrt(xx * xx + yy * yy + zz * zz);
          double dl = rr / wavelength;
          double dmp = dl * damping;
          double amp = amplitude * Math.exp(dmp);
          double amount = amp * (double) Math.sin(twoPi * (t - dl) + phase);
          double vx, vy, vz;
          if (rr > 0.00001) {
            vx = xx / rr;
            vy = yy / rr;
            vz = zz / rr;
          }
          else {
            vx = vy = 0.0;
            vz = 1.0;
          }
          x[i] += vx * amount;
          y[i] += vy * amount;
          z[i] += vz * amount;
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
    damping = -0.5;
    wavelength = Math.round(rr / 12.0);
    amplitude = Math.round(rr / 40.0);
    damp = true;
    phase = 30.0;
    frames = 60;
    frame = 33;
    originX = Math.round((double) width / 2.05);
    originY = Math.round((double) height / 1.95);
    originZ = 0.0;
    axis = Axis.XY;
  }

  public double getDamping() {
    return damping;
  }

  public void setDamping(double damping) {
    this.damping = damping;
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

  public boolean isDamp() {
    return damp;
  }

  public void setDamp(boolean damp) {
    this.damp = damp;
  }

  public double getPhase() {
    return phase;
  }

  public void setPhase(double phase) {
    this.phase = phase;
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

  public double getOriginZ() {
    return originZ;
  }

  public void setOriginZ(double originZ) {
    this.originZ = originZ;
  }

  public Axis getAxis() {
    return axis;
  }

  public void setAxis(Axis axis) {
    this.axis = axis;
  }

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y, Axis.XY, Axis.RADIAL });
    }
  }

}
