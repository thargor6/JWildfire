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
package org.jwildfire.create.tina.dance.model;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;

public class AnimationModelService {

  private static boolean isPrimitiveProperty(Class<?> pCls) {
    return pCls == Integer.class || pCls == int.class || pCls == Double.class || pCls == double.class || pCls == Boolean.class || pCls == boolean.class;
  }

  @SuppressWarnings("unchecked")
  public static PropertyModel createModel(Flame pFlame) {
    PropertyModel res = new PropertyModel("flame", pFlame.getClass());
    Class<?> cls = pFlame.getClass();
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getAnnotation(AnimAware.class) != null) {
        Class<?> fCls = field.getType();
        if (isPrimitiveProperty(fCls)) {
          PlainProperty property = new PlainProperty(field.getName(), cls);
          res.getProperties().add(property);
        }
        else if (fCls == List.class) {
          ParameterizedType listType = (ParameterizedType) field.getGenericType();
          Class<?> listSubClass = (Class<?>) listType.getActualTypeArguments()[0];
          if (listSubClass.isAssignableFrom(XForm.class)) {
            List<XForm> xForms;
            try {
              xForms = (List<XForm>) field.get(pFlame);
            }
            catch (Exception e) {
              xForms = null;
              e.printStackTrace();
            }
            if (xForms != null) {
              int idx = 0;
              for (XForm xForm : xForms) {
                addXFormToModel(res, field.getName().indexOf("final") == 0, idx++, xForm);
              }
            }
          }
        }
      }
    }
    return res;
  }

  public final static String PROPNAME_XFORM = "transform";
  public final static String PROPNAME_FINALXFORM = "finalTransform";
  public final static String PROPNAME_ORIGIN_X = "originX";
  public final static String PROPNAME_ORIGIN_Y = "originY";
  public final static String PROPNAME_ANGLE = "angle";
  public final static String PROPNAME_ZOOM = "zoom";
  public final static String PROPNAME_POST_ORIGIN_X = "postOriginX";
  public final static String PROPNAME_POST_ORIGIN_Y = "postOriginY";
  public final static String PROPNAME_POST_ANGLE = "postAngle";
  public final static String PROPNAME_POST_ZOOM = "postZoom";

  private final static String[] ADD_XFORM_PROPS = { PROPNAME_ORIGIN_X, PROPNAME_ORIGIN_Y, PROPNAME_ANGLE, PROPNAME_ZOOM, PROPNAME_POST_ORIGIN_X, PROPNAME_POST_ORIGIN_Y, PROPNAME_POST_ANGLE, PROPNAME_POST_ZOOM };

  @SuppressWarnings("unchecked")
  private static void addXFormToModel(PropertyModel pNode, boolean pIsFinal, int pIndex, XForm pXForm) {
    Class<?> cls = pXForm.getClass();
    String fieldname = pIsFinal ? PROPNAME_FINALXFORM : PROPNAME_XFORM;
    PropertyModel xFormNode = new PropertyModel(fieldname + (pIndex + 1), cls);
    pNode.getChields().add(xFormNode);
    for (String propName : ADD_XFORM_PROPS) {
      PlainProperty property = new PlainProperty(propName, Double.class);
      xFormNode.getProperties().add(property);
    }
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getAnnotation(AnimAware.class) != null) {
        Class<?> fCls = field.getType();
        if (isPrimitiveProperty(fCls)) {
          PlainProperty property = new PlainProperty(field.getName(), cls);
          xFormNode.getProperties().add(property);
        }
        else if (fCls == List.class) {
          ParameterizedType listType = (ParameterizedType) field.getGenericType();
          Class<?> listSubClass = (Class<?>) listType.getActualTypeArguments()[0];
          if (listSubClass.isAssignableFrom(Variation.class)) {
            List<Variation> variations;
            try {
              variations = (List<Variation>) field.get(pXForm);
            }
            catch (Exception e) {
              variations = null;
              e.printStackTrace();
            }
            if (variations != null) {
              for (Variation variation : variations) {
                addVariationToModel(xFormNode, variation);
              }
            }
          }
        }
      }
    }
  }

  private static void addVariationToModel(PropertyModel pXFormNode, Variation pVariation) {
    Class<?> cls = pVariation.getClass();
    PropertyModel variationNode = new PropertyModel(pVariation.getFunc().getName(), cls);
    pXFormNode.getChields().add(variationNode);
    for (Field field : cls.getDeclaredFields()) {
      field.setAccessible(true);
      if (field.getAnnotation(AnimAware.class) != null) {
        Class<?> fCls = field.getType();
        if (isPrimitiveProperty(fCls)) {
          PlainProperty property = new PlainProperty(field.getName(), cls);
          variationNode.getProperties().add(property);
        }
      }
    }
    VariationFunc varFunc = pVariation.getFunc();
    String[] params = varFunc.getParameterNames();
    Object[] vals = varFunc.getParameterValues();
    if (params != null) {
      for (int i = 0; i < params.length; i++) {
        Object val = vals[i];
        if (val instanceof Double) {
          PlainProperty property = new PlainProperty(params[i], Double.class);
          variationNode.getProperties().add(property);
        }
        else if (val instanceof Integer) {
          PlainProperty property = new PlainProperty(params[i], Integer.class);
          variationNode.getProperties().add(property);
        }
      }
    }
  }
}
