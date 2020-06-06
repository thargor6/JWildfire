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
package org.jwildfire.create.tina.animate;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.lang.reflect.Field;
import java.util.*;

import javafx.animation.KeyFrame;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.base.solidrender.SolidRenderSettings;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.Envelope.Interpolation;
import org.jwildfire.image.SimpleImage;

public class AnimationService {

  public static final int STARTFRAME = 1;

  public static SimpleImage renderFrame(Flame flame, int pWidth, int pHeight, Prefs pPrefs) throws Exception {
    RenderInfo info = new RenderInfo(pWidth, pHeight, RenderMode.PRODUCTION);
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    FlameRenderer renderer = new FlameRenderer(flame, pPrefs, flame.isBGTransparency(), false);
    RenderedFlame res = renderer.renderFlame(info);
    return res.getImage();
  }

  public static Flame createFrameFlame(int pFrame, int pFrameCount, double pFPS, Flame pFlame, GlobalScript pGlobalScripts[], XFormScript[] pXFormScripts, int pMotionBlurLength, double pMotionBlurTimeStep, int pWidth, int pHeight, Prefs pPrefs) {
    Flame res = pFlame.makeCopy();
    for (GlobalScript script : pGlobalScripts) {
      AnimationService.addMotionCurve(res, script, pFrame, pFrameCount, pFPS);
    }
    for (XFormScript script : pXFormScripts) {
      AnimationService.addMotionCurve(res, script, pFrame, pFrameCount, pFPS);
    }
    res.setFrame(pFrame);
    res.setMotionBlurLength(pMotionBlurLength);
    res.setMotionBlurTimeStep(pMotionBlurTimeStep);
    return res;
  }

