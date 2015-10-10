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
package org.jwildfire.create.tina.swing.flamepanel;

import java.awt.Color;

import org.jwildfire.create.tina.swing.MouseDragOperation;

public class FlamePanelConfig {
  private boolean editPostTransform = false;
  private boolean withColoredTransforms = false;
  private double triangleViewXScale, triangleViewYScale;
  private double triangleViewXTrans, triangleViewYTrans;
  private double triangleZoom = 1.21;
  private MouseDragOperation mouseDragOperation = MouseDragOperation.MOVE_TRIANGLE;
  private int selectedPoint = 1;
  private boolean noControls;
  private boolean progressivePreview = true;

  // Apophysis-compatible colors
  public static final Color[] XFORM_COLORS = new Color[] {
      new Color(255, 0, 0), new Color(204, 204, 0), new Color(0, 204, 0),
      new Color(0, 204, 204), new Color(64, 64, 255), new Color(204, 0, 204),
      new Color(204, 128, 0), new Color(128, 0, 79), new Color(128, 128, 34),
      new Color(96, 128, 96), new Color(80, 128, 128), new Color(79, 79, 128),
      new Color(128, 80, 128), new Color(128, 96, 34) };

  public static final Color XFORM_COLOR = new Color(217, 219, 223);

  public boolean isEditPostTransform() {
    return editPostTransform;
  }

  public void setEditPostTransform(boolean pEditPostTransform) {
    editPostTransform = pEditPostTransform;
  }

  public double getTriangleViewXScale() {
    return triangleViewXScale;
  }

  public void setTriangleViewXScale(double pTriangleViewXScale) {
    triangleViewXScale = pTriangleViewXScale;
  }

  public double getTriangleViewYScale() {
    return triangleViewYScale;
  }

  public void setTriangleViewYScale(double pTriangleViewYScale) {
    triangleViewYScale = pTriangleViewYScale;
  }

  public double getTriangleViewXTrans() {
    return triangleViewXTrans;
  }

  public void setTriangleViewXTrans(double pTriangleViewXTrans) {
    triangleViewXTrans = pTriangleViewXTrans;
  }

  public double getTriangleViewYTrans() {
    return triangleViewYTrans;
  }

  public void setTriangleViewYTrans(double pTriangleViewYTrans) {
    triangleViewYTrans = pTriangleViewYTrans;
  }

  public double getTriangleZoom() {
    return triangleZoom;
  }

  public void setTriangleZoom(double pTriangleZoom) {
    triangleZoom = pTriangleZoom;
  }

  public boolean isWithColoredTransforms() {
    return withColoredTransforms;
  }

  public void setWithColoredTransforms(boolean pWithColoredTransforms) {
    withColoredTransforms = pWithColoredTransforms;
  }

  public void setMouseDragOperation(MouseDragOperation pMouseDragOperation) {
    mouseDragOperation = pMouseDragOperation;
    selectedPoint = 1;
  }

  public MouseDragOperation getMouseDragOperation() {
    return mouseDragOperation;
  }

  public int getSelectedPoint() {
    return selectedPoint;
  }

  public void setSelectedPoint(int pSelectedPoint) {
    selectedPoint = pSelectedPoint;
  }

  public boolean isNoControls() {
    return noControls;
  }

  public void setNoControls(boolean pNoControls) {
    noControls = pNoControls;
  }

  public boolean isProgressivePreview() {
    return progressivePreview;
  }

  public void setProgressivePreview(boolean pProgressivePreview) {
    progressivePreview = pProgressivePreview;
  }

}
