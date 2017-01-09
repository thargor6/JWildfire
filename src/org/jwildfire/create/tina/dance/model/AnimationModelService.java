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
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;

public class AnimationModelService {

  private static boolean isPrimitiveProperty(Class<?> pCls) {
    return pCls == Integer.class || pCls == int.class || pCls == Double.class || pCls == double.class || pCls == Boolean.class || pCls == boolean.class;
  }

  private static interface PropertyVisitor {
    public boolean accept(Object pOwner, Field pField, PlainProperty pProperty);

    public boolean accept(VariationFunc pVarFunc, PlainProperty pProperty);

    public boolean accept(XForm pXForm, String pPropName, PlainProperty pProperty);

    public boolean finishOnAccept();
  }

  public static PropertyModel createModel(Flame pFlame) {
    PropertyModel res = new PropertyModel(null, "flame", pFlame.getClass());
    visitModel(res, pFlame, null);
    return res;
  }

  private static class VisitState {
    private final PropertyVisitor visitor;
    private boolean accepted;

    public VisitState(PropertyVisitor pVisitor) {
      visitor = pVisitor;
    }

    public boolean isCancelSignalled() {
      return accepted && visitor != null && visitor.finishOnAccept();
    }

    public void updateState(boolean pAccept) {
      accepted = pAccept;
    }

  }

