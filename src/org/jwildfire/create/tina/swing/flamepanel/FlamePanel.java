/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.Stereo3dPreview;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.RandomGeneratorFactory;
import org.jwildfire.create.tina.random.RandomGeneratorType;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.swing.*;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.transform.BalancingTransformer;

public class FlamePanel extends ImagePanel {
  private final static int BORDER = 20;
  private static final double DFLT_VIEWSIZE = 5.0;
  private static final Color XFORM_POST_COLOR = new Color(255, 219, 160);
  private static final Color BACKGROUND_COLOR = new Color(60, 60, 60);
  private static final Color VARIATION_COLOR = new Color(245, 205, 16);
  private static final Color SHADOW_COLOR = new Color(32, 32, 32);

  private static BasicStroke GRID_LINE = new BasicStroke(1.0f);
  private static BasicStroke GRID_LINE_ZERO = new BasicStroke(1.6f);
  private static final Color XFORM_GRID_COLOR = new Color(140, 140, 120);
  private static final Color XFORM_GRID_COLOR_ZERO = new Color(255, 255, 160);

  private static final long serialVersionUID = 1L;
  private FlameHolder flameHolder;
  private LayerHolder layerHolder;
  private final FrameControlsUtil frameControlsUtil;

  private FlamePanelControlStyle flamePanelTriangleMode = FlamePanelControlStyle.TRIANGLE;
  private boolean withImage = true;
  private int imageBrightness = 100;
  private boolean withTriangles = true;
  private boolean withVariations = false;
  private boolean withShowTransparency = false;
  private boolean withGrid = false;
  private boolean withGuides = false;
  private boolean fineMovement = false;
  private XForm selectedXForm = null;
  private final XForm rotateXForm = new XForm();
  private boolean allowScaleX = true;
  private boolean allowScaleY = true;

  private int xBeginDrag, yBeginDrag;
  private int xMouseClickPosition, yMouseClickPosition;

  private int renderWidth;
  private int renderHeight;
  private double renderAspect = 1.0;

  private boolean redrawAfterMouseClick;
  private boolean reRender;
  private UndoManagerHolder<Flame> undoManagerHolder;
  private GradientOverlay gradientOverlay = new GradientOverlay(this);
  private final Prefs prefs;

  private double viewSizeIncrease = 0.0;
  private double viewOffsetX = 0.0;
  private double viewOffsetY = 0.0;

  private final FlamePanelConfig config = new FlamePanelConfig();

  private BasicStroke strokeGuides;
  private Color colorGuideCenter;
  private Color colorGuideThirds;
  private Color colorGuideGoldenRatio;

  public FlamePanel(Prefs pPrefs, SimpleImage pSimpleImage, int pX, int pY, int pWidth, FlameHolder pFlameHolder, LayerHolder pLayerHolder, FrameControlsUtil pFrameControlsUtil) {
    super(pSimpleImage, pX, pY, pWidth);
    prefs = pPrefs;
    frameControlsUtil = pFrameControlsUtil;
    initPropertiesFromPrefs();
    flameHolder = pFlameHolder;
    layerHolder = pLayerHolder;
  }

  private void initPropertiesFromPrefs() {
    strokeGuides = new BasicStroke((float) prefs.getTinaEditorGuidesLineWidth());
    colorGuideCenter = prefs.getTinaEditorGuidesCenterPointColor();
    colorGuideThirds = prefs.getTinaEditorGuidesRuleOfThirdsColor();
    colorGuideGoldenRatio = prefs.getTinaEditorGuidesGoldenRatioColor();
  }

