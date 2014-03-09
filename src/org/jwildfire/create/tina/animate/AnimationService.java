/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.MotionCurve;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.image.SimpleImage;

public class AnimationService {

  public static Flame createFlame(Flame pFlame, GlobalScript pGlobalScripts[], double pGlobalTime, XFormScript pXFormScripts[], double pXFormTime, Prefs pPrefs) {
    Flame flame = pFlame.makeCopy();
    double camPitch = flame.getCamPitch();
    double camRoll = flame.getCamRoll();
    double camYaw = flame.getCamYaw();

    for (GlobalScript script : pGlobalScripts) {
      if (script != null) {
        switch (script) {
          case ROTATE_PITCH: {
            camPitch += 360.0 * pGlobalTime;
          }
            break;
          case ROTATE_PITCH_NEG: {
            camPitch += -360.0 * pGlobalTime;
          }
            break;
          case ROTATE_YAW: {
            camYaw += 360.0 * pGlobalTime;
          }
            break;
          case ROTATE_YAW_NEG: {
            camYaw += -360.0 * pGlobalTime;
          }
            break;
          case ROTATE_ROLL: {
            camRoll += 360.0 * pGlobalTime;
            flame.setCamRoll(camRoll);
          }
            break;
          case ROTATE_ROLL_NEG: {
            camRoll += -360.0 * pGlobalTime;
          }
            break;
        }
      }
      flame.setCamPitch(camPitch);
      flame.setCamRoll(camRoll);
      flame.setCamYaw(camYaw);
    }
    for (XFormScript script : pXFormScripts) {
      if (script != null) {
        switch (script) {
          case ROTATE_FULL: {
            for (Layer layer : flame.getLayers()) {
              int idx = 0;
              for (XForm xForm : layer.getXForms()) {
                idx++;
                double angle = 360.0 * pXFormTime;
                if (idx % 2 == 0) {
                  angle = -angle;
                }
                XFormTransformService.rotate(xForm, angle);
              }
            }
          }
            break;
          case ROTATE_SLIGHTLY: {
            for (Layer layer : flame.getLayers()) {
              int idx = 0;
              for (XForm xForm : layer.getXForms()) {
                double maxAngle = (++idx * 3.0) + 90;
                double angle = maxAngle * pXFormTime;
                angle = maxAngle * (1.0 - Math.cos(pXFormTime * 2.0 * Math.PI)) * 0.5;
                if (idx % 2 == 0) {
                  angle = -angle;
                }
                XFormTransformService.rotate(xForm, angle);
              }
            }
          }
            break;
          case ROTATE_LAST_XFORM: {
            for (Layer layer : flame.getLayers()) {
              if (layer.getXForms().size() > 0) {
                XForm xForm = layer.getXForms().get(layer.getXForms().size() - 1);
                double angle = 360.0 * pXFormTime;
                XFormTransformService.rotate(xForm, angle);
              }
            }
          }
            break;
          case ROTATE_FIRST_XFORM: {
            for (Layer layer : flame.getLayers()) {
              if (layer.getXForms().size() > 0) {
                XForm xForm = layer.getXForms().get(0);
                double angle = 360.0 * pXFormTime;
                XFormTransformService.rotate(xForm, angle);
              }
            }
          }
            break;
          case ROTATE_2ND_XFORM: {
            for (Layer layer : flame.getLayers()) {
              if (layer.getXForms().size() > 1) {
                XForm xForm = layer.getXForms().get(1);
                double angle = 360.0 * pXFormTime;
                XFormTransformService.rotate(xForm, angle);
              }
            }
          }
            break;
          case ROTATE_3RD_XFORM: {
            for (Layer layer : flame.getLayers()) {
              if (layer.getXForms().size() > 2) {
                XForm xForm = layer.getXForms().get(2);
                double angle = 360.0 * pXFormTime;
                XFormTransformService.rotate(xForm, angle);
              }
            }
          }
            break;
          case ROTATE_4TH_XFORM: {
            for (Layer layer : flame.getLayers()) {
              if (layer.getXForms().size() > 3) {
                XForm xForm = layer.getXForms().get(3);
                double angle = 360.0 * pXFormTime;
                XFormTransformService.rotate(xForm, angle);
              }
            }
          }
            break;
          case ROTATE_5TH_XFORM: {
            for (Layer layer : flame.getLayers()) {
              if (layer.getXForms().size() > 4) {
                XForm xForm = layer.getXForms().get(4);
                double angle = 360.0 * pXFormTime;
                XFormTransformService.rotate(xForm, angle);
              }
            }
          }
            break;
        }
      }
    }
    return flame;
  }

  public static SimpleImage renderFrame(int pFrame, int pFrames, Flame pFlame, GlobalScript pGlobalScripts[], MotionSpeed pGlobalSpeed, XFormScript[] pXFormScripts, MotionSpeed pXFormSpeed, int pWidth, int pHeight, Prefs pPrefs) throws Exception {
    double globalTime = pGlobalSpeed.calcTime(pFrame, pFrames, true);
    double xFormTime = pXFormSpeed.calcTime(pFrame, pFrames, true);
    Flame flame = createFlame(pFlame, pGlobalScripts, globalTime, pXFormScripts, xFormTime, pPrefs);
    RenderInfo info = new RenderInfo(pWidth, pHeight);
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    FlameRenderer renderer = new FlameRenderer(flame, pPrefs, flame.isBGTransparency(), false);
    RenderedFlame res = renderer.renderFlame(info);
    return res.getImage();
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
        return res != null ? res.doubleValue() : 0.0;
      }
      else if (fieldCls == int.class || fieldCls == Integer.class) {
        Integer res = field.getInt(pSource);
        return res != null ? Double.valueOf(res.intValue()) : 0.0;
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
      field = cls.getDeclaredField(pName + CURVE_POSTFIX);
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

  private static final String CURVE_POSTFIX = "Curve";

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
      if (field.getType() == MotionCurve.class && field.getName().endsWith(CURVE_POSTFIX)) {
        MotionCurve curve = (MotionCurve) field.get(pObject);
        if (curve.isEnabled()) {
          Envelope envelope = curve.toEnvelope();
          double value = envelope.evaluate(pFrame);
          String propName = field.getName().substring(0, field.getName().length() - CURVE_POSTFIX.length());
          setPropertyValue(pObject, propName, value);
          //          System.out.println(propName + " " + value);
        }
      }
      else if (field.getType().isAssignableFrom(ArrayList.class)) {
        List<?> childs = (List<?>) field.get(pObject);
        for (Object child : childs) {
          _evalMotionCurves(child, pFrame);
        }
      }
    }
  }

}
