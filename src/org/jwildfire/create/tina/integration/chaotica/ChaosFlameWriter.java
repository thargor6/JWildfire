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
package org.jwildfire.create.tina.integration.chaotica;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.io.SimpleXMLBuilder;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.transform.Identity3x3;
import org.jwildfire.create.tina.transform.Matrix3x3;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Variation;

public class ChaosFlameWriter {
  private static final String ELEM_AFFINE2 = "affine2";
  private static final String ELEM_BOOL = "bool";
  private static final String ELEM_CAMERA = "camera";
  private static final String ELEM_COLOURING = "colouring";
  private static final String ELEM_CURVE = "curve";
  private static final String ELEM_FLAM3_SHADER = "flam3_shader";
  private static final String ELEM_FLAM3_TRANSFORM = "flam3_transform";
  private static final String ELEM_FLAM3_VARIATION = "flam3_variation";
  private static final String ELEM_IFS = "IFS";
  private static final String ELEM_IMAGING = "imaging";
  private static final String ELEM_INT = "int";
  private static final String ELEM_ITERATOR = "iterator";
  private static final String ELEM_NODE = "node";
  private static final String ELEM_PARAMS = "params";
  private static final String ELEM_REAL = "real";
  private static final String ELEM_STRING = "string";
  private static final String ELEM_TABLE = "table";
  private static final String ELEM_VALUES = "values";
  private static final String ELEM_VEC2 = "vec2";
  private static final String ELEM_VEC2_CURVE = "vec2_curve";
  private static final String ELEM_VEC4 = "vec4";
  private static final String ELEM_WEIGHTS_SELECTOR = "weights_selector";
  private static final String ELEM_WORLD = "world";

  private static final String ATTR_NAME = "name";

  private static final String PROPERTY_ANIM_EXPOSURE_TIME = "anim_exposure_time";
  private static final String PROPERTY_ANIM_EXPOSURE_SHAPE = "anim_exposure_shape";
  private static final String PROPERTY_ANIM_FPS = "anim_fps";
  private static final String PROPERTY_ANIM_LENGTH = "anim_length";
  private static final String PROPERTY_ANTIALIASING_MODE = "antialiasing_mode";
  private static final String PROPERTY_APPLY_BG_BEFORE_CURVES = "apply_bg_before_curves";
  private static final String PROPERTY_BACKGROUND_COLOR = "background_colour";
  private static final String PROPERTY_BASE_WEIGHT = "base_weight";
  private static final String PROPERTY_BLEND_SPEED = "blend_speed";
  private static final String PROPERTY_BRIGHTNESS = "brightness";
  private static final String PROPERTY_FLAM3_CAMERA = "flam3 camera";
  private static final String PROPERTY_FLAM3_GAMMA = "flam3_gamma";
  private static final String PROPERTY_FLAM3_GAMMA_LINEAR_THRESHOLD = "flam3_gamma_linear_threshold";
  private static final String PROPERTY_FLAM3_HIGHTLIGHT_POWER = "flam3_highlight_power";
  private static final String PROPERTY_FLAM3_PALETTE = "flam3_palette";
  private static final String PROPERTY_FLAM3_USE_HIGHLIGHT_POWER = "flam3_use_highlight_power";
  private static final String PROPERTY_FLAM3_VIBRANCY = "flam3_vibrancy";
  private static final String PROPERTY_FORMAT_VERSION = "format_version";
  private static final String PROPERTY_IMAGE_WIDTH = "image_width";
  private static final String PROPERTY_IMAGE_HEIGHT = "image_height";
  private static final String PROPERTY_IMAGE_AA_LEVEL = "image_aa_level";
  private static final String PROPERTY_IMAGE_LAYERS = "image_layers";
  private static final String PROPERTY_IMAGE_QUALITY = "image_quality";
  private static final String PROPERTY_LAYER_WEIGHTS = "layer_weights";
  private static final String PROPERTY_OFFSET = "offset";
  private static final String PROPERTY_OPACITY = "opacity";
  private static final String PROPERTY_PALETTE_INDEX = "palette_index";
  private static final String PROPERTY_POS = "pos";
  private static final String PROPERTY_ROTATE = "rotate";
  private static final String PROPERTY_SENSOR_WIDTH = "sensor_width";
  private static final String PROPERTY_TIME_SCALE = "time_scale";
  private static final String PROPERTY_VARIATION_NAME = "variation_name";
  private static final String PROPERTY_X_AXIS_ANGLE = "x_axis_angle";
  private static final String PROPERTY_X_AXIS_LENGTH = "x_axis_length";
  private static final String PROPERTY_Y_AXIS_ANGLE = "y_axis_angle";
  private static final String PROPERTY_Y_AXIS_LENGTH = "y_axis_length";