  public static <T> double getPropertyValue(T pSource, String pName) {
    @SuppressWarnings("unchecked")
    Class<T> cls = (Class<T>) pSource.getClass();
    Field field;
    try {
      field = cls.getDeclaredField(pName);
      field.setAccessible(true);
      Class<?> fieldCls = field.getType();
      if (fieldCls == double.class || fieldCls == Double.class) {
        Double res = field.getDouble(pSource);
        return res;
      }
      else if (fieldCls == int.class || fieldCls == Integer.class) {
        Integer res = field.getInt(pSource);
        return Double.valueOf(res.intValue());
      }
      else if (fieldCls == boolean.class || fieldCls == Boolean.class) {
        Boolean res = field.getBoolean(pSource);
        return res ? 1.0 : 0.0;
      }
      else {
        throw new IllegalStateException(fieldCls.getName());
      }
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T> void setPropertyValue(T pDest, String pName, double pValue) {
    @SuppressWarnings("unchecked")
    Class<T> cls = (Class<T>) pDest.getClass();
    Field field;
    try {
      field = cls.getDeclaredField(pName);
      field.setAccessible(true);
      Class<?> fieldCls = field.getType();
      if (fieldCls == double.class || fieldCls == Double.class) {
        field.setDouble(pDest, pValue);
      }
      else if (fieldCls == int.class || fieldCls == Integer.class) {
        field.setInt(pDest, (int) MathLib.round(pValue));
      }
      else if (fieldCls == boolean.class || fieldCls == Boolean.class) {
        field.setBoolean(pDest, (int) MathLib.round(pValue) != 0);
      }
      else {
        throw new IllegalStateException(fieldCls.getName());
      }
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T> MotionCurve getPropertyCurve(T pSource, String pName) {
    @SuppressWarnings("unchecked")
    Class<T> cls = (Class<T>) pSource.getClass();
    Field field;
    try {
      field = cls.getDeclaredField(pName + Tools.CURVE_POSTFIX);
      field.setAccessible(true);
      Class<?> fieldCls = field.getType();
      if (fieldCls == MotionCurve.class) {
        return (MotionCurve) field.get(pSource);
      }
      else {
        throw new IllegalStateException(fieldCls.getName());
      }
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  public static Flame evalMotionCurves(Flame pFlame, double pFrame) {
    try {
      _evalMotionCurves(pFlame, pFrame);
      return pFlame;
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  private static void _evalMotionCurves(Object pObject, double pFrame) throws IllegalAccessException {
    Class<?> cls = pObject.getClass();
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getType() == MotionCurve.class && field.getName().endsWith(Tools.CURVE_POSTFIX)) {
        MotionCurve curve = (MotionCurve) field.get(pObject);
        if (curve.isEnabled()) {
          double value = evalCurve(pFrame, curve);
          String propName = field.getName().substring(0, field.getName().length() - Tools.CURVE_POSTFIX.length());
          curve.getChangeHandler().processValueChange(pObject, propName, value);
          if (pObject instanceof RGBPalette) {
            curve.getChangeHandler().processValueChange(pObject, "modified", 1.0);
          }
          //setPropertyValue(pObject, propName, value);
          //          System.out.println(propName + " " + value);
        }
      }
      else if (field.getType().isAssignableFrom(ArrayList.class)) {
        List<?> childs = (List<?>) field.get(pObject);
        if (childs != null) {
          for (Object child : childs) {
            _evalMotionCurves(child, pFrame);
          }
        }
      }
      else if (field.getType().isAssignableFrom(RGBPalette.class)) {
        RGBPalette gradient = (RGBPalette) field.get(pObject);
        _evalMotionCurves(gradient, pFrame);
      }
      else if (field.getType().isAssignableFrom(SolidRenderSettings.class)) {
        SolidRenderSettings settings = (SolidRenderSettings) field.get(pObject);
        _evalMotionCurves(settings, pFrame);
      }
    }
    if (pObject instanceof Variation) {
      Variation var = (Variation) pObject;
      VariationFunc func = var.getFunc();
      for (String name : func.getParameterNames()) {
        MotionCurve curve = var.getMotionCurve(name);
        if (curve != null && curve.isEnabled()) {
          double value = evalCurve(pFrame, curve);
          try {
            func.setParameter(name, value);
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    }
  }

  /**  Resets all motion curves to the default state  */
  public static Flame resetMotionCurves(Flame pFlame) {
    try {
      _resetMotionCurves(pFlame);
      return pFlame;
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  private static void _resetMotionCurves(Object pObject) throws IllegalAccessException {
    Class<?> cls = pObject.getClass();
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getType() == MotionCurve.class && field.getName().endsWith(Tools.CURVE_POSTFIX)) {
        MotionCurve curve = (MotionCurve) field.get(pObject);
        if (curve != null) {
          curve.assign(new MotionCurve());
          curve.setEnabled(false);
        }
      }
      else if (field.getType().isAssignableFrom(ArrayList.class)) {
        List<?> childs = (List<?>) field.get(pObject);
        if (childs != null)
          for (Object child : childs) {
            _resetMotionCurves(child);
          }
      }
      else if (field.getType().isAssignableFrom(RGBPalette.class)) {
        RGBPalette gradient = (RGBPalette) field.get(pObject);
        if (gradient != null)
          _resetMotionCurves(gradient);
      }
    }
    if (pObject instanceof Variation) {
      Variation var = (Variation) pObject;
      VariationFunc func = var.getFunc();
      for (String name : func.getParameterNames()) {
        MotionCurve curve = var.getMotionCurve(name);
        if (curve != null) {
          curve.assign(new MotionCurve());
          curve.setEnabled(false);
        }
      }
    }
  }

  private static void _deleteKeyFrame(Object pObject, int pKeyFrame) throws IllegalAccessException {
    Class<?> cls = pObject.getClass();
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getType() == MotionCurve.class && field.getName().endsWith(Tools.CURVE_POSTFIX)) {
        MotionCurve curve = (MotionCurve) field.get(pObject);
        if (curve != null) {
          curve.deleteKeyFrameIfExists(pKeyFrame);
        }
      }
      else if (field.getType().isAssignableFrom(ArrayList.class)) {
        List<?> childs = (List<?>) field.get(pObject);
        if (childs != null)
          for (Object child : childs) {
            _deleteKeyFrame(child, pKeyFrame);
          }
      }
      else if (field.getType().isAssignableFrom(RGBPalette.class)) {
        RGBPalette gradient = (RGBPalette) field.get(pObject);
        if (gradient != null)
          _deleteKeyFrame(gradient, pKeyFrame);
      }
    }
    if (pObject instanceof Variation) {
      Variation var = (Variation) pObject;
      VariationFunc func = var.getFunc();
      for (String name : func.getParameterNames()) {
        MotionCurve curve = var.getMotionCurve(name);
        if (curve != null) {
          curve.deleteKeyFrameIfExists(pKeyFrame);
        }
      }
    }
  }

  public static void deleteKeyFrame(Flame pFlame, int pKeyFrame) {
    try {
      _deleteKeyFrame(pFlame, pKeyFrame);
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  public static boolean hasKeyFrame(Flame pFlame, int pKeyFrame) {
    try {
      return _hasKeyFrame(pFlame, pKeyFrame);
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  private static boolean _hasKeyFrame(Object pObject, int pKeyFrame) throws IllegalAccessException {
    Class<?> cls = pObject.getClass();
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getType() == MotionCurve.class && field.getName().endsWith(Tools.CURVE_POSTFIX)) {
        MotionCurve curve = (MotionCurve) field.get(pObject);
        if (curve != null && curve.hasKeyFrame(pKeyFrame)) {
          return true;
        }
      }
      else if (field.getType().isAssignableFrom(ArrayList.class)) {
        List<?> childs = (List<?>) field.get(pObject);
        if (childs != null)
          for (Object child : childs) {
            if(_hasKeyFrame(child, pKeyFrame)) {
              return true;
            }
          }
      }
      else if (field.getType().isAssignableFrom(RGBPalette.class)) {
        RGBPalette gradient = (RGBPalette) field.get(pObject);
        if (gradient != null) {
          if(_hasKeyFrame(gradient, pKeyFrame)) {
            return true;
          }
        }
      }
    }
    if (pObject instanceof Variation) {
      Variation var = (Variation) pObject;
      VariationFunc func = var.getFunc();
      for (String name : func.getParameterNames()) {
        MotionCurve curve = var.getMotionCurve(name);
        if (curve != null && curve.hasKeyFrame(pKeyFrame)) {
          return true;
        }
      }
    }
    return false;
  }

  public static int getNextKeyFrame(Flame pFlame, int pStartKeyFrame, boolean pForward) {
    try {
      List<Integer> keyFrames=new ArrayList<>();
      _collectNextKeyFrames(pFlame, keyFrames, pStartKeyFrame, pForward);
      if(keyFrames.isEmpty()) {
        return -1;
      }
      else {
        if(pForward) {
          return keyFrames.stream().min(Comparator.comparing(Integer::intValue)).get();
        }
        else {
          return keyFrames.stream().max(Comparator.comparing(Integer::intValue)).get();
        }
      }
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  private static void _collectNextKeyFrames(Object pObject, List<Integer> pKeyFrames, int pStartKeyFrame, boolean pForward) throws IllegalAccessException {
    Class<?> cls = pObject.getClass();
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getType() == MotionCurve.class && field.getName().endsWith(Tools.CURVE_POSTFIX)) {
        MotionCurve curve = (MotionCurve) field.get(pObject);
        if (curve != null && curve.isEnabled()) {
          int nextKeyFrame = curve.getNextKeyFrame(pStartKeyFrame, pForward);
          if(nextKeyFrame>=0) {
            pKeyFrames.add(nextKeyFrame);
          }
        }
      }
      else if (field.getType().isAssignableFrom(ArrayList.class)) {
        List<?> childs = (List<?>) field.get(pObject);
        if (childs != null) {
          for (Object child : childs) {
            _collectNextKeyFrames(child, pKeyFrames, pStartKeyFrame, pForward);
          }
        }
      }
      else if (field.getType().isAssignableFrom(RGBPalette.class)) {
        RGBPalette gradient = (RGBPalette) field.get(pObject);
        if (gradient != null) {
          _collectNextKeyFrames(gradient, pKeyFrames, pStartKeyFrame, pForward);
        }
      }
    }
    if (pObject instanceof Variation) {
      Variation var = (Variation) pObject;
      VariationFunc func = var.getFunc();
      for (String name : func.getParameterNames()) {
        MotionCurve curve = var.getMotionCurve(name);
        if (curve != null && curve.isEnabled()) {
          int nextKeyFrame = curve.getNextKeyFrame(pStartKeyFrame, pForward);
          if(nextKeyFrame>=0) {
            pKeyFrames.add(nextKeyFrame);
          }
        }
      }
    }
  }

  private static void _duplicateKeyFrame(Object pObject, int pKeyFrame, int pDestKeyFrame) throws IllegalAccessException {
    Class<?> cls = pObject.getClass();
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getType() == MotionCurve.class && field.getName().endsWith(Tools.CURVE_POSTFIX)) {
        MotionCurve curve = (MotionCurve) field.get(pObject);
        if (curve != null && curve.isEnabled()) {
          curve.duplicateKeyFrameIfExists(pKeyFrame, pDestKeyFrame);
        }
      }
      else if (field.getType().isAssignableFrom(ArrayList.class)) {
        List<?> childs = (List<?>) field.get(pObject);
        if (childs != null)
          for (Object child : childs) {
            _duplicateKeyFrame(child, pKeyFrame, pDestKeyFrame);
          }
      }
      else if (field.getType().isAssignableFrom(RGBPalette.class)) {
        RGBPalette gradient = (RGBPalette) field.get(pObject);
        if (gradient != null)
          _duplicateKeyFrame(gradient, pKeyFrame, pDestKeyFrame);
      }
    }
    if (pObject instanceof Variation) {
      Variation var = (Variation) pObject;
      VariationFunc func = var.getFunc();
      for (String name : func.getParameterNames()) {
        MotionCurve curve = var.getMotionCurve(name);
        if (curve != null && curve.isEnabled()) {
          curve.duplicateKeyFrameIfExists(pKeyFrame, pDestKeyFrame);
        }
      }
    }
  }

  public static void duplicateKeyFrame(Flame pFlame, int pKeyFrame, int pDestKeyFrame) {
    try {
      _duplicateKeyFrame(pFlame, pKeyFrame, pDestKeyFrame);
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }


  public static double evalCurve(double pFrame, MotionCurve curve) {
    MotionCurve currCurve = curve;
    double value = 0.0;
    while (currCurve != null) {
      Envelope envelope = currCurve.toEnvelope();
      value += envelope.evaluate(pFrame);
      currCurve = currCurve.getParent();
    }
    return value;
  }

  public static class MotionCurveAttribute {
    private final String name;
    private final MotionCurve motionCurve;

    public MotionCurveAttribute(String pName, MotionCurve pMotionCurve) {
      name = pName;
      motionCurve = pMotionCurve;
    }

    public String getName() {
      return name;
    }

    public MotionCurve getMotionCurve() {
      return motionCurve;
    }
  }

  private static Map<Class<?>, List<Field>> motionCurvePropertyCache = new HashMap<Class<?>, List<Field>>();

  private static List<Field> getMotionCurveProperties(Object pObject) {
    Class<?> cls = pObject.getClass();
    List<Field> res = motionCurvePropertyCache.get(cls);
    if (res == null) {
      res = new ArrayList<Field>();
      motionCurvePropertyCache.put(cls, res);
      for (Field field : cls.getDeclaredFields()) {
        field.setAccessible(true);
        if (field.getType() == MotionCurve.class && field.getName().endsWith(Tools.CURVE_POSTFIX)) {
          res.add(field);
        }
      }
    }
    return res;
  }

  public static List<MotionCurveAttribute> getAllMotionCurves(Object pObject) {
    try {
      List<MotionCurveAttribute> res = new ArrayList<MotionCurveAttribute>();
      for (Field field : getMotionCurveProperties(pObject)) {
        MotionCurve curve = (MotionCurve) field.get(pObject);
        res.add(new MotionCurveAttribute(field.getName(), curve));
      }
      return res;
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static enum EnvelopePointsShape {
    RAMP, SINE
  }

  public static class EnvelopePoints {
    private int envX[];
    private double envY[];

    public EnvelopePoints(SimpleScript pScript, int pFrameCount, double pFPS, EnvelopePointsShape pDefaultShape, double pAmplitude) {
      if (pScript.getAmplitudeCurve().isEnabled()) {
        int[] srcX = pScript.getAmplitudeCurve().getX();
        double[] srcY = pScript.getAmplitudeCurve().getY();
        if (srcX.length > 1) {
          envX = new int[srcX.length + 2];
          envY = new double[srcY.length + 2];
          for (int i = 0; i < srcX.length; i++) {
            envX[i + 1] = srcX[i];
            envY[i + 1] = (double) pFrameCount / (DFLT_DURATION * pFPS) * srcY[i];
          }
          final int DX = 50;
          double DY;

          DY = (envY[envY.length - 2] - envY[envY.length - 3]) / (double) (envX[envX.length - 2] - envX[envX.length - 3]) * DX;
          if (Double.isInfinite(DY) || Double.isNaN(DY))
            DY = 0.0;
          envX[envX.length - 1] = envX[envX.length - 2] + DX;
          envY[envY.length - 1] = envY[envY.length - 2] + DY;

          DY = (envY[2] - envY[1]) / (double) (envX[2] - envX[1]) * DX;
          if (Double.isInfinite(DY) || Double.isNaN(DY))
            DY = 0.0;

          envX[0] = envX[1] - DX;
          envY[0] = envY[1] - DY;
        }
        else {
          envX = new int[1];
          envY = new double[1];
          envX[0] = -10;
          envY[0] = (double) pFrameCount / (DFLT_DURATION * pFPS) * srcY[0];
        }
      }
      else {
        if (fabs(pScript.getAmplitude()) < EPSILON || fabs(pFPS) < EPSILON) {
          envX = new int[1];
          envY = new double[1];
          envX[0] = 0;
          envY[0] = 0.0;
        }
        else {
          switch (pDefaultShape) {
            case RAMP: {
              double amp = pAmplitude * (double) pFrameCount / (DFLT_DURATION * pFPS) * pScript.getAmplitude();
              envX = new int[2];
              envY = new double[2];
              final int DX = 10;
              final double DY = amp / (double) pFrameCount * (double) DX;
              envX[0] = -DX;
              envY[0] = -DY;
              envX[1] = pFrameCount + DX;
              envY[1] = amp + DY;
            }
              break;
            case SINE: {
              int phase = Tools.FTOI(DFLT_DURATION * pFPS / 2.0);
              int start = -phase;
              int cnt = 1;
              while (start <= pFrameCount + phase) {
                start += phase;
                cnt++;
              }
              envX = new int[cnt];
              envY = new double[cnt];

              start = -phase;
              int state = 1;
              for (int i = 0; i < cnt; i++) {
                envX[i] = start;
                switch (state++ % 4) {
                  case 1:
                    envY[i] = -pScript.getAmplitude();
                    break;
                  case 3:
                    envY[i] = pScript.getAmplitude();
                    break;
                  default:
                    envY[i] = 0.0;
                    break;
                }
                start += phase;
              }
            }
              break;
            default:
              throw new IllegalArgumentException(pDefaultShape.toString());
          }
        }
      }
    }

    public int[] getEnvX() {
      return envX;
    }

    public double[] getEnvY() {
      return envY;
    }
  }

  public static void addMotionCurve(Flame pFlame, GlobalScript pScript, int pFrame, int pFrameCount, double pFPS) {
    if (pScript != null && pScript.getScriptType() != null && !GlobalScriptType.NONE.equals(pScript.getScriptType()) && fabs(pScript.getAmplitude()) > EPSILON) {
      EnvelopePoints points;
      MotionCurve curve = null;
      switch (pScript.getScriptType()) {
        case ROTATE_PITCH:
          curve = pFlame.getCamPitchCurve();
          points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.RAMP, 360.0);
          break;
        case ROTATE_ROLL:
          curve = pFlame.getCamRollCurve();
          points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.RAMP, 360.0);
          break;
        case ROTATE_YAW:
          curve = pFlame.getCamYawCurve();
          points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.RAMP, 360.0);
          break;
        case ROTATE_BANK:
          curve = pFlame.getCamBankCurve();
          points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.RAMP, 360.0);
          break;
        case MOVE_CAM_X:
          curve = pFlame.getCamPosXCurve();
          points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.SINE, 1.0);
          break;
        case MOVE_CAM_Y:
          curve = pFlame.getCamPosYCurve();
          points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.SINE, 1.0);
          break;
        case MOVE_CAM_Z:
          curve = pFlame.getCamPosZCurve();
          points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.SINE, 1.0);
          break;
        default:
          throw new IllegalArgumentException(pScript.toString());
      }
      addEnvelope(pFrameCount, curve, points.getEnvX(), points.getEnvY());
    }
  }

  private static void addEnvelope(int pFrameCount, MotionCurve curve, int[] envX, double[] envY) {
    Envelope envelope = new Envelope(envX, envY);
    envelope.setViewXMin(-10);
    envelope.setViewXMax(10 + pFrameCount);
    envelope.setViewYMin(-10000.0);
    envelope.setViewYMax(10000.0);
    envelope.setInterpolation(Interpolation.SPLINE);
    envelope.setSelectedIdx(0);

    if (!curve.isEnabled()) {
      curve.assignFromEnvelope(envelope);
      curve.setEnabled(true);
    }
    else {
      MotionCurve newParentCurve = new MotionCurve();
      newParentCurve.setEnabled(true);
      newParentCurve.assignFromEnvelope(envelope);
      while (curve.getParent() != null) {
        curve = curve.getParent();
      }
      curve.setParent(newParentCurve);
    }
  }

  public static final double DFLT_DURATION = 6.0;

  public static void addMotionCurve(Flame pFlame, XFormScript pScript, int pFrame, int pFrameCount, double pFPS) {
    if (pScript != null && pScript.getScriptType() != null && !XFormScriptType.NONE.equals(pScript.getScriptType()) && fabs(pScript.getAmplitude()) > EPSILON) {
      for (Layer layer : pFlame.getLayers()) {
        switch (pScript.getScriptType()) {
          case ROTATE_FULL: {
            int idx = 0;
            for (XForm xForm : layer.getXForms()) {
              MotionCurve curve = xForm.getXYRotateCurve();
              EnvelopePoints points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.RAMP, idx++ % 2 == 0 ? 360.0 : -360.0);
              addEnvelope(pFrameCount, curve, points.getEnvX(), points.getEnvY());
            }
          }
            break;
          case ROTATE_POST_FULL: {
            int idx = 0;
            for (XForm xForm : layer.getXForms()) {
              MotionCurve curve = xForm.getXYPostRotateCurve();
              EnvelopePoints points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.RAMP, idx++ % 2 == 0 ? 360.0 : -360.0);
              addEnvelope(pFrameCount, curve, points.getEnvX(), points.getEnvY());
            }
          }
            break;
          case ROTATE_FIRST_XFORM:
          case ROTATE_2ND_XFORM:
          case ROTATE_3RD_XFORM:
          case ROTATE_4TH_XFORM:
          case ROTATE_5TH_XFORM:
          case ROTATE_LAST_XFORM:
          case ROTATE_FINAL_XFORM: {
            XForm xForm = null;
            xForm = getXForm(pScript.getScriptType(), layer, xForm);

            if (xForm != null) {
              EnvelopePoints points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.RAMP, 360.0);
              MotionCurve curve = xForm.getXYRotateCurve();
              addEnvelope(pFrameCount, curve, points.getEnvX(), points.getEnvY());
            }
          }
            break;
          case ROTATE_POST_FIRST_XFORM:
          case ROTATE_POST_2ND_XFORM:
          case ROTATE_POST_3RD_XFORM:
          case ROTATE_POST_4TH_XFORM:
          case ROTATE_POST_5TH_XFORM:
          case ROTATE_POST_LAST_XFORM:
          case ROTATE_POST_FINAL_XFORM: {
            XForm xForm = null;
            xForm = getXForm(pScript.getScriptType(), layer, xForm);

            if (xForm != null) {
              EnvelopePoints points = new EnvelopePoints(pScript, pFrameCount, pFPS, EnvelopePointsShape.RAMP, 360.0);
              MotionCurve curve = xForm.getXYPostRotateCurve();
              addEnvelope(pFrameCount, curve, points.getEnvX(), points.getEnvY());
            }
          }
            break;
          default:
            throw new IllegalArgumentException(pScript.toString());
        }
        break;
      }
    }
  }