  @Override
  public void paintComponent(Graphics g) {
    if (config.isNoControls()) {
      if (withImage) {
        drawImage(g);
      }
      return;
    }
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    if (prefs.isTinaEditorControlsWithAntialiasing()) {
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
    fillBackground(g);
    initTriangleView(g2d);
    if (withImage) {
      drawImage(g);
    }
    if (withVariations) {
      drawXForms(g2d);
    }
    if (withGrid) {
      drawGrid(g2d);
    }
    if (withGuides) {
      drawGuides(g2d);
    }
    if (withTriangles) {
      drawTriangles(g2d);
    }
    if (config.getMouseDragOperation() == MouseDragOperation.GRADIENT && flameHolder.getFlame() != null && layerHolder != null) {
      gradientOverlay.paintGradient((Graphics2D) g, layerHolder.getLayer().getPalette(), this.getParentImageBounds());
    }
  }

  private void fillBackground(Graphics g) {
    Rectangle bounds = this.getBounds();
    if (withShowTransparency) {
      SimpleImage bgImg = getTransparencyImg(bounds.width, bounds.height);
      g.drawImage(bgImg.getBufferedImg(), 0, 0, bounds.width, bounds.height, this);
    }
    else {
      g.setColor(BACKGROUND_COLOR);
      g.fillRect(0, 0, bounds.width, bounds.height);
    }
  }

  private SimpleImage _transpBGImg = null;

  private SimpleImage getTransparencyImg(int pWidth, int pHeight) {
    if (_transpBGImg != null && (_transpBGImg.getImageWidth() != pWidth || _transpBGImg.getImageHeight() != pHeight)) {
      _transpBGImg = null;
    }
    if (_transpBGImg == null) {
      _transpBGImg = new SimpleImage(pWidth, pHeight);
      final int SQUARE_SIZE = 8;
      final int BRIGHT_COLOR = 250;
      final int DARK_COLOR = 200;
      for (int i = 0; i < pHeight; i++) {
        for (int j = 0; j < pWidth; j++) {
          boolean hAlt = (i / SQUARE_SIZE) % 2 == 0;
          boolean wAlt = (j / SQUARE_SIZE) % 2 == 0;
          boolean alt = wAlt ^ hAlt;
          int color = alt ? BRIGHT_COLOR : DARK_COLOR;
          _transpBGImg.setARGB(j, i, 255, color, color, color);
        }
      }
    }
    return _transpBGImg;
  }

  public Rectangle getImageBounds() {
    Rectangle bounds = this.getBounds();
    if (!config.isProgressivePreview() && flameHolder.getFlame() != null && flameHolder.getFlame().getStereo3dMode() != Stereo3dMode.NONE && 
    		flameHolder.getFlame().getStereo3dPreview() == Stereo3dPreview.SIDE_BY_SIDE_FULL) {
    	return bounds;   	
    }
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
    return new Rectangle(0, 0, imageWidth, imageHeight);
  }

  public Rectangle getParentImageBounds() {
    Rectangle bounds = this.getParent().getBounds();
    if (!config.isProgressivePreview() && flameHolder.getFlame() != null && flameHolder.getFlame().getStereo3dMode() != Stereo3dMode.NONE && 
    		flameHolder.getFlame().getStereo3dPreview() == Stereo3dPreview.SIDE_BY_SIDE_FULL) {
    	return bounds;   	
    }
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
    return new Rectangle(0, 0, imageWidth, imageHeight);
  }

  private int viewAreaLeft, viewAreaRight, viewAreaTop, viewAreaBottom;

  private void initTriangleView(Graphics2D g) {
    Rectangle bounds = this.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;

    viewAreaLeft = BORDER;
    viewAreaRight = width - 1 - BORDER;
    viewAreaTop = BORDER;
    viewAreaBottom = height - 1 - BORDER;

    double viewSize = DFLT_VIEWSIZE + viewSizeIncrease;

    config.setTriangleViewXScale((double) (width - 2 * BORDER) / viewSize);
    config.setTriangleViewYScale(-(double) (height - 2 * BORDER) / viewSize);

    if (fabs(config.getTriangleViewXScale()) < fabs(config.getTriangleViewYScale())) {
      config.setTriangleViewYScale(-config.getTriangleViewXScale());
    }
    else {
      config.setTriangleViewXScale(-config.getTriangleViewYScale());
    }

    double xsize = (width - 2 * BORDER) / config.getTriangleViewXScale();
    double ysize = (height - 2 * BORDER) / config.getTriangleViewXScale();
    double viewXMin = -xsize / 2.0 + viewOffsetX;
    double viewYMin = -ysize / 2.0 + viewOffsetY;

    config.setTriangleViewXTrans(viewXMin * config.getTriangleViewXScale() - viewAreaLeft);
    config.setTriangleViewYTrans(viewYMin * config.getTriangleViewYScale() - viewAreaBottom);
  }

  private class TickSpec {
    private final double tmin;
    private final double tmax;
    private final double tstep;

    public TickSpec(double pMin, double pMax, int pMaxCount) {
      //      double rawTickStep = (pMax - pMin) / (pMaxCount - 1);
      //      BigDecimal bd = new BigDecimal(rawTickStep);
      //      BigDecimal rounded = bd.setScale(1, BigDecimal.ROUND_HALF_EVEN);
      //      tstep = rounded.doubleValue();

      // for now we use fixed tickstep, so that the user always knows which distance holds for two lines, at whatever zoom-level
      tstep = prefs.getTinaEditorGridSize() > EPSILON ? prefs.getTinaEditorGridSize() : 0.5;

      if (pMin < 0) {
        int n = (int) Math.round(fabs(pMin / tstep));
        tmin = -n * tstep;
      }
      else if (pMin > 0) {
        int n = (int) Math.round(pMin / tstep);
        tmin = n * tstep;
      }
      else {
        tmin = tstep;
      }

      if (pMax < 0) {
        int n = (int) Math.round(fabs(pMax / tstep)) + 1;
        tmax = -n * tstep;
      }
      else if (pMax > 0) {
        int n = (int) Math.round((pMax / tstep)) + 1;
        tmax = n * tstep;
      }
      else {
        tmax = tstep;
      }
    }

    public double getTmin() {
      return tmin;
    }

    public double getTmax() {
      return tmax;
    }

    public double getTstep() {
      return tstep;
    }

  }

  // guides known from Apo
  private void drawGuides(Graphics2D g) {
    Rectangle bounds = this.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    int position;

    g.setStroke(strokeGuides);
    g.setColor(colorGuideCenter);
    position = (int) ((double) height / 2.0 + 0.5);
    g.drawLine(0, position, width - 1, position);
    position = (int) ((double) width / 2.0 + 0.5);
    g.drawLine(position, 0, position, height - 1);

    g.setColor(colorGuideThirds);
    position = (int) ((double) height / 3.0 + 0.5);
    g.drawLine(0, position, width - 1, position);
    g.drawLine(0, height - position - 1, width - 1, height - position - 1);
    position = (int) ((double) width / 3.0 + 0.5);
    g.drawLine(position, 0, position, height - 1);
    g.drawLine(width - position - 1, 0, width - position - 1, height);

    g.setColor(colorGuideGoldenRatio);

    position = (int) ((double) height * 0.61803399 + 0.5);
    g.drawLine(0, position, width, position);
    g.drawLine(0, height - position - 1, width - 1, height - position - 1);
    position = (int) ((double) width * 0.61803399 + 0.5);
    g.drawLine(position, 0, position, height - 1);
    g.drawLine(width - position - 1, 0, width - position - 1, height);
  }

  private void drawGrid(Graphics2D g) {
    double xmin = triangleViewToX(viewAreaLeft);
    double xmax = triangleViewToX(viewAreaRight);

    double ymin = triangleViewToY(viewAreaBottom);
    double ymax = triangleViewToY(viewAreaTop);

    int desiredTickCount = 16;

    TickSpec xTicks = new TickSpec(xmin, xmax, desiredTickCount);
    TickSpec yTicks = new TickSpec(ymin, ymax, (int) Math.round(desiredTickCount / renderAspect));

    Rectangle bounds = this.getImageBounds();

    {
      double x = xTicks.getTmin();
      while (x + EPSILON <= xTicks.getTmax()) {
        int vx = triangleXToView(x);
        if (fabs(x) < EPSILON) {
          g.setStroke(GRID_LINE_ZERO);
          g.setColor(XFORM_GRID_COLOR_ZERO);
        }
        else {
          g.setStroke(GRID_LINE);
          g.setColor(XFORM_GRID_COLOR);
        }
        g.drawLine(vx, bounds.y, vx, bounds.y + bounds.height - 1);
        x += xTicks.getTstep();
      }
    }
    {
      double y = yTicks.getTmin();
      while (y + EPSILON <= yTicks.getTmax()) {
        int vy = triangleYToView(y);
        if (fabs(y) < EPSILON) {
          g.setColor(XFORM_GRID_COLOR_ZERO);
        }
        else {
          g.setColor(XFORM_GRID_COLOR);
        }
        g.drawLine(bounds.x, vy, bounds.x + bounds.width - 1, vy);
        y += yTicks.getTstep();
      }
    }
  }

  private void drawTriangles(Graphics2D g) {
    if (layerHolder != null) {
      Layer layer = layerHolder.getLayer();
      if (layer != null) {
        if (!prefs.isTinaEditorControlsWithShadows()) {
          g.setColor(config.isEditPostTransform() ? XFORM_POST_COLOR : FlamePanelConfig.XFORM_COLOR);
        }

        // draw the selected one at last
        for (int pass = 0; pass < 2; pass++) {
          for (int i = 0; i < layer.getXForms().size(); i++) {
            XForm xForm = layer.getXForms().get(i);
            if ((pass == 0 && (selectedXForm == null || xForm != selectedXForm)) || (pass == 1 && xForm == selectedXForm)) {
              boolean isSelected = xForm == selectedXForm;
              if (prefs.isTinaEditorControlsWithShadows()) {
                g.setColor(SHADOW_COLOR);
                drawXForm(g, xForm, i, layer.getXForms().size(), false, true, isSelected);
                drawXForm(g, xForm, i, layer.getXForms().size(), false, false, isSelected);
              }
              else {
                drawXForm(g, xForm, i, layer.getXForms().size(), false, false, isSelected);
              }
            }
          }
          for (int i = 0; i < layer.getFinalXForms().size(); i++) {
            XForm xForm = layer.getFinalXForms().get(i);
            if ((pass == 0 && (selectedXForm == null || xForm != selectedXForm)) || (pass == 1 && xForm == selectedXForm)) {
              boolean isSelected = xForm == selectedXForm;
              if (prefs.isTinaEditorControlsWithShadows()) {
                g.setColor(SHADOW_COLOR);
                drawXForm(g, xForm, i, layer.getXForms().size(), true, true, isSelected);
                drawXForm(g, xForm, i, layer.getXForms().size(), true, false, isSelected);
              }
              else {
                drawXForm(g, xForm, i, layer.getXForms().size(), true, false, isSelected);
              }
            }
          }
          if (selectedXForm == null) {
            break;
          }
        }
      }
    }
  }

  private void drawXForms(Graphics2D g) {
    if (selectedXForm != null && selectedXForm.getVariationCount() > 0) {
      try {
        selectedXForm.initTransform();
        for (Variation var : selectedXForm.getVariations()) {
          var.getFunc().initOnce(getFlameTransformationContext(), layerHolder != null ? layerHolder.getLayer() : null, selectedXForm, var.getAmount());
          var.getFunc().init(getFlameTransformationContext(), layerHolder != null ? layerHolder.getLayer() : null, selectedXForm, var.getAmount());
        }

        double xMin = -1.0;
        double xMax = 1.0;
        int xSteps = 32;
        double yMin = -1.0;
        double yMax = 1.0;
        int ySteps = 32;
        //
        double xs = (xMax - xMin) / (double) (xSteps - 1.0);
        double ys = (yMax - yMin) / (double) (ySteps - 1.0);
        int xx[][] = new int[ySteps][xSteps];
        int yy[][] = new int[ySteps][xSteps];
        {
          double y = yMin;
          XYZPoint affineT = new XYZPoint(); // affine part of the transformation
          XYZPoint varT = new XYZPoint(); // complete transformation
          XYZPoint p = new XYZPoint();
          for (int i = 0; i < ySteps; i++) {
            double x = xMin;
            for (int j = 0; j < xSteps; j++) {
              p.x = x;
              p.y = y;

              p.z = 0.0;
              selectedXForm.transformPoint(getFlameTransformationContext(), affineT, varT, p, p);
              xx[i][j] = triangleXToView(p.x);
              yy[i][j] = triangleYToView(-p.y);
              x += xs;
            }
            y += ys;
          }
        }

        g.setColor(VARIATION_COLOR);
        g.setStroke(GRID_LINE);

        for (int y = 0; y < ySteps - 1; y++) {
          for (int x = 0; x < xSteps - 1; x++) {
            g.drawLine(xx[y][x], yy[y][x], xx[y][x + 1], yy[y][x + 1]);
            g.drawLine(xx[y][x], yy[y][x], xx[y + 1][x], yy[y + 1][x]);
            if (x == xSteps - 2) {
              g.drawLine(xx[y][x + 1], yy[y][x + 1], xx[y + 1][x + 1], yy[y + 1][x + 1]);
            }
            if (y == ySteps - 2) {
              g.drawLine(xx[y + 1][x], yy[y + 1][x], xx[y + 1][x + 1], yy[y + 1][x + 1]);
            }
          }
        }

      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private Map<FlamePanelControlStyle, AbstractControlHandler<?>> controlHandlerMap = new HashMap<FlamePanelControlStyle, AbstractControlHandler<?>>();

  private AbstractControlHandler<?> getHandler(FlamePanelControlStyle pShape) {
    AbstractControlHandler<?> res = controlHandlerMap.get(pShape);
    if (res == null) {
      res = pShape.createHandlerInstance(frameControlsUtil, prefs, config);
      controlHandlerMap.put(pShape, res);
    }
    return res;
  }

  private void drawXForm(Graphics2D g, XForm pXForm, int pIndex, int pXFormCount, boolean pIsFinal, boolean pShadow, boolean pIsSelected) {
    getHandler(flamePanelTriangleMode).drawXForm(g, pXForm, pIndex, pXFormCount, pIsFinal, pShadow, pIsSelected);
  }

  private int triangleXToView(double pX) {
    return Tools.FTOI(config.getTriangleViewXScale() * config.getTriangleZoom() * pX - config.getTriangleViewXTrans());
  }

  private int triangleYToView(double pY) {
    return Tools.FTOI(config.getTriangleViewYScale() * config.getTriangleZoom() * pY - config.getTriangleViewYTrans());
  }

  private double triangleViewToX(int viewX) {
    return ((double) viewX + config.getTriangleViewXTrans()) / (config.getTriangleViewXScale() * config.getTriangleZoom());
  }

  private double triangleViewToY(int viewY) {
    return ((double) viewY + config.getTriangleViewYTrans()) / (config.getTriangleViewYScale() * config.getTriangleZoom());
  }

  public void setDrawImage(boolean drawImage) {
    this.withImage = drawImage;
  }

  public void setDrawTriangles(boolean drawTriangles) {
    this.withTriangles = drawTriangles;
  }

  public void setSelectedXForm(XForm selectedXForm) {
    this.selectedXForm = selectedXForm;
  }

  public boolean mouseDragged(int pX, int pY, boolean pLeftButton, boolean pRightButton, boolean pMiddleButton) {
    reRender = true;
    int viewDX = pX - xBeginDrag;
    int viewDY = pY - yBeginDrag;
    if (viewDX != 0 || viewDY != 0) {
      double dx = triangleViewToX(pX) - triangleViewToX(xBeginDrag);
      double dy = triangleViewToY(pY) - triangleViewToY(yBeginDrag);
      xBeginDrag = pX;
      yBeginDrag = pY;
      if (Math.abs(dx) > MathLib.EPSILON || Math.abs(dy) > MathLib.EPSILON) {
        switch (config.getMouseDragOperation()) {
          case TRIANGLE_VIEW: {
            dx *= 0.75;
            dy *= 0.75;
            if (fineMovement) {
              dx *= 0.25;
              dy *= 0.25;
            }
            if (pRightButton || pMiddleButton) {
              // zoom 
              viewSizeIncrease -= dx;
            }
            else {
              // move
              viewOffsetX -= dx;
              viewOffsetY -= dy;
            }
            return true;
          }
          case MOVE_TRIANGLE: {
            if (selectedXForm == null) {
              return false;
            }
            // Move
            if (pLeftButton && !pRightButton) {
              if (fineMovement) {
                dx *= 0.05;
                dy *= 0.05;
              }
              // move
              frameControlsUtil.setAffineProperty(selectedXForm, "20", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "20", config.isEditPostTransform()) + dx);
              frameControlsUtil.setAffineProperty(selectedXForm, "21", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "21", config.isEditPostTransform()) - dy);
              return true;
            }
            // rotate
            else if (pRightButton) {
              if (fineMovement) {
                dx *= 0.05;
              }
              else {
                dx *= 0.5;
                dy *= 0.5;
              }
              rotateXForm(dx * 30);
              return true;
            }
            // zoom
            else if (pMiddleButton && !pLeftButton && !pRightButton) {
              if (fineMovement) {
                dx *= 0.05;
                dy *= 0.05;
              }
              else {
                dx *= 0.25;
                dy *= 0.25;
              }
              TriangleControlShape triangle = new TriangleControlShape(frameControlsUtil, config, selectedXForm);
              double v1x = triangle.x[0] - triangle.x[1];
              double v1y = triangle.y[0] - triangle.y[1];
              double v2x = v1x + dx;
              double v2y = v1y + dy;
              double dr1 = Math.sqrt(v1x * v1x + v1y * v1y);
              double dr2 = Math.sqrt(v2x * v2x + v2y * v2y);
              double scale = dr2 / dr1;
              if (allowScaleX) {
                frameControlsUtil.setAffineProperty(selectedXForm, "00", config.isEditPostTransform(), frameControlsUtil.getAffineProperty(selectedXForm, "00", config.isEditPostTransform()) * scale);
                frameControlsUtil.setAffineProperty(selectedXForm, "01", config.isEditPostTransform(), frameControlsUtil.getAffineProperty(selectedXForm, "01", config.isEditPostTransform()) * scale);
              }
              if (allowScaleY) {
                frameControlsUtil.setAffineProperty(selectedXForm, "10", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "10", config.isEditPostTransform()) * scale);
                frameControlsUtil.setAffineProperty(selectedXForm, "11", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "11", config.isEditPostTransform()) * scale);
              }
              return true;
            }
          }
          case ROTATE_TRIANGLE: {
            if (selectedXForm == null) {
              return false;
            }
            if (fineMovement) {
              dx *= 0.05;
            }
            else {
              dx *= 0.5;
            }
            rotateXForm(dx * 30);
            return true;
          }
          case SCALE_TRIANGLE: {
            if (selectedXForm == null) {
              return false;
            }
            if (fineMovement) {
              dx *= 0.05;
              dy *= 0.05;
            }
            else {
              dx *= 0.25;
              dy *= 0.25;
            }
            TriangleControlShape triangle = new TriangleControlShape(frameControlsUtil, config, selectedXForm);
            double v1x = triangle.x[0] - triangle.x[1];
            double v1y = triangle.y[0] - triangle.y[1];
            double v2x = v1x + dx;
            double v2y = v1y + dy;
            double dr1 = Math.sqrt(v1x * v1x + v1y * v1y);
            double dr2 = Math.sqrt(v2x * v2x + v2y * v2y);
            double scale = dr2 / dr1;
            if (allowScaleX) {
              frameControlsUtil.setAffineProperty(selectedXForm, "00", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "00", config.isEditPostTransform()) * scale);
              frameControlsUtil.setAffineProperty(selectedXForm, "01", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "01", config.isEditPostTransform()) * scale);
            }
            if (allowScaleY) {
              frameControlsUtil.setAffineProperty(selectedXForm, "10", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "10", config.isEditPostTransform()) * scale);
              frameControlsUtil.setAffineProperty(selectedXForm, "11", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "11", config.isEditPostTransform()) * scale);
            }
          return true;
          }
          case POINTS: {
            if (selectedXForm == null) {
              return false;
            }
            if (fineMovement) {
              dx *= 0.05;
              dy *= 0.05;
            }
            switch (config.getSelectedPoint()) {
              case 0:
                frameControlsUtil.setAffineProperty(selectedXForm, "00", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "00", config.isEditPostTransform()) + dx);
                frameControlsUtil.setAffineProperty(selectedXForm, "01", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "01", config.isEditPostTransform()) - dy);
                break;
              case 1:
                frameControlsUtil.setAffineProperty(selectedXForm, "20", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "20", config.isEditPostTransform()) + dx);
                frameControlsUtil.setAffineProperty(selectedXForm, "21", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "21", config.isEditPostTransform()) - dy);

                frameControlsUtil.setAffineProperty(selectedXForm, "10", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "10", config.isEditPostTransform()) + dx);
                frameControlsUtil.setAffineProperty(selectedXForm, "11", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "11", config.isEditPostTransform()) - dy);

                frameControlsUtil.setAffineProperty(selectedXForm, "00", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "00", config.isEditPostTransform()) - dx);
                frameControlsUtil.setAffineProperty(selectedXForm, "01", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "01", config.isEditPostTransform()) + dy);
                break;
              case 2:
                frameControlsUtil.setAffineProperty(selectedXForm, "10", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "10", config.isEditPostTransform()) - dx);
                frameControlsUtil.setAffineProperty(selectedXForm, "11", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "11", config.isEditPostTransform()) + dy);
                break;
              default: // nothing to do
                break;
            }
            return true;
          }
          case VIEW: {
            if (flameHolder != null && flameHolder.getFlame() != null) {
              Flame flame = flameHolder.getFlame();
              // Move
              if (pLeftButton && !pRightButton) {
                double rcX, rcY;
                if (flame.is3dProjectionRequired()) {
                  rcX = dx;
                  rcY = dy;
                }
                else {
                  double camRoll = frameControlsUtil.getEvaluatedPropertyValue(flame, "camRoll");
                  double cosa = cos(M_PI * camRoll / 180.0);
                  double sina = sin(M_PI * camRoll / 180.0);
                  rcX = dx * cosa - dy * sina;
                  rcY = dy * cosa + dx * sina;
                }
                if (fineMovement) {
                  rcX *= 0.1;
                  rcY *= 0.1;
                }
                frameControlsUtil.applyValueChange(flame, "centreX", frameControlsUtil.getEvaluatedPropertyValue(flame, "centreX") - rcX * 0.3);
                frameControlsUtil.applyValueChange(flame, "centreY", frameControlsUtil.getEvaluatedPropertyValue(flame, "centreY") + rcY * 0.3);
                return true;
              }
              // rotate
              else if (pRightButton) {
                if (fineMovement) {
                  dx *= 0.1;
                  dy *= 0.1;
                }
                if (pLeftButton) {
                  frameControlsUtil.applyValueChange(flame, "camYaw", frameControlsUtil.getEvaluatedPropertyValue(flame, "camYaw") + 12 * dx);
                }
                else {
                  frameControlsUtil.applyValueChange(flame, "camRoll", frameControlsUtil.getEvaluatedPropertyValue(flame, "camRoll") - 12 * dx);
                }
                return true;
              }
              // zoom
              else if (pMiddleButton && !pLeftButton && !pRightButton) {
                if (fineMovement) {
                  dx *= 0.1;
                  dy *= 0.1;
                }
                frameControlsUtil.applyValueChange(flame, "pixelsPerUnit", frameControlsUtil.getEvaluatedPropertyValue(flame, "pixelsPerUnit") + 2.0 * dx);
                return true;
              }
            }
          }
          case FOCUS: {
            Flame flame = flameHolder.getFlame();
            double rcX = dx;
            double rcY = dy;
            if (fineMovement) {
              rcX *= 0.1;
              rcY *= 0.1;
            }
            frameControlsUtil.applyValueChange(flame, "focusX", frameControlsUtil.getEvaluatedPropertyValue(flame, "focusX") + rcX * 0.5);
            frameControlsUtil.applyValueChange(flame, "focusY", frameControlsUtil.getEvaluatedPropertyValue(flame, "focusY") - rcY * 0.5);
            return true;
          }
          case GRADIENT: {
            Layer layer = layerHolder.getLayer();
            dx = viewDX * 0.25;
            if (fineMovement) {
              dx *= 0.25;
            }
            reRender = gradientOverlay.mouseDragged(dx, xBeginDrag, yBeginDrag, layer);
            return true;
          }
          default: // nothing to do
            break;
        }
      }
    }
    return false;
  }

  private void rotateXForm(double angle) {
    rotateXForm.setOwner(layerHolder.getLayer());
    rotateXForm.setCoeff00(frameControlsUtil.getAffineProperty(selectedXForm, "00", config.isEditPostTransform()));
    rotateXForm.setCoeff01(frameControlsUtil.getAffineProperty(selectedXForm, "01", config.isEditPostTransform()));
    rotateXForm.setCoeff10(frameControlsUtil.getAffineProperty(selectedXForm, "10", config.isEditPostTransform()));
    rotateXForm.setCoeff11(frameControlsUtil.getAffineProperty(selectedXForm, "11", config.isEditPostTransform()));
    rotateXForm.setCoeff20(frameControlsUtil.getAffineProperty(selectedXForm, "20", config.isEditPostTransform()));
    rotateXForm.setCoeff21(frameControlsUtil.getAffineProperty(selectedXForm, "21", config.isEditPostTransform()));
    XFormTransformService.rotate(rotateXForm, angle, false);
    frameControlsUtil.setAffineProperty(selectedXForm, "00", config.isEditPostTransform(), rotateXForm.getCoeff00());
    frameControlsUtil.setAffineProperty(selectedXForm, "01", config.isEditPostTransform(), rotateXForm.getCoeff01());
    frameControlsUtil.setAffineProperty(selectedXForm, "10", config.isEditPostTransform(), rotateXForm.getCoeff10());
    frameControlsUtil.setAffineProperty(selectedXForm, "11", config.isEditPostTransform(), rotateXForm.getCoeff11());
    frameControlsUtil.setAffineProperty(selectedXForm, "20", config.isEditPostTransform(), rotateXForm.getCoeff20());
    frameControlsUtil.setAffineProperty(selectedXForm, "21", config.isEditPostTransform(), rotateXForm.getCoeff21());
  }

  public void mousePressed(int x, int y) {
    if (selectedXForm != null || config.getMouseDragOperation() == MouseDragOperation.VIEW || config.getMouseDragOperation() == MouseDragOperation.GRADIENT || config.getMouseDragOperation() == MouseDragOperation.FOCUS || config.getMouseDragOperation() == MouseDragOperation.TRIANGLE_VIEW) {
      xMouseClickPosition = xBeginDrag = x;
      yMouseClickPosition = yBeginDrag = y;
      gradientOverlay.beginDrag(xMouseClickPosition, yMouseClickPosition);
      if (undoManagerHolder != null) {
        undoManagerHolder.saveUndoPoint();
      }
    }
  }

  public XForm mouseClicked(int x, int y) {
    redrawAfterMouseClick = false;
    // select flame
    if (config.getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE || config.getMouseDragOperation() == MouseDragOperation.ROTATE_TRIANGLE || config.getMouseDragOperation() == MouseDragOperation.SCALE_TRIANGLE) {
      Layer layer = layerHolder.getLayer();
      if (layer != null) {
        for (XForm xForm : layer.getXForms()) {
          if (getHandler(flamePanelTriangleMode).isInsideXForm(xForm, x, y)) {
            if (config.getMouseDragOperation() == MouseDragOperation.POINTS) {
              config.setSelectedPoint(getHandler(flamePanelTriangleMode).selectNearestPoint(xForm, x, y));
              redrawAfterMouseClick = true;
              reRender = true;
            }
            return xForm;
          }
        }
        for (XForm xForm : layer.getFinalXForms()) {
          if (getHandler(flamePanelTriangleMode).isInsideXForm(xForm, x, y)) {
            if (config.getMouseDragOperation() == MouseDragOperation.POINTS) {
              config.setSelectedPoint(getHandler(flamePanelTriangleMode).selectNearestPoint(xForm, x, y));
              redrawAfterMouseClick = true;
              reRender = true;
            }
            return xForm;
          }
        }
      }
    }
    // select nearest point
    else if (config.getMouseDragOperation() == MouseDragOperation.POINTS && selectedXForm != null) {
      config.setSelectedPoint(getHandler(flamePanelTriangleMode).selectNearestPoint(selectedXForm, x, y));
      redrawAfterMouseClick = true;
      reRender = true;
      return selectedXForm;
    }
    else if (config.getMouseDragOperation() == MouseDragOperation.GRADIENT && flameHolder.getFlame() != null && layerHolder != null && layerHolder.getLayer() != null) {
      if (undoManagerHolder != null) {
        undoManagerHolder.saveUndoPoint();
      }
      redrawAfterMouseClick = gradientOverlay.mouseClicked(x, y, layerHolder.getLayer().getPalette());
      reRender = false;
    }
    return null;
  }

  public void setEditPostTransform(boolean pEditPostTransform) {
    config.setEditPostTransform(pEditPostTransform);
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

  public void setRenderHeight(int pRenderHeight) {
    renderHeight = pRenderHeight;
    updateRenderAspect();
  }

  public void setFineMovement(boolean pFineMovement) {
    fineMovement = pFineMovement;
  }

  public void setAllowScaleX(boolean allowScaleX) {
    this.allowScaleX = allowScaleX;
  }

  public void setAllowScaleY(boolean allowScaleY) {
    this.allowScaleY = allowScaleY;
  }

  public XForm getSelectedXForm() {
    return selectedXForm;
  }

  public void setDrawVariations(boolean drawVariations) {
    this.withVariations = drawVariations;
  }

  private FlameRenderer flameRenderer = null;

  private FlameRenderer getFlameRenderer() {
    if (flameRenderer == null) {
      Flame flame = flameHolder.getFlame();
      if (flame == null) {
        throw new IllegalStateException();
      }
      flameRenderer = new FlameRenderer(flame, prefs, withShowTransparency, false);
    }
    return flameRenderer;
  }

  private FlameTransformationContext flameTransformationContext = null;

  private FlameTransformationContext getFlameTransformationContext() {
    if (flameTransformationContext == null) {
      flameTransformationContext = new FlameTransformationContext(getFlameRenderer(), RandomGeneratorFactory.getInstance(prefs, RandomGeneratorType.getDefaultValue()), 0,1);
      flameTransformationContext.setPreview(getFlameRenderer().isPreview());
    }
    return flameTransformationContext;
  }

  public boolean isRedrawAfterMouseClick() {
    return redrawAfterMouseClick;
  }

  public void setUndoManagerHolder(UndoManagerHolder<Flame> undoManagerHolder) {
    this.undoManagerHolder = undoManagerHolder;
  }

  public void setShowTransparency(boolean pShowTransparency) {
    withShowTransparency = pShowTransparency;
  }

  public boolean mouseWheelMoved(int pRotateAmount) {
    if (pRotateAmount != 0) {
      if (config.getMouseDragOperation() == MouseDragOperation.VIEW && flameHolder != null && flameHolder.getFlame() != null) {
        Flame flame = flameHolder.getFlame();
        double dx = pRotateAmount * 3.0;
        if (fineMovement) {
          dx *= 0.05;
        }
        else {
          dx *= 0.25;
        }
        frameControlsUtil.applyValueChange(flame, "pixelsPerUnit", frameControlsUtil.getEvaluatedPropertyValue(flame, "pixelsPerUnit") - dx);
        return true;
      }
      if (config.getMouseDragOperation() == MouseDragOperation.TRIANGLE_VIEW && flameHolder != null && flameHolder.getFlame() != null) {
        double dx = pRotateAmount * 0.3;
        if (fineMovement) {
          dx *= 0.05;
        }
        else {
          dx *= 0.25;
        }
        viewSizeIncrease -= dx;
        return true;
      }
      else if (config.getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE && selectedXForm != null) {
        double dx = MathLib.fabs(pRotateAmount * 0.1);
        double dy = dx;
        if (fineMovement) {
          dx *= 0.05;
          dy *= 0.05;
        }
        else {
          dx *= 0.25;
          dy *= 0.25;
        }
        TriangleControlShape triangle = new TriangleControlShape(frameControlsUtil, config, selectedXForm);
        double v1x = triangle.x[0] - triangle.x[1];
        double v1y = triangle.y[0] - triangle.y[1];
        double v2x = v1x + dx;
        double v2y = v1y + dy;
        double dr1 = Math.sqrt(v1x * v1x + v1y * v1y);
        double dr2 = Math.sqrt(v2x * v2x + v2y * v2y);
        double scale = pRotateAmount < 0 ? dr2 / dr1 : dr1 / dr2;
        if (allowScaleX) {
          frameControlsUtil.setAffineProperty(selectedXForm, "00", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "00", config.isEditPostTransform()) * scale);
          frameControlsUtil.setAffineProperty(selectedXForm, "01", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "01", config.isEditPostTransform()) * scale);
        }
        if (allowScaleY) {
          frameControlsUtil.setAffineProperty(selectedXForm, "10", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "10", config.isEditPostTransform()) * scale);
          frameControlsUtil.setAffineProperty(selectedXForm, "11", config.isEditPostTransform(),frameControlsUtil.getAffineProperty(selectedXForm, "11", config.isEditPostTransform()) * scale);
        }
        return true;
      }
      else if (config.getMouseDragOperation() == MouseDragOperation.FOCUS && flameHolder != null && flameHolder.getFlame() != null) {
        double dz = -pRotateAmount * 0.1;
        if (fineMovement) {
          dz *= 0.05;
        }
        else {
          dz *= 0.25;
        }
        Flame flame = flameHolder.getFlame();
        frameControlsUtil.applyValueChange(flame, "focusZ", frameControlsUtil.getEvaluatedPropertyValue(flame, "focusZ") + dz);
        return true;
      }
      else if (config.getMouseDragOperation() == MouseDragOperation.GRADIENT && flameHolder != null && flameHolder.getFlame() != null) {
        double dz = -pRotateAmount * 0.1;
        if (fineMovement) {
          dz *= 0.1;
        }
        gradientOverlay.mouseWheelAction(dz);
        return true;
      }
    }
    return false;
  }

  public void setFlameHolder(FlameHolder pFlameHolder) {
    flameHolder = pFlameHolder;
  }

  public void importOptions(FlamePanel pFlamePanel) {
    if (pFlamePanel != null) {
      withImage = pFlamePanel.withImage;
      imageBrightness = pFlamePanel.imageBrightness;
      withTriangles = pFlamePanel.withTriangles;
      flamePanelTriangleMode = pFlamePanel.flamePanelTriangleMode;
      withVariations = pFlamePanel.withVariations;
      withGrid = pFlamePanel.withGrid;
      withGuides = pFlamePanel.withGuides;
      fineMovement = pFlamePanel.fineMovement;
      allowScaleX = pFlamePanel.allowScaleX;
      allowScaleY = pFlamePanel.allowScaleY;

      withShowTransparency = pFlamePanel.withShowTransparency;
      config.setWithColoredTransforms(pFlamePanel.config.isWithColoredTransforms());
      config.setMouseDragOperation(pFlamePanel.config.getMouseDragOperation());
      config.setEditPostTransform(pFlamePanel.config.isEditPostTransform());
      config.setProgressivePreview(pFlamePanel.config.isProgressivePreview());
    }
  }

  public void gradientFade() {
    if (layerHolder.getLayer() != null) {
      gradientOverlay.fadeRange(layerHolder.getLayer().getPalette());
    }
  }

  public void gradientInvert() {
    if (layerHolder.getLayer() != null) {
      gradientOverlay.invertRange(layerHolder.getLayer().getPalette());
    }
  }

  public void gradientReverse() {
    if (layerHolder.getLayer() != null) {
      gradientOverlay.reverseRange(layerHolder.getLayer().getPalette());
    }
  }

  public void gradientSort() {
    if (layerHolder.getLayer() != null) {
      gradientOverlay.sortRange(layerHolder.getLayer().getPalette());
    }
  }

  public void gradientSelectAll() {
    if (flameHolder.getFlame() != null) {
      gradientOverlay.selectAll();
    }
  }

  public void gradientMarker_move(int pIdx, int pDeltaPos) {
    gradientOverlay.gradientMarker_move(pIdx, pDeltaPos);
  }

  public boolean gradientMarker_selectColor(int pIdx) {
    if (layerHolder.getLayer() != null) {
      return gradientOverlay.gradientMarker_selectColor(pIdx, layerHolder.getLayer().getPalette());
    }
    else {
      return false;
    }
  }

  public int getGradientFrom() {
    return gradientOverlay.getFrom();
  }

  public int getGradientTo() {
    return gradientOverlay.getTo();
  }

  public void gradientCopyRange() {
    if (layerHolder.getLayer() != null) {
      gradientOverlay.copyRange(layerHolder.getLayer().getPalette());
    }
  }

  public void gradientPasteRange() {
    if (layerHolder.getLayer() != null) {
      gradientOverlay.pasteRange(layerHolder.getLayer().getPalette());
    }
  }

  public void gradientEraseRange() {
    if (layerHolder.getLayer() != null) {
      gradientOverlay.eraseRange(layerHolder.getLayer().getPalette());
    }
  }

  public void gradientMonochrome() {
    if (layerHolder.getLayer() != null) {
      gradientOverlay.monochrome(layerHolder.getLayer().getPalette());
    }
  }

  public boolean isReRender() {
    return reRender;
  }

  public void gradientFadeAll() {
    if (layerHolder.getLayer() != null) {
      gradientOverlay.fadeAll(layerHolder.getLayer().getPalette());
    }
  }

  public void applyBalancing() {
    if (layerHolder.getLayer() != null) {
      layerHolder.getLayer().getPalette().applyBalancing();
    }
  }

  public void applyTX() {
    if (layerHolder.getLayer() != null) {
      layerHolder.getLayer().getPalette().applyTX();
    }
  }

  @Override
  protected SimpleImage preProcessImage(SimpleImage pSimpleImage) {
    SimpleImage img = super.preProcessImage(pSimpleImage);
    if (imageBrightness < 100 && imageBrightness >= 0) {
      BalancingTransformer bT = new BalancingTransformer();
      bT.setBrightness((int) (-(100 - imageBrightness) * 2.55));
      bT.transformImage(img);
    }
    return img;
  }

  public boolean isWithGrid() {
    return withGrid;
  }

  public void setWithGrid(boolean pWithGrid) {
    withGrid = pWithGrid;
  }

  public boolean isWithGuides() {
    return withGuides;
  }

  public void setWithGuides(boolean pWithGuides) {
    withGuides = pWithGuides;
  }

  public int getImageBrightness() {
    return imageBrightness;
  }

  public void setImageBrightness(int pImageBrightness) {
    imageBrightness = pImageBrightness;
  }

  public void setFlamePanelTriangleMode(FlamePanelControlStyle pFlamePanelTriangleMode) {
    flamePanelTriangleMode = pFlamePanelTriangleMode;
  }

  public FlamePanelConfig getConfig() {
    return config;
  }

  public void resetGridToDefaults() {
    viewSizeIncrease = 0.0;
    viewOffsetX = 0.0;
    viewOffsetY = 0.0;
  }

}