  private static final int AA_LEVEL = 2;

  private ChaoticaPluginTranslators translator = new ChaoticaPluginTranslators();
  private final Flame flame;

  public ChaosFlameWriter(Flame pFlame) {
    flame = pFlame;
  }

  public void writeFlame(String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename, getFlameXML());
  }

  public String getFlameXML() throws Exception {
    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    xb.addContent("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    Layer layer = flame.getFirstLayer();
    xb.beginElement(ELEM_WORLD, xb.createAttrList(xb.createAttr(ATTR_NAME, "World")));
    addIntProperty(xb, PROPERTY_FORMAT_VERSION, 2);
    xb.beginElement(ELEM_IFS, xb.createAttrList(xb.createAttr(ATTR_NAME, flame.getName())));
    addImaging(xb, flame);
    addAnimAndCamera(xb, flame);

    xb.beginElement(ELEM_NODE, xb.createAttrList(xb.createAttr(ATTR_NAME, "iterators")));
    IdProvider iteratorIdProvider = new IdProvider();
    IdProvider variatonIdProvider = new IdProvider();
    for (XForm xform : layer.getXForms()) {
      addXForm(xb, layer, xform, false, iteratorIdProvider, variatonIdProvider);
    }
    xb.endElement(ELEM_NODE);
    addGradient(xb, layer);
    xb.endElement(ELEM_IFS);
    xb.endElement(ELEM_WORLD);
    return xb.buildXML();
  }

  private static class IdProvider {
    private int counter;

    public int getNextId() {
      return counter++;
    }
  }

  private void addXForm(SimpleXMLBuilder xb, Layer layer, XForm xform, boolean isFinal, IdProvider iteratorIdProvider, IdProvider variatonIdProvider) {
    int iteratorId = iteratorIdProvider.getNextId();
    xb.beginElement(ELEM_ITERATOR, xb.createAttrList(xb.createAttr(ATTR_NAME, "Iterator " + iteratorId)));
    xb.beginElement(ELEM_FLAM3_TRANSFORM, xb.createAttrList(xb.createAttr(ATTR_NAME, "flam3_xform " + iteratorId)));
    addAffine(xb, xform, false);
    if (xform.isHasXYPostCoeffs()) {
      addAffine(xb, xform, true);
    }
    if (xform.getVariationCount() > 0) {
      xb.beginElement(ELEM_NODE, xb.createAttrList(xb.createAttr(ATTR_NAME, "transforms")));
      for (int i = 0; i < xform.getVariationCount(); i++) {
        Variation var = xform.getVariation(i);
        String varName = var.getFunc().getName();
        String translatedVarName = translator.translateVarName(varName);
        if (translatedVarName != null) {
          xb.beginElement(ELEM_FLAM3_VARIATION, xb.createAttrList(xb.createAttr(ATTR_NAME, "Transform " + variatonIdProvider.getNextId())));
          addStringProperty(xb, PROPERTY_VARIATION_NAME, translatedVarName);
          xb.beginElement(ELEM_PARAMS, xb.createAttrList());
          addRealProperty(xb, translatedVarName, var.getAmount(), var.getAmountCurve());
          for (String name : var.getFunc().getParameterNames()) {
            Object param = var.getFunc().getParameter(name);
            if (param != null) {
              double value = 0.0;
              if (param instanceof Double) {
                value = ((Double) param).doubleValue();
              }
              else if (param instanceof Integer) {
                value = ((Integer) param).intValue();
              }
              String propertyName = translator.translatePropertyName(varName, name);
              if (propertyName != null) {
                addRealProperty(xb, propertyName, value, var.getMotionCurve(name));
              }
            }
          }
          for (String fixedValue : translator.getFixedValueNames(varName)) {
            addRealProperty(xb, translator.translatePropertyName(varName, fixedValue), translator.getFixedValue(varName, fixedValue), null);
          }
          xb.endElement(ELEM_PARAMS);
          xb.endElement(ELEM_FLAM3_VARIATION);
        }
      }
      xb.endElement(ELEM_NODE);
    }
    xb.endElement(ELEM_FLAM3_TRANSFORM);

    xb.beginElement(ELEM_FLAM3_SHADER, xb.createAttrList(xb.createAttr(ATTR_NAME, "flam3 shader")));
    addRealProperty(xb, PROPERTY_PALETTE_INDEX, xform.getColor(), null);
    addRealProperty(xb, PROPERTY_BLEND_SPEED, (1.0 - xform.getColorSymmetry()) * 0.5, null);
    {
      double opacity;
      switch (xform.getDrawMode()) {
        case HIDDEN:
          opacity = 0.0;
          break;
        case OPAQUE:
          opacity = xform.getOpacity();
        default:
          opacity = 1.0;
          break;
      }
      addRealProperty(xb, PROPERTY_OPACITY, opacity, null);
    }
    addTableProperty(xb, PROPERTY_LAYER_WEIGHTS, new Double[] { 1.0, 1.0, 1.0, 1.0 });
    xb.endElement(ELEM_FLAM3_SHADER);
    if (!isFinal) {
      xb.beginElement(ELEM_WEIGHTS_SELECTOR, xb.createAttrList(xb.createAttr(ATTR_NAME, "Iterator selection weights")));
      addRealProperty(xb, PROPERTY_BASE_WEIGHT, xform.getWeight(), null);
      xb.beginElement(ELEM_NODE, xb.createAttrList(xb.createAttr(ATTR_NAME, "per_iterator_weights")));
      for (int i = 0; i < layer.getXForms().size(); i++) {
        addRealProperty(xb, "iter " + i + " weight", xform.getModifiedWeights()[i], null);
      }
      xb.endElement(ELEM_NODE);
      xb.endElement(ELEM_WEIGHTS_SELECTOR);
    }
    addRealProperty(xb, PROPERTY_TIME_SCALE, 1.0, null);
    xb.endElement(ELEM_ITERATOR);
  }

  public class AffineBaseParam {
    protected final double xAxisAngle;
    protected final double xAxisLength;
    protected final double yAxisAngle;
    protected final double yAxisLength;
    protected final double xOffset;
    protected final double yOffset;

    public AffineBaseParam(double xAxisAngle, double xAxisLength, double yAxisAngle, double yAxisLength, double xOffset, double yOffset) {
      this.xAxisAngle = xAxisAngle;
      this.xAxisLength = xAxisLength;
      this.yAxisAngle = yAxisAngle;
      this.yAxisLength = yAxisLength;
      this.xOffset = xOffset;
      this.yOffset = yOffset;
    }

    public double getXAxisAngle() {
      return xAxisAngle;
    }

    public double getXAxisLength() {
      return xAxisLength;
    }

    public double getYAxisAngle() {
      return yAxisAngle;
    }

    public double getYAxisLength() {
      return yAxisLength;
    }

    public double getXOffset() {
      return xOffset;
    }

    public double getYOffset() {
      return yOffset;
    }

  }

  public class AffineParam extends AffineBaseParam {
    private final MotionCurve xAxisAngleCurve;
    private final MotionCurve xAxisLengthCurve;
    private final MotionCurve yAxisAngleCurve;
    private final MotionCurve yAxisLengthCurve;
    private final MotionCurve xOffsetCurve;
    private final MotionCurve yOffsetCurve;

    public AffineParam(
        double xAxisAngle, MotionCurve xAxisAngleCurve, double xAxisLength, MotionCurve xAxisLengthCurve,
        double yAxisAngle, MotionCurve yAxisAngleCurve, double yAxisLength, MotionCurve yAxisLengthCurve,
        double xOffset, MotionCurve xOffsetCurve, double yOffset, MotionCurve yOffsetCurve) {
      super(xAxisAngle, xAxisLength, yAxisAngle, yAxisLength, xOffset, yOffset);
      this.xAxisAngleCurve = xAxisAngleCurve;
      this.xAxisLengthCurve = xAxisLengthCurve;
      this.yAxisAngleCurve = yAxisAngleCurve;
      this.yAxisLengthCurve = yAxisLengthCurve;
      this.xOffsetCurve = xOffsetCurve;
      this.yOffsetCurve = yOffsetCurve;
    }

    public MotionCurve getXAxisAngleCurve() {
      return xAxisAngleCurve;
    }

    public MotionCurve getXAxisLengthCurve() {
      return xAxisLengthCurve;
    }

    public MotionCurve getYAxisAngleCurve() {
      return yAxisAngleCurve;
    }

    public MotionCurve getYAxisLengthCurve() {
      return yAxisLengthCurve;
    }

    public MotionCurve getXOffsetCurve() {
      return xOffsetCurve;
    }

    public MotionCurve getYOffsetCurve() {
      return yOffsetCurve;
    }

  }

  public class AffineParamCalculator {
    private final double coeff[][];
    private final MotionCurve coeffCurve[][];
    private final MotionCurve rotateCurve;
    private final MotionCurve scaleCurve;

    public AffineParamCalculator(XForm xform, boolean isPost) {
      coeff = new double[2][3];
      coeffCurve = new MotionCurve[2][3];
      if (isPost) {
        coeff[0][0] = xform.getXYPostCoeff00();
        coeff[0][1] = xform.getXYPostCoeff01();
        coeff[1][0] = xform.getXYPostCoeff10();
        coeff[1][1] = xform.getXYPostCoeff11();
        coeff[0][2] = xform.getXYPostCoeff20();
        coeff[1][2] = xform.getXYPostCoeff21();
        coeffCurve[0][0] = xform.getXYPostCoeff00Curve();
        coeffCurve[0][1] = xform.getXYPostCoeff01Curve();
        coeffCurve[1][0] = xform.getXYPostCoeff10Curve();
        coeffCurve[1][1] = xform.getXYPostCoeff11Curve();
        coeffCurve[0][2] = xform.getXYPostCoeff20Curve();
        coeffCurve[1][2] = xform.getXYPostCoeff21Curve();
        rotateCurve = xform.getXYPostRotateCurve();
        scaleCurve = xform.getXYPostScaleCurve();
      }
      else {
        coeff[0][0] = xform.getXYCoeff00();
        coeff[0][1] = xform.getXYCoeff01();
        coeff[1][0] = xform.getXYCoeff10();
        coeff[1][1] = xform.getXYCoeff11();
        coeff[0][2] = xform.getXYCoeff20();
        coeff[1][2] = xform.getXYCoeff21();
        coeffCurve[0][0] = xform.getXYCoeff00Curve();
        coeffCurve[0][1] = xform.getXYCoeff01Curve();
        coeffCurve[1][0] = xform.getXYCoeff10Curve();
        coeffCurve[1][1] = xform.getXYCoeff11Curve();
        coeffCurve[0][2] = xform.getXYCoeff20Curve();
        coeffCurve[1][2] = xform.getXYCoeff21Curve();
        rotateCurve = xform.getXYRotateCurve();
        scaleCurve = xform.getXYScaleCurve();
      }
    }

    private List<Integer> collectKeyFrames(MotionCurve... curves) {
      List<Integer> res = new ArrayList<Integer>();
      for (MotionCurve curve : curves) {
        if (!curve.isEmpty() && curve.isEnabled()) {
          for (int i = 0; i < curve.getX().length; i++) {
            Integer keyFrame = Integer.valueOf(curve.getX()[i]);
            if (!res.contains(keyFrame)) {
              res.add(keyFrame);
            }
          }
        }
      }
      return res;
    }

    public AffineParam calculate() {
      AffineBaseParam baseParams = calculateBaseParam(coeff, 0.0, 1.0);

      MotionCurve xAxisAngleCurve = new MotionCurve();
      MotionCurve xAxisLengthCurve = new MotionCurve();
      MotionCurve yAxisAngleCurve = new MotionCurve();
      MotionCurve yAxisLengthCurve = new MotionCurve();
      {
        List<Integer> keyFrames = collectKeyFrames(coeffCurve[0][0], coeffCurve[0][1], coeffCurve[1][0], coeffCurve[1][1], rotateCurve, scaleCurve);
        if (keyFrames.size() > 0) {
          xAxisAngleCurve.setEnabled(true);
          xAxisLengthCurve.setEnabled(true);
          yAxisAngleCurve.setEnabled(true);
          yAxisLengthCurve.setEnabled(true);
          for (Integer keyFrame : keyFrames) {
            double lCoeff[][] = new double[2][3];
            lCoeff[0][0] = evalCurve(coeffCurve[0][0], keyFrame, coeff[0][0]);
            lCoeff[0][1] = evalCurve(coeffCurve[0][1], keyFrame, coeff[0][1]);
            lCoeff[1][0] = evalCurve(coeffCurve[1][0], keyFrame, coeff[1][0]);
            lCoeff[1][1] = evalCurve(coeffCurve[1][1], keyFrame, coeff[1][1]);
            double lRotate = evalCurve(rotateCurve, keyFrame, 0.0);
            double lScale = evalCurve(scaleCurve, keyFrame, 1.0);
            AffineBaseParam lBaseParam = calculateBaseParam(lCoeff, 0.0, lScale);

            xAxisAngleCurve.appendKeyFrame(keyFrame, lBaseParam.getXAxisAngle() - lRotate);
            xAxisLengthCurve.appendKeyFrame(keyFrame, lBaseParam.getXAxisLength());
            yAxisAngleCurve.appendKeyFrame(keyFrame, lBaseParam.getYAxisAngle() - lRotate);
            yAxisLengthCurve.appendKeyFrame(keyFrame, lBaseParam.getYAxisLength());
          }
        }
      }

      MotionCurve xOffsetCurve = new MotionCurve();
      MotionCurve yOffsetCurve = new MotionCurve();
      {
        List<Integer> keyFrames = collectKeyFrames(coeffCurve[0][2], coeffCurve[1][2]);
        if (keyFrames.size() > 0) {
          xOffsetCurve.setEnabled(true);
          yOffsetCurve.setEnabled(true);
          for (Integer keyFrame : keyFrames) {
            double xOffSet = evalCurve(coeffCurve[0][2], keyFrame, baseParams.getXOffset());
            double yOffSet = evalCurve(coeffCurve[1][2], keyFrame, baseParams.getYOffset());
            xOffsetCurve.appendKeyFrame(keyFrame, xOffSet);
            yOffsetCurve.appendKeyFrame(keyFrame, yOffSet);
          }
        }
      }

      return new AffineParam(
          baseParams.getXAxisAngle(), xAxisAngleCurve, baseParams.getXAxisLength(), xAxisLengthCurve,
          baseParams.getYAxisAngle(), yAxisAngleCurve, baseParams.getYAxisLength(), yAxisLengthCurve,
          baseParams.getXOffset(), xOffsetCurve, baseParams.getYOffset(), yOffsetCurve);
    }

    private double evalCurve(MotionCurve curve, Integer keyFrame, double defaultValue) {
      if (curve == null || curve.isEmpty() || !curve.isEnabled()) {
        return defaultValue;
      }
      else {
        return curve.toEnvelope().evaluate(keyFrame);
      }
    }

    private AffineBaseParam calculateBaseParam(double coeff[][], double rotate, double scale) {
      Matrix3x3 affine;
      {
        Matrix3x3 m1 = new Identity3x3();
        m1.val[0][0] = 1.0;
        m1.val[0][1] = 0.0;
        m1.val[1][0] = 0.0;
        m1.val[1][1] = 1.0;
        Matrix3x3 m2 = new Identity3x3();
        m2.val[0][0] = coeff[0][0];
        m2.val[0][1] = coeff[0][1];
        m2.val[1][0] = coeff[1][0];
        m2.val[1][1] = coeff[1][1];
        m2.val[0][2] = coeff[0][2];
        m2.val[1][2] = coeff[1][2];
        affine = XFormTransformService.multiply(m1, m2);
      }
      if (Math.abs(rotate) >= MathLib.SMALL_EPSILON) {
        double alpha = rotate * Math.PI / 180.0;
        Matrix3x3 m1 = new Identity3x3();
        m1.val[0][0] = Math.cos(alpha);
        m1.val[0][1] = -Math.sin(alpha);
        m1.val[1][0] = Math.sin(alpha);
        m1.val[1][1] = Math.cos(alpha);
        Matrix3x3 m2 = new Identity3x3();
        m2.val[0][0] = affine.val[0][0];
        m2.val[0][1] = affine.val[0][1];
        m2.val[1][0] = affine.val[1][0];
        m2.val[1][1] = affine.val[1][1];
        m2.val[0][2] = affine.val[2][0];
        m2.val[1][2] = affine.val[2][1];
        affine = XFormTransformService.multiply(m2, m1);
      }
      if (Math.abs(scale - 1.0) >= MathLib.SMALL_EPSILON) {
        affine.val[0][0] *= scale;
        affine.val[1][1] *= scale;
      }

      double xo = affine.val[0][2];
      double yo = affine.val[1][2];
      double xaxis_x = affine.val[0][0];
      double xaxis_y = affine.val[0][1];
      double yaxis_x = affine.val[1][0];
      double yaxis_y = affine.val[1][1];

      return new AffineParam(
          Math.toDegrees(MathLib.atan2(xaxis_y, xaxis_x)), null,
          MathLib.sqrt(xaxis_x * xaxis_x + xaxis_y * xaxis_y), null,
          Math.toDegrees(MathLib.atan2(yaxis_y, yaxis_x)), null,
          MathLib.sqrt(yaxis_x * yaxis_x + yaxis_y * yaxis_y), null,
          xo, null, yo, null);
    }
  }

  private void addAffine(SimpleXMLBuilder xb, XForm xform, boolean isPost) {
    xb.beginElement(ELEM_AFFINE2, xb.createAttrList(xb.createAttr(ATTR_NAME, isPost ? "Post affine" : "Pre affine")));
    AffineParamCalculator calculator = new AffineParamCalculator(xform, isPost);
    AffineParam affine = calculator.calculate();
    addRealProperty(xb, PROPERTY_X_AXIS_ANGLE, affine.getXAxisAngle(), affine.getXAxisAngleCurve());
    addRealProperty(xb, PROPERTY_X_AXIS_LENGTH, affine.getXAxisLength(), affine.getXAxisLengthCurve());
    addRealProperty(xb, PROPERTY_Y_AXIS_ANGLE, affine.getYAxisAngle(), affine.getYAxisAngleCurve());
    addRealProperty(xb, PROPERTY_Y_AXIS_LENGTH, affine.getYAxisLength(), affine.getYAxisLengthCurve());
    addVec2Property(xb, PROPERTY_OFFSET, affine.getXOffset(), affine.getYOffset(), affine.getXOffsetCurve(), affine.getYOffsetCurve());
    xb.endElement(ELEM_AFFINE2);
  }

  private void addGradient(SimpleXMLBuilder xb, Layer layer) {
    xb.beginElement(ELEM_COLOURING, xb.createAttrList());
    List<Double> colors = new ArrayList<Double>();
    for (int i = 0; i < layer.getPalette().getSize(); i++) {
      RGBColor color = layer.getPalette().getColor(i);
      colors.add(convertColorValue(color.getRed()));
      colors.add(convertColorValue(color.getGreen()));
      colors.add(convertColorValue(color.getBlue()));
    }
    addTableProperty(xb, PROPERTY_FLAM3_PALETTE, colors.toArray(new Double[colors.size()]));
    xb.endElement(ELEM_COLOURING);
  }

  private void addImaging(SimpleXMLBuilder xb, Flame pFlame) {
    xb.beginElement(ELEM_IMAGING, xb.createAttrList(xb.createAttr(ATTR_NAME, "img")));
    addIntProperty(xb, PROPERTY_IMAGE_WIDTH, pFlame.getWidth());
    addIntProperty(xb, PROPERTY_IMAGE_HEIGHT, pFlame.getHeight());
    addIntProperty(xb, PROPERTY_IMAGE_AA_LEVEL, AA_LEVEL);
    addIntProperty(xb, PROPERTY_IMAGE_LAYERS, 1);
    addIntProperty(xb, PROPERTY_IMAGE_QUALITY, 0);
    addStringProperty(xb, PROPERTY_ANTIALIASING_MODE, "strong");
    addRealProperty(xb, PROPERTY_BRIGHTNESS, pFlame.getBrightness(), null);
    addVec4Property(xb, PROPERTY_BACKGROUND_COLOR, convertColorValue(pFlame.getBGColorRed()), convertColorValue(pFlame.getBGColorGreen()), convertColorValue(pFlame.getBGColorBlue()), convertColorValue(255));
    addBoolProperty(xb, PROPERTY_APPLY_BG_BEFORE_CURVES, false);
    addRealProperty(xb, PROPERTY_FLAM3_GAMMA, pFlame.getGamma(), null);
    addRealProperty(xb, PROPERTY_FLAM3_VIBRANCY, pFlame.getVibrancy(), null);
    addBoolProperty(xb, PROPERTY_FLAM3_USE_HIGHLIGHT_POWER, true);
    addRealProperty(xb, PROPERTY_FLAM3_HIGHTLIGHT_POWER, (double) pFlame.getWhiteLevel() / 200.0 - 1.0, null);
    addRealProperty(xb, PROPERTY_FLAM3_GAMMA_LINEAR_THRESHOLD, pFlame.getGammaThreshold(), null);
    addTableProperty(xb, PROPERTY_LAYER_WEIGHTS, new Double[] { 1.0, 1.0, 1.0, 1.0 });
    xb.endElement(ELEM_IMAGING);
  }

  private void addAnimAndCamera(SimpleXMLBuilder xb, Flame pFlame) {
    addIntProperty(xb, PROPERTY_ANIM_LENGTH, Tools.FTOI((double) pFlame.getFrameCount() / (double) pFlame.getFps()));
    addIntProperty(xb, PROPERTY_ANIM_FPS, pFlame.getFps());
    addRealProperty(xb, PROPERTY_ANIM_EXPOSURE_TIME, pFlame.getMotionBlurLength() * pFlame.getMotionBlurTimeStep(), null);
    addStringProperty(xb, PROPERTY_ANIM_EXPOSURE_SHAPE, "uniform");
    xb.beginElement(ELEM_CAMERA, xb.createAttrList(xb.createAttr(ATTR_NAME, PROPERTY_FLAM3_CAMERA)));
    addVec2Property(xb, PROPERTY_POS, pFlame.getCentreX(), -pFlame.getCentreY(), pFlame.getCentreXCurve(), pFlame.getCentreYCurve() != null ? pFlame.getCentreYCurve().multiplyValue(-1.0) : null);
    addRealProperty(xb, PROPERTY_ROTATE, pFlame.getCamRoll(), pFlame.getCamRollCurve());
    double final_scale = pFlame.getPixelsPerUnit() * pFlame.getCamZoom() * 2.0;
    double sensor_width = (double) (pFlame.getWidth() * AA_LEVEL) / final_scale;
    addRealProperty(xb, PROPERTY_SENSOR_WIDTH, sensor_width, null);
    xb.endElement(ELEM_CAMERA);
  }

  private void addIntProperty(SimpleXMLBuilder xb, String property, int value) {
    xb.simpleElement(ELEM_INT, String.valueOf(value), xb.createAttrList(xb.createAttr(ATTR_NAME, property)));
  }

  private void addStringProperty(SimpleXMLBuilder xb, String property, String value) {
    xb.simpleElement(ELEM_STRING, value, xb.createAttrList(xb.createAttr(ATTR_NAME, property)));
  }

  private void addRealProperty(SimpleXMLBuilder xb, String property, double value, MotionCurve pCurve) {
    if (pCurve == null || pCurve.isEmpty() || !pCurve.isEnabled()) {
      xb.simpleElement(ELEM_REAL, Tools.doubleToString(value), xb.createAttrList(xb.createAttr(ATTR_NAME, property)));
    }
    else {
      xb.beginElement(ELEM_REAL, xb.createAttrList(xb.createAttr(ATTR_NAME, property)));
      xb.beginElement(ELEM_CURVE, xb.createAttrList(xb.createAttr(ATTR_NAME, "val_curve")));
      List<Double> time = new ArrayList<Double>();
      List<Double> amp = new ArrayList<Double>();
      for (int i = 0; i < pCurve.getX().length; i++) {
        time.add(convertFrameToTime(pCurve.getX()[i]));
        amp.add(pCurve.getY()[i]);
      }
      addTableProperty(xb, "knots", time.toArray(new Double[time.size()]));
      addTableProperty(xb, "values", amp.toArray(new Double[amp.size()]));
      xb.endElement(ELEM_CURVE);
      xb.endElement(ELEM_REAL);
    }
  }

  private Double convertFrameToTime(int frame) {
    return (double) frame / (double) flame.getFps();
  }

  private void addVec4Property(SimpleXMLBuilder xb, String property, double v1, double v2, double v3, double v4) {
    xb.simpleElement(ELEM_VEC4, Tools.doubleToString(v1) + " " + Tools.doubleToString(v2) + " " + Tools.doubleToString(v3) + " " + Tools.doubleToString(v4), xb.createAttrList(xb.createAttr(ATTR_NAME, property)));
  }

  private void addVec2Property(SimpleXMLBuilder xb, String property, double v1, double v2, MotionCurve v1Curve, MotionCurve v2Curve) {
    if ((v1Curve == null || v1Curve.isEmpty() || !v1Curve.isEnabled()) && (v2Curve == null || v2Curve.isEmpty() || !v2Curve.isEnabled())) {
      xb.simpleElement(ELEM_VEC2, Tools.doubleToString(v1) + " " + Tools.doubleToString(v2), xb.createAttrList(xb.createAttr(ATTR_NAME, property)));
    }
    else {
      if (((v1Curve == null || v1Curve.isEmpty() || !v1Curve.isEnabled()) != (v2Curve == null || v2Curve.isEmpty() || !v2Curve.isEnabled())) || (v1Curve.getX().length != v2Curve.getY().length)) {
        throw new RuntimeException("Non-matching curves");
      }
      xb.beginElement(ELEM_VEC2, xb.createAttrList(xb.createAttr(ATTR_NAME, property)));
      xb.beginElement(ELEM_VEC2_CURVE, xb.createAttrList(xb.createAttr(ATTR_NAME, "val_curve")));
      List<Double> time = new ArrayList<Double>();
      List<Double> amp = new ArrayList<Double>();
      for (int i = 0; i < v1Curve.getX().length; i++) {
        if (v1Curve.getX()[i] != v2Curve.getX()[i]) {
          throw new RuntimeException("Non-matching keys");
        }
        time.add(convertFrameToTime(v1Curve.getX()[i]));
        amp.add(v1Curve.getY()[i]);
        amp.add(v2Curve.getY()[i]);
      }
      addTableProperty(xb, "knots", time.toArray(new Double[time.size()]));
      addTableProperty(xb, "values", amp.toArray(new Double[amp.size()]));
      xb.endElement(ELEM_VEC2_CURVE);
      xb.endElement(ELEM_VEC2);
    }
  }

  private void addBoolProperty(SimpleXMLBuilder xb, String property, boolean value) {
    xb.simpleElement(ELEM_BOOL, String.valueOf(value), xb.createAttrList(xb.createAttr(ATTR_NAME, property)));
  }

  private void addTableProperty(SimpleXMLBuilder xb, String property, Double... values) {
    xb.beginElement(ELEM_TABLE, xb.createAttrList(xb.createAttr(ATTR_NAME, property)));
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < values.length; i++) {
      sb.append(Tools.doubleToString(values[i]));
      if (i < values.length - 1) {
        sb.append(" ");
      }
    }
    xb.simpleElement(ELEM_VALUES, sb.toString(), xb.createAttrList());
    xb.endElement(ELEM_TABLE);
  }

  private double convertColorValue(int color) {
    return (double) color / 255.0;
  }

}