  private static XForm getXForm(XFormScriptType pScript, Layer layer, XForm xForm) {
    switch (pScript) {
      case ROTATE_FIRST_XFORM:
      case ROTATE_POST_FIRST_XFORM:
        return layer.getXForms().size() > 0 ? layer.getXForms().get(0) : null;
      case ROTATE_2ND_XFORM:
      case ROTATE_POST_2ND_XFORM:
        return layer.getXForms().size() > 1 ? layer.getXForms().get(1) : null;
      case ROTATE_3RD_XFORM:
      case ROTATE_POST_3RD_XFORM:
        return layer.getXForms().size() > 2 ? layer.getXForms().get(2) : null;
      case ROTATE_4TH_XFORM:
      case ROTATE_POST_4TH_XFORM:
        return layer.getXForms().size() > 3 ? layer.getXForms().get(3) : null;
      case ROTATE_5TH_XFORM:
      case ROTATE_POST_5TH_XFORM:
        return layer.getXForms().size() > 4 ? layer.getXForms().get(4) : null;
      case ROTATE_LAST_XFORM:
      case ROTATE_POST_LAST_XFORM:
        return layer.getXForms().size() > 0 ? layer.getXForms().get(layer.getXForms().size() - 1) : null;
      case ROTATE_FINAL_XFORM:
      case ROTATE_POST_FINAL_XFORM:
        return layer.getFinalXForms().size() > 0 ? layer.getFinalXForms().get(0) : null;
      default:
        throw new IllegalArgumentException(pScript.toString());
    }
  }

}
