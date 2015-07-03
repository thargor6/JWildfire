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
package org.jwildfire.swing;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Map;
import java.util.Map.Entry;

import org.jwildfire.base.ManagedObject;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

public class PropertyPanel extends PropertySheetPanel {

  private static final long serialVersionUID = 1L;
  private ManagedObject managedObject;

  public PropertyPanel(ManagedObject pManagedObject) {
    this(pManagedObject, null);
  }

  public PropertyPanel(ManagedObject pManagedObject, Map<Class, PropertyEditor> pEditors) {
    super();
    try {
      managedObject = pManagedObject;
      if (pEditors != null) {
        PropertyEditorRegistry registry = (PropertyEditorRegistry) getEditorFactory();
        for (Entry<Class, PropertyEditor> pedit : pEditors.entrySet()) {
          registry.registerEditor(pedit.getKey(), pedit.getValue());
        }
      }
      setMode(PropertySheet.VIEW_AS_CATEGORIES);
      setSortingCategories(true);
      setSortingProperties(true);
      setRestoreToggleStates(true);

      setToolBarVisible(false);
      setDescriptionVisible(true);

      if (pManagedObject != null) {

        BeanInfo beanInfo = managedObject.getBeanInfo();

        if (beanInfo != null)
          setBeanInfo(beanInfo);

        readFromObject(managedObject);

        PropertyChangeListener listener = new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            Property prop = (Property) evt.getSource();
            //System.out.println("WRITE " + prop.getName());
            prop.writeToObject(managedObject);
          }
        };
        addPropertySheetChangeListener(listener);
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  public void readFromObject(ManagedObject pManagedObject) {
    Property[] properties = getProperties();
    for (int i = 0, c = properties.length; i < c; i++) {
      try {
        properties[i].readFromObject(pManagedObject);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void writeToObject(ManagedObject pManagedObject) {
    if (managedObject.getClass().equals(pManagedObject.getClass())) {
      Property[] properties = getProperties();
      for (int i = 0, c = properties.length; i < c; i++) {
        try {
          // System.out.println("REWRITE " + properties[i].getName());
          properties[i].writeToObject(pManagedObject);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
