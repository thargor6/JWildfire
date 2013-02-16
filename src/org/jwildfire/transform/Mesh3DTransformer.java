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
import java.awt.image.BufferedImage;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer.BufferType;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public abstract class Mesh3DTransformer extends Transformer {

  public enum Rotate {
    XY, YZ, XZ, NONE
  }

  public enum Light {
    NORMAL, PHONG, OFF
  };

  public enum Faces {
    NORMAL, DOUBLE, MIRRORED
  };

  @Property(category = PropertyCategory.RENDERING, description = "Type of shading", editorClass = LightEditor.class)
  protected Light light = Light.NORMAL;

  @Property(category = PropertyCategory.RENDERING, description = "Treat faces as single or double sided", editorClass = FacesEditor.class)
  protected Faces faces = Faces.NORMAL;

  @Property(category = PropertyCategory.RENDERING, description = "Rotation mode", editorClass = RotateEditor.class)
  protected Rotate doRotate = Rotate.XY;

  @Property(category = PropertyCategory.RENDERING, description = "Rotation angle alpha")
  @PropertyMin(-360)
  @PropertyMax(360)
  protected double alpha = 30.0;

  @Property(category = PropertyCategory.RENDERING, description = "Rotation angle beta")
  @PropertyMin(-360)
  @PropertyMax(360)
  protected double beta = 60.0;

  @Property(category = PropertyCategory.RENDERING, description = "Object zoom factor")
  @PropertyMin(0.01)
  @PropertyMax(10.0)
  protected double zoom = 1.40;

  @Property(category = PropertyCategory.RENDERING, description = "Detail of the generated 3d-object (higher value -> less detail <-> better performance)")
  @PropertyMin(0.01)
  @PropertyMax(10.0)
  protected double quant3D = 1.0;

  @Property(category = PropertyCategory.RENDERING, description = "X-coordinate of the object centre")
  protected double centreX = 350.0;

  @Property(category = PropertyCategory.RENDERING, description = "Y-coordinate of the object centre")
  protected double centreY = 400.0;

  @Property(category = PropertyCategory.RENDERING, description = "Simulate camera view (perspective)")
  protected boolean doCam = true;

  @Property(category = PropertyCategory.RENDERING, description = "X-coordinate of the camera position")
  protected double camX = 0.0;

  @Property(category = PropertyCategory.RENDERING, description = "Y-coordinate of the camera position")
  protected double camY = 0.0;

  @Property(category = PropertyCategory.RENDERING, description = "Z-coordinate of the camera position")
  protected double camZ = -1000.0;

  @Property(category = PropertyCategory.RENDERING, description = "X-coordinate of the 1st light source")
  protected double light1X = -200.0;

  @Property(category = PropertyCategory.RENDERING, description = "Y-coordinate of the 1st light source")
  protected double light1Y = 100.0;

  @Property(category = PropertyCategory.RENDERING, description = "Z-coordinate of the 1st light source")
  protected double light1Z = -600.0;

  @Property(category = PropertyCategory.RENDERING, description = "Color of the 1st light source")
  protected Color light1Color = new Color(255, 255, 255);

  @Property(category = PropertyCategory.RENDERING, description = "X-coordinate of the 2nd light source")
  protected double light2X = 200.0;

  @Property(category = PropertyCategory.RENDERING, description = "Y-coordinate of the 2nd light source")
  protected double light2Y = 100.0;

  @Property(category = PropertyCategory.RENDERING, description = "Z-coordinate of the 2nd light source")
  protected double light2Z = -600.0;

  @Property(category = PropertyCategory.RENDERING, description = "Color of the 2nd light source")
  protected Color light2Color = new Color(0, 0, 0);

  @Property(category = PropertyCategory.RENDERING, description = "X-coordinate of the 3rd light source")
  protected double light3X = -200.0;

  @Property(category = PropertyCategory.RENDERING, description = "Y-coordinate of the 3rd light source")
  protected double light3Y = -100.0;

  @Property(category = PropertyCategory.RENDERING, description = "Z-coordinate of the 3rd light source")
  protected double light3Z = -600.0;

  @Property(category = PropertyCategory.RENDERING, description = "Color of the 3rd light source")
  protected Color light3Color = new Color(0, 0, 0);

  @Property(category = PropertyCategory.RENDERING, description = "X-coordinate of the 4th light source")
  protected double light4X = 200.0;

  @Property(category = PropertyCategory.RENDERING, description = "Y-coordinate of the 4th light source")
  protected double light4Y = -100.0;

  @Property(category = PropertyCategory.RENDERING, description = "Z-coordinate of the 4th light source")
  protected double light4Z = -600.0;

  @Property(category = PropertyCategory.RENDERING, description = "Color of the 4th light source")
  protected Color light4Color = new Color(0, 0, 0);

  @Property(category = PropertyCategory.RENDERING, description = "Ambient light intensity")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  protected double ambient = 0.06;

  @Property(category = PropertyCategory.RENDERING, description = "Diffuse light intensity")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  protected double diffuse = 0.94;

  @Property(category = PropertyCategory.RENDERING, description = "Phong light intensity")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  protected double phong = 0.900;

  @Property(category = PropertyCategory.RENDERING, description = "Sharpness of the phong spot size (the higher the value the more the sharpness)")
  @PropertyMin(0.0)
  protected double phongSize = 30.00;

  @Property(category = PropertyCategory.RENDERING, description = "Phong angle")
  @PropertyMin(-360.0)
  @PropertyMax(360.0)
  protected double phongAngle = 60.00;

  @Property(description = "Smoothing amount", category = PropertyCategory.SECONDARY)
  private int smoothing = 1;

  @Property(description = "Image scale", category = PropertyCategory.SECONDARY)
  private double imgScale = 1.0;

  protected abstract void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight);

  private Mesh3D inputMesh3D;
  private Mesh3D outputMesh3D;

  @Override
  public void setInputMesh3D(Mesh3D pInputMesh3D) {
    inputMesh3D = pInputMesh3D;
  }

  @Override
  public Mesh3D getOutputMesh3D(boolean pRemoveOwnReference) {
    Mesh3D res = outputMesh3D;
    if (pRemoveOwnReference)
      outputMesh3D = null;
    return res;
  }

  protected void createMeshFromImage(Mesh3D pMesh3D, SimpleImage pImg, double pQuant3D) {
    pMesh3D.readPixels(pImg, pQuant3D);
  }

  @Override
  protected void performImageTransformation(WFImage pImg) {
    Mesh3D lInputMesh3D;
    SimpleImage img = (SimpleImage) pImg;
    if (inputMesh3D == null) {
      lInputMesh3D = new Mesh3D();
      createMeshFromImage(lInputMesh3D, img, quant3D);
    }
    else {
      lInputMesh3D = inputMesh3D.clone();
    }
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    transformMesh(lInputMesh3D, width, height);
    if (storeMesh3D) {
      this.outputMesh3D = lInputMesh3D.clone();
      this.outputMesh3D.setLastTransformation(this);
    }
    transform3D(lInputMesh3D, width, height);
    img.fillBackground(0, 0, 0);
    render3D(lInputMesh3D, img);
    applySmoothing(img, smoothing);
    lInputMesh3D = null;
    inputMesh3D = null;
  }

  private void transform3D(Mesh3D pMesh3D, int pWidth, int pHeight) {
    /* check the parameters */
    int width = pWidth;
    int height = pHeight;
    int pCount = pMesh3D.getPCount();
    double zeroX = (double) centreX - (double) width / 2.0;
    double zeroY = (double) centreY - (double) height / 2.0;
    if ((Math.abs(alpha) <= MathLib.EPSILON) && (Math.abs(beta) <= MathLib.EPSILON))
      doRotate = Rotate.NONE;
    boolean doZoom = Math.abs(zoom - 1.0) > MathLib.EPSILON;
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();

    /*  rotate it */
    if (doRotate != Rotate.NONE) {
      double alpha = this.alpha * Math.PI / 180.0;
      double beta = this.beta * Math.PI / 180.0;
      double sinA = Math.sin(alpha);
      double cosA = Math.cos(alpha);
      double sinB = Math.sin(beta);
      double cosB = Math.cos(beta);

      if (doRotate == Rotate.XY) {
        double sinBsinA = sinB * sinA;
        double cosBsinA = cosB * sinA;
        double sinBcosA = sinB * cosA;
        double cosBcosA = cosB * cosA;
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - zeroX;
          double dy = y[i] - zeroY;
          double dz = z[i];
          x[i] = cosA * dx - sinBsinA * dy + cosBsinA * dz + zeroX;
          y[i] = cosB * dy + sinB * dz + zeroY;
          z[i] = -sinA * dx - sinBcosA * dy + cosBcosA * dz;
        }
      }
      else if (doRotate == Rotate.YZ) {
        double cosAcosB = cosA * cosB;
        double sinAcosB = sinA * cosB;
        double cosAsinB = cosA * sinB;
        double sinAsinB = sinA * sinB;
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - zeroX;
          double dy = y[i] - zeroY;
          double dz = z[i];
          x[i] = cosAcosB * dx + sinAcosB * dy + sinB * dz + zeroX;
          y[i] = -sinA * dx + cosA * dy + zeroY;
          z[i] = -cosAsinB * dx - sinAsinB * dy + cosB * dz;
        }
      }
      else if (doRotate == Rotate.XZ) {
        double cosBsinA = cosB * sinA;
        double sinBsinA = sinB * sinA;
        double cosBcosA = cosB * cosA;
        double sinBcosA = sinB * cosA;
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - zeroX;
          double dy = y[i] - zeroY;
          double dz = z[i];
          x[i] = cosA * dx + cosBsinA * dy + sinBsinA * dz + zeroX;
          y[i] = -sinA * dx + cosBcosA * dy + sinBcosA * dz + zeroY;
          z[i] = -sinB * dy + cosB * dz;
        }
      }
    }
    /* zoom it */
    if (doZoom) {
      double zoom = this.zoom;
      for (int i = 0; i < pCount; i++) {
        double dx = x[i] - zeroX;
        double dy = y[i] - zeroY;
        x[i] = dx * zoom + zeroX;
        y[i] = dy * zoom + zeroY;
        z[i] *= zoom;
      }
    }
    /* add camera */
    if (doCam) {
      double camX = this.camX;
      double camY = this.camY;
      double camZ = this.camZ;
      if (camZ > (-50.0))
        camZ = -50.0;
      for (int i = 0; i < pCount; i++) {
        double dx = x[i];
        double dy = y[i];
        double dz = z[i];
        dx -= camX;
        dy += camY;
        double ttf = camZ / ((double) camZ - dz);
        dx *= ttf;
        dy *= ttf;
        dz *= ttf;
        x[i] = dx;
        y[i] = dy;
        z[i] = dz;
      }
    }
  }

  private void render3D(Mesh3D pMesh3D, SimpleImage pImg) {

    Mesh3DRenderer renderer;
    if (light != Light.PHONG)
      renderer = new Simple3DRenderer();
    else
      renderer = new Phong3DRenderer();
    if (Math.abs(imgScale - 1.0) > MathLib.EPSILON) {
      // scaled image
      int width = (int) ((double) pImg.getImageWidth() * imgScale + 0.5);
      int height = (int) ((double) pImg.getImageHeight() * imgScale + 0.5);
      BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      pImg.setBufferedImage(img, width, height);
      // scaled mesh
      Mesh3D mesh3D = pMesh3D.clone(imgScale);
      // scaled params
      double oCentreX = centreX;
      double oCentreY = centreY;
      double oCamX = camX;
      double oCamY = camY;
      double oCamZ = camZ;
      double oLight1X = light1X;
      double oLight1Y = light1Y;
      double oLight1Z = light1Z;
      double oLight2X = light2X;
      double oLight2Y = light2Y;
      double oLight2Z = light2Z;
      double oLight3X = light3X;
      double oLight3Y = light3Y;
      double oLight3Z = light3Z;
      double oLight4X = light4X;
      double oLight4Y = light4Y;
      double oLight4Z = light4Z;
      double oPhongSize = phongSize;
      try {
        oCentreX *= imgScale;
        oCentreY *= imgScale;
        oCamX *= imgScale;
        oCamY *= imgScale;
        oCamZ *= imgScale;
        oLight1X *= imgScale;
        oLight1Y *= imgScale;
        oLight1Z *= imgScale;
        oLight2X *= imgScale;
        oLight2Y *= imgScale;
        oLight2Z *= imgScale;
        oLight3X *= imgScale;
        oLight3Y *= imgScale;
        oLight3Z *= imgScale;
        oLight4X *= imgScale;
        oLight4Y *= imgScale;
        oLight4Z *= imgScale;
        oPhongSize *= imgScale;
        // render
        renderer.renderImage(mesh3D, this, pImg);
      }
      finally {
        // reset params
        centreX = oCentreX;
        centreY = oCentreY;
        camX = oCamX;
        camY = oCamY;
        camZ = oCamZ;
        light1X = oLight1X;
        light1Y = oLight1Y;
        light1Z = oLight1Z;
        light2X = oLight2X;
        light2Y = oLight2Y;
        light2Z = oLight2Z;
        light3X = oLight3X;
        light3Y = oLight3Y;
        light3Z = oLight3Z;
        light4X = oLight4X;
        light4Y = oLight4Y;
        light4Z = oLight4Z;
        phongSize = oPhongSize;
      }
    }
    else {

      renderer.renderImage(pMesh3D, this, pImg);
    }

    /*

    {*/
    /* antialiasing */
    /*     
          if(r3dSmoothing!=0) {
           UBYTE r1,r2,r3,g1,g2,g3,b1,b2,b3,r,g,b,*dred,*dgreen,*dblue;
           WORD av1,av2,av3,av,d1,d2,d3,rw1,k;
           ULONG rv,gv,bv;
           #ifdef SDEBUG
           ULONG pixels;
           #endif
           rw1=width16-width+1;
           for(k=0;k<r3dSmoothing;k++) {
           dred=red;
           dgreen=green;
           dblue=blue;
           #ifdef SDEBUG
           pixels=0;
           #endif
           // copy 1st line 
           dred+=width16;dgreen+=width16;dblue+=width16;
           for(i=1;i<(height-1);i++) {
            // copy last pixel 
            dred++;dgreen++;dblue++;
            // process pixels 2..width-1 
            for(j=1;j<(width-1);j++) {
             r1=*(dred-width16);g1=*(dgreen-width16);b1=*(dblue-width16);
             r2=*(dred+1);g2=*(dgreen+1);b2=*(dblue+1);
             r3=*(dred+width16);g3=*(dgreen+width16);b3=*(dblue+width16);
             r=*(dred);g=*(dgreen);b=*(dblue);

             av1=((WORD)r1+(WORD)g1+(WORD)b1);av2=((WORD)r2+(WORD)g2+(WORD)b2);
             av3=((WORD)r3+(WORD)g3+(WORD)b3);av=((WORD)r+(WORD)g+(WORD)b);

             d1=av1-av;if(d1<0) d1=0-d1;d2=av2-av;if(d2<0) d2=0-d2;d3=av3-av;if(d3<0) d3=0-d3;

             if((d1>64) && (d2>64)) {
              rv=(LONG)r1*(LONG)d1+(LONG)r2*(LONG)d2;
              rv/=(LONG)(d1+d2);rv+=(WORD)r;rv/=2;
              gv=(LONG)g1*(LONG)d1+(LONG)g2*(LONG)d2;
              gv/=(LONG)(d1+d2);gv+=(WORD)g;gv/=2;
              bv=(LONG)b1*(LONG)d1+(LONG)b2*(LONG)d2;
              bv/=(LONG)(d1+d2);bv+=(WORD)b;bv/=2;
              *dred++=(UBYTE)rv;*dgreen++=(UBYTE)gv;*dblue++=(UBYTE)bv;
              #ifdef SDEBUG
              pixels++;
              #endif
             }
             else if((d2>64) && (d3>64)) {
              rv=(LONG)r2*(LONG)d2+(LONG)r3*(LONG)d3;
              rv/=(LONG)(d2+d3);rv+=(WORD)r;rv/=2;
              gv=(LONG)g2*(LONG)d2+(LONG)g3*(LONG)d3;
              gv/=(LONG)(d2+d3);gv+=(WORD)g;gv/=2;
              bv=(LONG)b2*(LONG)d2+(LONG)b3*(LONG)d3;
              bv/=(LONG)(d2+d3);bv+=(WORD)b;bv/=2;
              *dred++=(UBYTE)rv;*dgreen++=(UBYTE)gv;*dblue++=(UBYTE)bv;
              #ifdef SDEBUG
              pixels++;
              #endif
             }
             else {
              *dred++;*dgreen++;*dblue++;
             }
            }
            // copy last pixel 
            dred+=rw1;dgreen+=rw1;dblue+=rw1;
           }
           #ifdef SDEBUG
           printf("pixels: %ld\n",pixels);
           #endif
          }
          }
         }
    */

  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    light = Light.NORMAL;
    faces = Faces.NORMAL;
    doRotate = Rotate.XY;
    alpha = 30.0;
    beta = 40.0;
    zoom = 1.40;
    quant3D = 1.0;
    centreX = Math.round((double) width / 2.05);
    centreY = Math.round((double) height / 1.95);
    doCam = true;
    camX = Math.round(0.005 * rr);
    camY = Math.round(-0.01 * rr);
    camZ = Math.round(-4.0 * rr);
    light1X = -200.0;
    light1Y = 100.0;
    light1Z = -600.0;
    light1Color = new Color(255, 255, 255);
    light2X = 200.0;
    light2Y = 100.0;
    light2Z = -600.0;
    light2Color = new Color(0, 0, 0);
    light3X = -200.0;
    light3Y = -100.0;
    light3Z = -600.0;
    light3Color = new Color(0, 0, 0);
    light4X = 200.0;
    light4Y = -100.0;
    light4Z = -600.0;
    light4Color = new Color(0, 0, 0);
    ambient = 0.06;
    diffuse = 0.94;
    phong = 0.900;
    phongSize = Math.round(rr / 20.0);
    phongAngle = 60.00;
    smoothing = 1;
    imgScale = 1.0;
  }

  @Override
  public boolean acceptsInputBufferType(BufferType pBufferType) {
    return (pBufferType == BufferType.IMAGE) || (pBufferType == BufferType.MESH3D);
  }

  public Light getLight() {
    return light;
  }

  public void setLight(Light light) {
    this.light = light;
  }

  public Faces getFaces() {
    return faces;
  }

  public void setFaces(Faces faces) {
    this.faces = faces;
  }

  public Rotate getDoRotate() {
    return doRotate;
  }

  public void setDoRotate(Rotate doRotate) {
    this.doRotate = doRotate;
  }

  public double getAlpha() {
    return alpha;
  }

  public void setAlpha(double alpha) {
    this.alpha = alpha;
  }

  public double getBeta() {
    return beta;
  }

  public void setBeta(double beta) {
    this.beta = beta;
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = zoom;
  }

  public double getQuant3D() {
    return quant3D;
  }

  public void setQuant3D(double quant3d) {
    quant3D = quant3d;
  }

  public double getCentreX() {
    return centreX;
  }

  public void setCentreX(double centreX) {
    this.centreX = centreX;
  }

  public double getCentreY() {
    return centreY;
  }

  public void setCentreY(double centreY) {
    this.centreY = centreY;
  }

  public boolean isDoCam() {
    return doCam;
  }

  public void setDoCam(boolean doCam) {
    this.doCam = doCam;
  }

  public double getCamX() {
    return camX;
  }

  public void setCamX(double camX) {
    this.camX = camX;
  }

  public double getCamY() {
    return camY;
  }

  public void setCamY(double camY) {
    this.camY = camY;
  }

  public double getCamZ() {
    return camZ;
  }

  public void setCamZ(double camZ) {
    this.camZ = camZ;
  }

  public double getLight1X() {
    return light1X;
  }

  public void setLight1X(double light1x) {
    light1X = light1x;
  }

  public double getLight1Y() {
    return light1Y;
  }

  public void setLight1Y(double light1y) {
    light1Y = light1y;
  }

  public double getLight1Z() {
    return light1Z;
  }

  public void setLight1Z(double light1z) {
    light1Z = light1z;
  }

  public Color getLight1Color() {
    return light1Color;
  }

  public void setLight1Color(Color light1Color) {
    this.light1Color = light1Color;
  }

  public double getLight2X() {
    return light2X;
  }

  public void setLight2X(double light2x) {
    light2X = light2x;
  }

  public double getLight2Y() {
    return light2Y;
  }

  public void setLight2Y(double light2y) {
    light2Y = light2y;
  }

  public double getLight2Z() {
    return light2Z;
  }

  public void setLight2Z(double light2z) {
    light2Z = light2z;
  }

  public Color getLight2Color() {
    return light2Color;
  }

  public void setLight2Color(Color light2Color) {
    this.light2Color = light2Color;
  }

  public double getLight3X() {
    return light3X;
  }

  public void setLight3X(double light3x) {
    light3X = light3x;
  }

  public double getLight3Y() {
    return light3Y;
  }

  public void setLight3Y(double light3y) {
    light3Y = light3y;
  }

  public double getLight3Z() {
    return light3Z;
  }

  public void setLight3Z(double light3z) {
    light3Z = light3z;
  }

  public Color getLight3Color() {
    return light3Color;
  }

  public void setLight3Color(Color light3Color) {
    this.light3Color = light3Color;
  }

  public double getLight4X() {
    return light4X;
  }

  public void setLight4X(double light4x) {
    light4X = light4x;
  }

  public double getLight4Y() {
    return light4Y;
  }

  public void setLight4Y(double light4y) {
    light4Y = light4y;
  }

  public double getLight4Z() {
    return light4Z;
  }

  public void setLight4Z(double light4z) {
    light4Z = light4z;
  }

  public Color getLight4Color() {
    return light4Color;
  }

  public void setLight4Color(Color light4Color) {
    this.light4Color = light4Color;
  }

  public double getAmbient() {
    return ambient;
  }

  public void setAmbient(double ambient) {
    this.ambient = ambient;
  }

  public double getDiffuse() {
    return diffuse;
  }

  public void setDiffuse(double diffuse) {
    this.diffuse = diffuse;
  }

  public double getPhong() {
    return phong;
  }

  public void setPhong(double phong) {
    this.phong = phong;
  }

  public double getPhongSize() {
    return phongSize;
  }

  public void setPhongSize(double phongSize) {
    this.phongSize = phongSize;
  }

  public double getPhongAngle() {
    return phongAngle;
  }

  public void setPhongAngle(double phongAngle) {
    this.phongAngle = phongAngle;
  }

  public static class RotateEditor extends ComboBoxPropertyEditor {
    public RotateEditor() {
      super();
      setAvailableValues(new Rotate[] { Rotate.XY, Rotate.YZ, Rotate.XZ, Rotate.NONE });
    }
  }

  public static class LightEditor extends ComboBoxPropertyEditor {
    public LightEditor() {
      super();
      setAvailableValues(new Light[] { Light.NORMAL, Light.PHONG, Light.OFF });
    }
  }

  public static class FacesEditor extends ComboBoxPropertyEditor {
    public FacesEditor() {
      super();
      setAvailableValues(new Faces[] { Faces.NORMAL, Faces.DOUBLE, Faces.MIRRORED });
    }
  }

  @Override
  public boolean supports3DOutput() {
    return true;
  }

  protected void applySmoothing(SimpleImage pImg, int pSmoothingAmount) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel pixel = new Pixel();
    for (int k = 0; k < pSmoothingAmount; k++) {
      for (int i = 1; i < (height - 1); i++) {
        /* process pixels 2..width-1 */
        for (int j = 1; j < (width - 1); j++) {
          pixel.setARGBValue(pImg.getARGBValue(j, i - 1));
          int r1 = pixel.r;
          int g1 = pixel.g;
          int b1 = pixel.b;
          pixel.setARGBValue(pImg.getARGBValue(j + 1, i));
          int r2 = pixel.r;
          int g2 = pixel.g;
          int b2 = pixel.b;
          pixel.setARGBValue(pImg.getARGBValue(j, i + 1));
          int r3 = pixel.r;
          int g3 = pixel.g;
          int b3 = pixel.b;
          pixel.setARGBValue(pImg.getARGBValue(j, i));
          int r = pixel.r;
          int g = pixel.g;
          int b = pixel.b;

          int av1 = (r1 + g1 + b1);
          int av2 = (r2 + g2 + b2);
          int av3 = (r3 + g3 + b3);
          int av = (r + g + b);

          int d1 = av1 - av;
          if (d1 < 0)
            d1 = 0 - d1;
          int d2 = av2 - av;
          if (d2 < 0)
            d2 = 0 - d2;
          int d3 = av3 - av;
          if (d3 < 0)
            d3 = 0 - d3;

          if ((d1 > 64) && (d2 > 64)) {
            int rv = r1 * d1 + r2 * d2;
            rv /= (d1 + d2);
            rv += r;
            rv /= 2;
            int gv = g1 * d1 + g2 * d2;
            gv /= (d1 + d2);
            gv += g;
            gv /= 2;
            int bv = b1 * d1 + b2 * d2;
            bv /= (d1 + d2);
            bv += b;
            bv /= 2;
            pImg.setRGB(j, i, rv, gv, bv);
          }
          else if ((d2 > 64) && (d3 > 64)) {
            int rv = r2 * d2 + r3 * d3;
            rv /= (d2 + d3);
            rv += r;
            rv /= 2;
            int gv = g2 * d2 + g3 * d3;
            gv /= (d2 + d3);
            gv += g;
            gv /= 2;
            int bv = b2 * d2 + b3 * d3;
            bv /= (d2 + d3);
            bv += b;
            bv /= 2;
            pImg.setRGB(j, i, rv, gv, bv);
          }
        }
      }
    }
  }

  public int getSmoothing() {
    return smoothing;
  }

  public void setSmoothing(int smoothing) {
    this.smoothing = smoothing;
  }

  public double getImgScale() {
    return imgScale;
  }

  public void setImgScale(double imgScale) {
    this.imgScale = imgScale;
  }

}