  @SuppressWarnings("unchecked")
  public static void visitModel(PropertyModel res, Flame pFlame, PropertyVisitor pVisitor) {
    VisitState state = new VisitState(pVisitor);
    Class<?> cls = pFlame.getClass();
    for (Field field : cls.getDeclaredFields()) {
      if (state.isCancelSignalled()) {
        return;
      }
      field.setAccessible(true);
      if (field.getAnnotation(AnimAware.class) != null) {
        Class<?> fCls = field.getType();
        if (isPrimitiveProperty(fCls)) {
          PlainProperty property = new PlainProperty(res, field.getName(), cls);
          res.getProperties().add(property);
          if (pVisitor != null) {
            state.updateState(pVisitor.accept(pFlame, field, property));
          }
        }
        else if (fCls == RGBPalette.class) {
          try {
            RGBPalette palette = (RGBPalette) field.get(pFlame);
            addGradientToModel(res, palette, pVisitor, state);
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
        else if (fCls == List.class) {
          ParameterizedType listType = (ParameterizedType) field.getGenericType();
          Class<?> listSubClass = (Class<?>) listType.getActualTypeArguments()[0];
          if (listSubClass.isAssignableFrom(Layer.class)) {
            List<Layer> layers;
            try {
              layers = (List<Layer>) field.get(pFlame);
            }
            catch (Exception e) {
              layers = null;
              e.printStackTrace();
            }
            if (layers != null) {
              int idx = 0;
              for (Layer layer : layers) {
                addLayerToModel(res, idx++, layer, pVisitor, state);
              }
            }
          }
        }
      }
    }
  }

  private static void addGradientToModel(PropertyModel pNode, RGBPalette pPalette, PropertyVisitor pVisitor, VisitState pState) {
    Class<?> cls = pPalette.getClass();
    String fieldname = PROPNAME_GRADIENT;
    PropertyModel gradientNode = new PropertyModel(pNode, fieldname, cls);
    pNode.getChields().add(gradientNode);
    for (Field field : cls.getDeclaredFields()) {
      if (pState.isCancelSignalled()) {
        return;
      }
      field.setAccessible(true);
      if (field.getAnnotation(AnimAware.class) != null) {
        Class<?> fCls = field.getType();
        if (isPrimitiveProperty(fCls)) {
          PlainProperty property = new PlainProperty(gradientNode, field.getName(), cls);
          gradientNode.getProperties().add(property);
          if (pVisitor != null) {
            pState.updateState(pVisitor.accept(pPalette, field, property));
          }
        }
      }
    }
  }

  public final static String PROPNAME_LAYER = "layer";
  public final static String PROPNAME_XFORM = "transform";
  public final static String PROPNAME_FINALXFORM = "finalTransform";
  public final static String PROPNAME_GRADIENT = "gradient";
  public final static String PROPNAME_SHADING = "shading";
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
  private static void addXFormToModel(PropertyModel pNode, boolean pIsFinal, int pIndex, XForm pXForm, PropertyVisitor pVisitor, VisitState pState) {
    Class<?> cls = pXForm.getClass();
    String fieldname = pIsFinal ? PROPNAME_FINALXFORM : PROPNAME_XFORM;
    PropertyModel xFormNode = new PropertyModel(pNode, fieldname + (pIndex + 1), cls);
    pNode.getChields().add(xFormNode);
    for (String propName : ADD_XFORM_PROPS) {
      if (pState.isCancelSignalled()) {
        return;
      }
      PlainProperty property = new PlainProperty(xFormNode, propName, Double.class);
      xFormNode.getProperties().add(property);
      if (pVisitor != null) {
        pState.updateState(pVisitor.accept(pXForm, propName, property));
      }
    }
    for (Field field : cls.getDeclaredFields()) {
      if (pState.isCancelSignalled()) {
        return;
      }
      field.setAccessible(true);
      if (field.getAnnotation(AnimAware.class) != null) {
        Class<?> fCls = field.getType();
        if (isPrimitiveProperty(fCls)) {
          PlainProperty property = new PlainProperty(xFormNode, field.getName(), cls);
          xFormNode.getProperties().add(property);
          if (pVisitor != null) {
            pState.updateState(pVisitor.accept(pXForm, field, property));
          }
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
                addVariationToModel(xFormNode, variation, pVisitor, pState);
              }
            }
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static void addLayerToModel(PropertyModel pNode, int pIndex, Layer pLayer, PropertyVisitor pVisitor, VisitState pState) {
    Class<?> cls = pLayer.getClass();
    PropertyModel layerNode = new PropertyModel(pNode, PROPNAME_LAYER + (pIndex + 1), cls);
    pNode.getChields().add(layerNode);
    for (Field field : cls.getDeclaredFields()) {
      if (pState.isCancelSignalled()) {
        return;
      }
      field.setAccessible(true);
      if (field.getAnnotation(AnimAware.class) != null) {
        Class<?> fCls = field.getType();
        if (isPrimitiveProperty(fCls)) {
          PlainProperty property = new PlainProperty(layerNode, field.getName(), cls);
          layerNode.getProperties().add(property);
          if (pVisitor != null) {
            pState.updateState(pVisitor.accept(pLayer, field, property));
          }
        }
        else if (fCls == List.class) {
          ParameterizedType listType = (ParameterizedType) field.getGenericType();
          Class<?> listSubClass = (Class<?>) listType.getActualTypeArguments()[0];
          if (listSubClass.isAssignableFrom(XForm.class)) {
            List<XForm> xForms;
            try {
              xForms = (List<XForm>) field.get(pLayer);
            }
            catch (Exception e) {
              xForms = null;
              e.printStackTrace();
            }
            if (xForms != null) {
              int idx = 0;
              for (XForm xForm : xForms) {
                addXFormToModel(layerNode, field.getName().indexOf("final") == 0, idx++, xForm, pVisitor, pState);
              }
            }
          }
        }
      }
    }
  }

  private static void addVariationToModel(PropertyModel pXFormNode, Variation pVariation, PropertyVisitor pVisitor, VisitState pState) {
    Class<?> cls = pVariation.getClass();
    PropertyModel variationNode = new PropertyModel(pXFormNode, pVariation.getFunc().getName(), cls);
    pXFormNode.getChields().add(variationNode);
    for (Field field : cls.getDeclaredFields()) {
      if (pState.isCancelSignalled()) {
        return;
      }
      field.setAccessible(true);
      if (field.getAnnotation(AnimAware.class) != null) {
        Class<?> fCls = field.getType();
        if (isPrimitiveProperty(fCls)) {
          PlainProperty property = new PlainProperty(variationNode, field.getName(), cls);
          variationNode.getProperties().add(property);
          if (pVisitor != null) {
            pState.updateState(pVisitor.accept(pVariation, field, property));
          }
        }
      }
    }
    VariationFunc varFunc = pVariation.getFunc();
    String[] params = varFunc.getParameterNames();
    Object[] vals = varFunc.getParameterValues();
    if (params != null) {
      for (int i = 0; i < params.length; i++) {
        if (pState.isCancelSignalled()) {
          return;
        }
        Object val = vals[i];
        if (val instanceof Double) {
          PlainProperty property = new PlainProperty(variationNode, params[i], Double.class);
          variationNode.getProperties().add(property);
          if (pVisitor != null) {
            pState.updateState(pVisitor.accept(varFunc, property));
          }
        }
        else if (val instanceof Integer) {
          PlainProperty property = new PlainProperty(variationNode, params[i], Integer.class);
          variationNode.getProperties().add(property);
          if (pVisitor != null) {
            pState.updateState(pVisitor.accept(varFunc, property));
          }
        }
      }
    }
  }

  public static List<String> createFlamePropertyPath(String pProperty) {
    List<String> res = new ArrayList<String>();
    res.add(pProperty);
    return res;
  }

  public static List<String> createXFormPropertyPath(int pLayerIdx, int pXFormIndex, String pProperty) {
    List<String> res = new ArrayList<String>();
    res.add(PROPNAME_LAYER + (pLayerIdx + 1));
    res.add(PROPNAME_XFORM + (pXFormIndex + 1));
    res.add(pProperty);
    return res;
  }

  public static List<String> createFinalXFormPropertyPath(int pLayerIdx, int pXFormIndex, String pProperty) {
    List<String> res = new ArrayList<String>();
    res.add(PROPNAME_LAYER + (pLayerIdx + 1));
    res.add(PROPNAME_FINALXFORM + (pXFormIndex + 1));
    res.add(pProperty);
    return res;
  }

  private enum ModifyPropMode {
    SET, ADD
  }

  private static class ModifyPropertyVisitor implements PropertyVisitor {
    private final List<String> path;
    protected double value;
    private boolean hasFound = false;
    protected ModifyPropMode mode = ModifyPropMode.SET;

    public ModifyPropertyVisitor(FlamePropertyPath pPath, double pValue) {
      path = pPath.getPathComponents();
      value = pValue;
    }

    public boolean accept(PlainProperty pProperty) {
      AbstractProperty currNode = pProperty;
      hasFound = currNode.getDepth() == path.size();
      for (int i = path.size() - 1; hasFound && i >= 0; i--) {
        if (!currNode.getName().equals(path.get(i))) {
          hasFound = false;
          break;
        }
        currNode = currNode.getParent();
      }
      return hasFound;
    }

    public boolean isHasFound() {
      return hasFound;
    }

    @Override
    public boolean finishOnAccept() {
      return true;
    }

    @Override
    public boolean accept(Object pOwner, Field pField, PlainProperty pProperty) {
      boolean accepted = accept(pProperty);
      if (accepted) {
        if (pField.getType() == Double.class || pField.getType() == double.class) {
          try {
            switch (mode) {
              case SET:
                pField.setDouble(pOwner, value);
                break;
              case ADD:
                Double o = pField.getDouble(pOwner);
                o += value;
                pField.setDouble(pOwner, o);
                break;
            }
          }
          catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
          }
          catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
        else if (pField.getType() == Integer.class || pField.getType() == int.class) {
          try {
            switch (mode) {
              case SET:
                pField.setInt(pOwner, Tools.FTOI(value));
                break;
              case ADD:
                Integer o = pField.getInt(pOwner);
                if (o == null) {
                  o = 0;
                }
                o += Tools.FTOI(value);
                System.out.println(o);
                pField.setInt(pOwner, o);
                break;
            }
          }
          catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
          }
          catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
        else if (pField.getType() == Boolean.class || pField.getType() == boolean.class) {
          try {
            switch (mode) {
              case SET:
                pField.setBoolean(pOwner, Tools.FTOI(value) != 0);
                break;
              default: // nothing to do
                break;
            }
          }
          catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
          }
          catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
        else {
          throw new RuntimeException("Unsupporded property type <" + pField.getType() + ">");
        }
      }
      return accepted;
    }

    @Override
    public boolean accept(VariationFunc pVarFunc, PlainProperty pProperty) {
      boolean accepted = accept(pProperty);
      if (accepted) {
        pVarFunc.setParameter(pProperty.getName(), value);
      }
      return accepted;
    }

    @Override
    public boolean accept(XForm pXForm, String pPropName, PlainProperty pProperty) {
      boolean accepted = accept(pProperty);
      if (accepted) {
        if (pPropName.equals(PROPNAME_ORIGIN_X)) {
          XFormTransformService.localTranslate(pXForm, value, 0, false);
        }
        else if (pPropName.equals(PROPNAME_ORIGIN_Y)) {
          XFormTransformService.localTranslate(pXForm, 0, value, false);
        }
        else if (pPropName.equals(PROPNAME_ANGLE)) {
          XFormTransformService.rotate(pXForm, value, false);
        }
        else if (pPropName.equals(PROPNAME_ZOOM)) {
          XFormTransformService.scale(pXForm, value, true, true, false);
        }
        else if (pPropName.equals(PROPNAME_POST_ORIGIN_X)) {
          XFormTransformService.localTranslate(pXForm, value, 0, true);
        }
        else if (pPropName.equals(PROPNAME_POST_ORIGIN_Y)) {
          XFormTransformService.localTranslate(pXForm, 0, value, true);
        }
        else if (pPropName.equals(PROPNAME_POST_ANGLE)) {
          XFormTransformService.rotate(pXForm, value, true);
        }
        else if (pPropName.equals(PROPNAME_POST_ZOOM)) {
          XFormTransformService.scale(pXForm, value, true, true, true);
        }
        else {
          throw new RuntimeException("Virtual property <" + pPropName + "> not supported");
        }
      }
      return accepted;
    }

  }

  public static void setFlameProperty(Flame pFlame, FlamePropertyPath pPath, double pValue) {
    PropertyModel res = new PropertyModel(null, "flame", pFlame.getClass());
    ModifyPropertyVisitor visitor = new ModifyPropertyVisitor(pPath, pValue);
    visitModel(res, pFlame, visitor);
    if (!visitor.isHasFound()) {
      throw new RuntimeException("Property <" + pPath.getPath() + "> not found");
    }
  }

}
