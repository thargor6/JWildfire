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
package org.jwildfire.base;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.l2fprod.common.beans.BaseBeanInfo;
import com.l2fprod.common.beans.ExtendedPropertyDescriptor;

public abstract class ManagedObject {
  private final List<Preset> presets = new ArrayList<Preset>();

  public String getName() {
    return this.getClass().getSimpleName();
  }

  public final BeanInfo getBeanInfo() {
    List<Field> annotatedFields = new ArrayList<Field>();
    Class<?> cls = this.getClass();
    while (cls != null) {
      for (Field field : cls.getDeclaredFields()) {
        if (field.isAnnotationPresent(Property.class)) {
          annotatedFields.add(field);
        }
      }
      cls = cls.getSuperclass();
    }
    if (annotatedFields.size() > 0)
      return getAnnotationBeanInfo(annotatedFields);
    else
      return null; // getStandardBeanInfo();
  }

  private final BeanInfo getAnnotationBeanInfo(List<Field> pFields) {
    BaseBeanInfo res = new BaseBeanInfo(this.getClass());
    for (Field field : pFields) {
      ExtendedPropertyDescriptor propDesc = res.addProperty(field.getName());
      Property propAnno = field.getAnnotation(Property.class);
      if (propAnno.description().length() > 0)
        propDesc.setShortDescription(propAnno.description());
      propDesc.setCategory(propAnno.category().toString());
      if (propAnno.editorClass() != Property.DEFAULT.class)
        propDesc.setPropertyEditorClass(propAnno.editorClass());
    }
    return res;
  }

  /*
    private final BeanInfo getStandardBeanInfo() {
      BeanInfo beanInfo = new SimpleBeanInfo();
      try {
        beanInfo = Introspector.getBeanInfo(this.getClass(), ManagedObject.class);
      }
      catch (Throwable e) {
        e.printStackTrace();
      }
      return beanInfo;
    }
  */

  public Preset addPreset(String pName) {
    Preset preset = new Preset(pName);
    presets.add(preset);
    return preset;
  }

  public List<Preset> getPresets() {
    return presets;
  }

  public void initPresets() {

  }

  protected Preset getPresetByName(String pName) {
    for (Preset preset : presets) {
      if (preset.getName().equals(pName)) {
        return preset;
      }
    }
    return null;
  }

  protected void applyPreset(Preset preset) {
    BeanInfo beanInfo = getBeanInfo();
    if (beanInfo != null) {
      PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
      if (props != null) {
        for (PropertyDescriptor prop : props) {
          Method writeMethod = prop.getWriteMethod();
          if (writeMethod != null) {
            Object val = null;
            PresetProperty<?> property = preset.getPropertyByName(prop.getName());
            if (property != null) {
              val = property.getValue();
            }
            if (val != null) {
              try {
                writeMethod.invoke(this, val);
              }
              catch (Exception ex) {
                throw new RuntimeException("Error setting property " + prop.getName() + " " + val, ex);
              }
            }
          }
        }
      }
    }
  }

}