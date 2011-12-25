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
package org.jwildfire.create.tina.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JToggleButton;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class FlamePanel extends ImagePanel {
  private final static int BORDER = 20;
  private static final Color XFORM_COLOR = new Color(217, 219, 223);
  private static final Color XFORM_POST_COLOR = new Color(255, 219, 160);
  private static final Color XFORM_COLOR_DARK = new Color(17, 19, 23);
  private static final Color XFORM_POST_COLOR_DARK = new Color(55, 19, 60);
  private static final Color BACKGROUND_COLOR = new Color(60, 60, 60);
  private static float LINE_WIDTH = 1.6f;
  private static float LINE_WIDTH_FAT = 1.8f * LINE_WIDTH;
  private static float DASH1[] = { 6.0f };
  private static BasicStroke DASHED = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, DASH1, 0.0f);
  private static BasicStroke SOLID = new BasicStroke(LINE_WIDTH);
  private static BasicStroke SOLID_FAT = new BasicStroke(LINE_WIDTH_FAT);
  private static BasicStroke DASHED_FAT = new BasicStroke(LINE_WIDTH_FAT, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, DASH1, 0.0f);

  private static final long serialVersionUID = 1L;
  private final FlameHolder flameHolder;

  private boolean darkTriangles = false;
  private boolean drawImage = true;
  private boolean drawFlame = true;
  private boolean fineMovement = false;
  private XForm selectedXForm = null;

  private double viewXScale, viewYScale;
  private double viewXTrans, viewYTrans;
  private MouseDragOperation mouseDragOperation = MouseDragOperation.MOVE;
  private int xBeginDrag, yBeginDrag;
  private boolean editPostTransform = false;
  private double zoom = 1.0;

  private int renderWidth;
  private int renderHeight;
  private double renderAspect = 1.0;

  private final JToggleButton toggleTrianglesButton;

  public FlamePanel(SimpleImage pSimpleImage, int pX, int pY, int pWidth, FlameHolder pFlameHolder, JToggleButton pToggleTrianglesButton) {
    super(pSimpleImage, pX, pY, pWidth);
    flameHolder = pFlameHolder;
    toggleTrianglesButton = pToggleTrianglesButton;
  }

  @Override
  public void paintComponent(Graphics g) {
    fillBackground(g);
    if (drawImage) {
      super.paintComponent(g);
    }
    if (drawFlame) {
      paintFlame((Graphics2D) g);
    }
  }

  private void fillBackground(Graphics g) {
    Rectangle bounds = this.getBounds();
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, bounds.width, bounds.height);
  }

  private class Triangle {
    private final XForm xForm;
    double x[] = new double[3];
    double y[] = new double[3];
    int viewX[] = new int[3];
    int viewY[] = new int[3];

    public Triangle(XForm pXForm) {
      xForm = pXForm;
      // x
      x[0] = 1.0;
      y[0] = 0.0;
      // 0
      x[1] = 0.0;
      y[1] = 0.0;
      // y
      x[2] = 0.0;
      y[2] = 1.0;

      // transform the points (affine transform)
      for (int i = 0; i < x.length; i++) {
        //          
        // use the same layout as Apophysis
        double tx = affineTransformedX(x[i], y[i]);
        double ty = affineTransformedY(x[i], y[i]);
        viewX[i] = xToView(tx);
        viewY[i] = yToView(ty);
      }
    }

    public double getC00() {
      return editPostTransform ? xForm.getPostCoeff00() : xForm.getCoeff00();
    }

    public double getC01() {
      return editPostTransform ? xForm.getPostCoeff01() : xForm.getCoeff01();
    }

    public double getC10() {
      return editPostTransform ? xForm.getPostCoeff10() : xForm.getCoeff10();
    }

    public double getC11() {
      return editPostTransform ? xForm.getPostCoeff11() : xForm.getCoeff11();
    }

    public double getC20() {
      return editPostTransform ? xForm.getPostCoeff20() : xForm.getCoeff20();
    }

    public double getC21() {
      return editPostTransform ? xForm.getPostCoeff21() : xForm.getCoeff21();
    }

    public double affineTransformedX(double pX, double pY) {
      //      return pX * xForm.getCoeff00() + pY * xForm.getCoeff10() + xForm.getCoeff20();
      // use the same layout as Apophysis
      return pX * getC00() + (-pY * getC10()) + getC20();
    }

    public double affineTransformedY(double pX, double pY) {
      //      return pX * xForm.getCoeff01() + pY * xForm.getCoeff11() + xForm.getCoeff21();
      // use the same layout as Apophysis
      return (-pX * getC01()) + pY * getC11() + (-getC21());
    }
  }

  Rectangle getImageBounds() {
    Rectangle bounds = this.getParent().getBounds();
    double aspect = (double) bounds.width / (double) bounds.height;
    int imageWidth, imageHeight;
    if (aspect <= renderAspect) {
      imageWidth = bounds.width;
      imageHeight = Tools.FTOI((double) imageWidth / renderAspect);
    }
    else {
      imageHeight = bounds.height;
      imageWidth = Tools.FTOI((double) imageHeight * renderAspect);
    }
    //    System.out.println(bounds.width + "x" + bounds.height + "->" + imageWidth + "x" + imageHeight);
    return new Rectangle(0, 0, imageWidth, imageHeight);
  }

  private void paintFlame(Graphics2D g) {
    Flame flame = flameHolder.getFlame();
    if (flame != null) {
      Rectangle bounds = this.getImageBounds();
      int width = bounds.width;
      int height = bounds.height;

      int areaLeft = BORDER;
      // unused:
      //      int areaRight = width - 1 - BORDER;
      //      int areaTop = BORDER;
      int areaBottom = height - 1 - BORDER;
      g.setColor(editPostTransform ? (darkTriangles ? XFORM_POST_COLOR_DARK : XFORM_POST_COLOR) : (darkTriangles ? XFORM_COLOR_DARK : XFORM_COLOR));

      double viewXMin = -1.25;
      double viewXMax = 1.25;
      double viewYMin = -1.25;
      double viewYMax = 1.25;

      viewXScale = (double) (width - 2 * BORDER) / (viewXMax - viewXMin);
      viewYScale = (double) (height - 2 * BORDER) / (viewYMin - viewYMax);
      viewXTrans = viewXMin * viewXScale - areaLeft;
      viewYTrans = viewYMin * viewYScale - areaBottom;
      viewXScale /= renderAspect;

      for (XForm xForm : flame.getXForms()) {
        drawXForm(g, xForm, false);
      }
      if (flame.getFinalXForm() != null) {
        drawXForm(g, flame.getFinalXForm(), true);
      }
    }
  }

  private void drawXForm(Graphics2D g, XForm pXForm, boolean pIsFinal) {
    Triangle triangle = new Triangle(pXForm);
    if (selectedXForm != null && selectedXForm == pXForm) {
      g.setStroke(pIsFinal ? SOLID_FAT : SOLID);
    }
    else {
      g.setStroke(pIsFinal ? DASHED_FAT : DASHED);
    }
    g.drawPolygon(triangle.viewX, triangle.viewY, 3);

    g.setStroke(SOLID);
    int radius = 10;
    g.drawOval(triangle.viewX[1] - radius / 2, triangle.viewY[1] - radius / 2, radius, radius);
  }

  private int xToView(double pX) {
    return Tools.FTOI(viewXScale * zoom * pX - viewXTrans);
  }

  private int yToView(double pY) {
    return Tools.FTOI(viewYScale * zoom * pY - viewYTrans);
  }

  private double viewToX(int viewX) {
    return ((double) viewX + viewXTrans) / (viewXScale * zoom);
  }

  private double viewToY(int viewY) {
    return ((double) viewY + viewYTrans) / (viewYScale * zoom);
  }

  public void setDrawImage(boolean drawImage) {
    this.drawImage = drawImage;
  }

  public void setDrawFlame(boolean drawFlame) {
    this.drawFlame = drawFlame;
  }

  public void setSelectedXForm(XForm selectedXForm) {
    this.selectedXForm = selectedXForm;
  }

  public boolean mouseDragged(int pX, int pY) {
    if (selectedXForm != null && mouseDragOperation != MouseDragOperation.NONE) {
      int viewDX = pX - xBeginDrag;
      int viewDY = pY - yBeginDrag;
      if (viewDX != 0 || viewDY != 0) {
        double dx = viewToX(pX) - viewToX(xBeginDrag);
        double dy = viewToY(pY) - viewToY(yBeginDrag);
        xBeginDrag = pX;
        yBeginDrag = pY;
        if (Math.abs(dx) > Tools.EPSILON || Math.abs(dy) > Tools.EPSILON) {
          switch (mouseDragOperation) {
            case MOVE: {
              if (fineMovement) {
                dx *= 0.25;
                dy *= 0.25;
              }
              // move
              if (editPostTransform) {
                selectedXForm.setPostCoeff20(selectedXForm.getPostCoeff20() + dx);
                selectedXForm.setPostCoeff21(selectedXForm.getPostCoeff21() - dy);
              }
              else {
                selectedXForm.setCoeff20(selectedXForm.getCoeff20() + dx);
                selectedXForm.setCoeff21(selectedXForm.getCoeff21() - dy);
              }
              return true;
            }
            case SCALE: {
              if (fineMovement) {
                dx *= 0.1;
                dy *= 0.1;
              }
              Triangle triangle = new Triangle(selectedXForm);
              double v1x = triangle.x[0] - triangle.x[1];
              double v1y = triangle.y[0] - triangle.y[1];
              double v2x = v1x + dx;
              double v2y = v1y + dy;
              double dr1 = Math.sqrt(v1x * v1x + v1y * v1y);
              double dr2 = Math.sqrt(v2x * v2x + v2y * v2y);
              double scale = dr2 / dr1;
              if (editPostTransform) {
                selectedXForm.setPostCoeff00(selectedXForm.getPostCoeff00() * scale);
                selectedXForm.setPostCoeff01(selectedXForm.getPostCoeff01() * scale);
                selectedXForm.setPostCoeff10(selectedXForm.getPostCoeff10() * scale);
                selectedXForm.setPostCoeff11(selectedXForm.getPostCoeff11() * scale);
              }
              else {
                selectedXForm.setCoeff00(selectedXForm.getCoeff00() * scale);
                selectedXForm.setCoeff01(selectedXForm.getCoeff01() * scale);
                selectedXForm.setCoeff10(selectedXForm.getCoeff10() * scale);
                selectedXForm.setCoeff11(selectedXForm.getCoeff11() * scale);
              }
              return true;
            }
            case ROTATE: {
              if (fineMovement) {
                dx *= 0.1;
              }
              XFormTransformService.rotate(selectedXForm, dx * 30, editPostTransform);
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public void mousePressed(int x, int y) {
    if (selectedXForm != null) {
      xBeginDrag = x;
      yBeginDrag = y;
      if (!drawFlame) {
        drawFlame = true;
        toggleTrianglesButton.setSelected(true);
      }
    }
  }

  // Algorithm from http://www.blackpawn.com/texts/pointinpoly/default.html
  private boolean insideTriange(Triangle pTriangle, int viewX, int viewY) {
    // Compute vectors
    //    v0 = C - A
    //    v1 = B - A
    //    v2 = P - A
    // A: pTriangle,view[0]
    // B: pTriangle,view[1]
    // C: pTriangle,view[2]
    double v0x = pTriangle.viewX[2] - pTriangle.viewX[0];
    double v0y = pTriangle.viewY[2] - pTriangle.viewY[0];
    double v1x = pTriangle.viewX[1] - pTriangle.viewX[0];
    double v1y = pTriangle.viewY[1] - pTriangle.viewY[0];
    double v2x = viewX - pTriangle.viewX[0];
    double v2y = viewY - pTriangle.viewY[0];
    // Compute dot products
    double dot00 = v0x * v0x + v0y * v0y;
    double dot01 = v0x * v1x + v0y * v1y;
    double dot02 = v0x * v2x + v0y * v2y;
    double dot11 = v1x * v1x + v1y * v1y;
    double dot12 = v1x * v2x + v1y * v2y;

    // Compute barycentric coordinates
    double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    // Check if point is in triangle
    return (u >= 0) && (v >= 0) && (u + v < 1);
  }

  public XForm mouseClicked(int x, int y) {
    // select flame
    Flame flame = flameHolder.getFlame();
    if (flame != null) {
      for (XForm xForm : flame.getXForms()) {
        Triangle triangle = new Triangle(xForm);
        if (insideTriange(triangle, x, y)) {
          return xForm;
        }
      }
      if (flame.getFinalXForm() != null) {
        Triangle triangle = new Triangle(flame.getFinalXForm());
        if (insideTriange(triangle, x, y)) {
          return flame.getFinalXForm();
        }
      }
    }
    return null;
  }

  public void setMouseDragOperation(MouseDragOperation mouseDragOperation) {
    this.mouseDragOperation = mouseDragOperation;
  }

  public void setEditPostTransform(boolean pEditPostTransform) {
    editPostTransform = pEditPostTransform;
  }

  public void zoomIn() {
    if (zoom > 0.2) {
      zoom -= 0.1;
    }
    else if (zoom > 0.05) {
      zoom -= 0.01;
    }
    else {
      zoom -= 0.001;
    }
  }

  public void zoomOut() {
    zoom += 0.1;
  }

  public void setRenderWidth(int pRenderWidth) {
    renderWidth = pRenderWidth;
    updateRenderAspect();
  }

  private void updateRenderAspect() {
    if (renderHeight > 0) {
      renderAspect = (double) renderWidth / (double) renderHeight;
    }
  }

  void setRenderHeight(int pRenderHeight) {
    renderHeight = pRenderHeight;
    updateRenderAspect();
  }

  public void setFineMovement(boolean pFineMovement) {
    fineMovement = pFineMovement;
  }

  public void setDarkTriangles(boolean darkTriangles) {
    this.darkTriangles = darkTriangles;
  }
}
