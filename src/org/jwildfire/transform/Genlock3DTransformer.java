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

import java.awt.Color;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class Genlock3DTransformer extends Mesh3DTransformer {

  public enum Genlock {
    COLOR, IN_RANGE, OUT_RANGE
  }

  @Property(category = PropertyCategory.PRIMARY, description = "Genlock mode", editorClass = GenlockEditor.class)
  private Genlock genlock = Genlock.COLOR;
  @Property(category = PropertyCategory.PRIMARY, description = "Genlock color A")
  protected Color colorA = new Color(0, 0, 0);
  @Property(category = PropertyCategory.PRIMARY, description = "Genlock color B")
  protected Color colorB = new Color(0, 0, 0);

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int fCount = pMesh3D.getFCount();
    /* 3-point-polygons */
    if (fCount > 0) {
      Pixel pixel = new Pixel();
      int fLst[] = new int[fCount];
      int color[] = pMesh3D.getColor();
      double u[] = pMesh3D.getU();
      double v[] = pMesh3D.getV();
      int p1[] = pMesh3D.getPP1();
      SimpleImage texture = pMesh3D.getTexture();

      int valid = 0;
      int r1 = this.colorA.getRed();
      int g1 = this.colorA.getGreen();
      int b1 = this.colorA.getBlue();
      int r2 = this.colorB.getRed();
      int g2 = this.colorB.getGreen();
      int b2 = this.colorB.getBlue();
      if (this.genlock == Genlock.COLOR) {
        for (int i = 0; i < fCount; i++) {
          if (color != null) {
            pixel.setARGBValue(color[i]);
          }
          else {
            int px = (int) (u[p1[i]] * texture.getImageWidth() + 0.5);
            int py = (int) (v[p1[i]] * texture.getImageHeight() + 0.5);
            pixel.setARGBValue(texture.getARGBValueIgnoreBounds(px, py));
          }
          if ((pixel.r != r1) || (pixel.g != g1) || (pixel.b != b1)) {
            fLst[i] = 1;
            valid++;
          }
        }
      }
      else if (this.genlock == Genlock.IN_RANGE) {
        for (int i = 0; i < fCount; i++) {
          if (color != null) {
            pixel.setARGBValue(color[i]);
          }
          else {
            int px = (int) (u[p1[i]] * texture.getImageWidth() + 0.5);
            int py = (int) (v[p1[i]] * texture.getImageHeight() + 0.5);
            pixel.setARGBValue(texture.getARGBValueIgnoreBounds(px, py));
          }
          if ((((pixel.r >= r1) && (pixel.r <= r2)) || ((pixel.r >= r2) && (pixel.r <= r1)))
              || (((pixel.g >= g1) && (pixel.g <= g2)) || ((pixel.g >= g2) && (pixel.g <= g1)))
              || (((pixel.b >= b1) && (pixel.b <= b2)) || ((pixel.b >= b2) && (pixel.b <= b1)))) {

          }
          else {
            fLst[i] = 1;
            valid++;
          }
        }
      }
      else if (this.genlock == Genlock.OUT_RANGE) {
        for (int i = 0; i < fCount; i++) {
          if (color != null) {
            pixel.setARGBValue(color[i]);
          }
          else {
            int px = (int) (u[p1[i]] * texture.getImageWidth() + 0.5);
            int py = (int) (v[p1[i]] * texture.getImageHeight() + 0.5);
            pixel.setARGBValue(texture.getARGBValueIgnoreBounds(px, py));
          }
          if ((((pixel.r >= r1) && (pixel.r <= r2)) || ((pixel.r >= r2) && (pixel.r <= r1)))
              || (((pixel.g >= g1) && (pixel.g <= g2)) || ((pixel.g >= g2) && (pixel.g <= g1)))
              || (((pixel.b >= b1) && (pixel.b <= b2)) || ((pixel.b >= b2) && (pixel.b <= b1)))) {
            fLst[i] = 1;
            valid++;
          }
        }
      }

      if (valid < fCount) {
        if (valid > 0) {
          pMesh3D.setFCount(valid);
          {
            int pp[] = new int[valid];
            int curr = 0;
            int ps[] = pMesh3D.getPP1();
            for (int i = 0; i < fCount; i++) {
              if (fLst[i] != 0)
                pp[curr++] = ps[i];
            }
            pMesh3D.setPP1(pp);
          }
          {
            int pp[] = new int[valid];
            int curr = 0;
            int ps[] = pMesh3D.getPP2();
            for (int i = 0; i < fCount; i++) {
              if (fLst[i] != 0)
                pp[curr++] = ps[i];
            }
            pMesh3D.setPP2(pp);
          }
          {
            int pp[] = new int[valid];
            int curr = 0;
            int ps[] = pMesh3D.getPP3();
            for (int i = 0; i < fCount; i++) {
              if (fLst[i] != 0)
                pp[curr++] = ps[i];
            }
            pMesh3D.setPP3(pp);
          }

          if (pMesh3D.getColor() != null) {
            int rr[] = new int[valid];
            int curr = 0;
            int rs[] = pMesh3D.getColor();
            for (int i = 0; i < fCount; i++) {
              if (fLst[i] != 0)
                rr[curr++] = rs[i];
            }
            pMesh3D.setColor(rr);
          }
        }
        else {
          pMesh3D.setFCount(0);
          pMesh3D.setPP1(null);
          pMesh3D.setPP2(null);
          pMesh3D.setPP3(null);
          pMesh3D.setColor(null);
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    genlock = Genlock.COLOR;
    colorA = new Color(0, 0, 0);
    colorB = new Color(0, 0, 0);
  }

  public static class GenlockEditor extends ComboBoxPropertyEditor {
    public GenlockEditor() {
      super();
      setAvailableValues(new Genlock[] { Genlock.COLOR, Genlock.IN_RANGE, Genlock.OUT_RANGE });
    }
  }

  public Genlock getGenlock() {
    return genlock;
  }

  public void setGenlock(Genlock genlock) {
    this.genlock = genlock;
  }

  public Color getColorA() {
    return colorA;
  }

  public void setColorA(Color colorA) {
    this.colorA = colorA;
  }

  public Color getColorB() {
    return colorB;
  }

  public void setColorB(Color colorB) {
    this.colorB = colorB;
  }

}
