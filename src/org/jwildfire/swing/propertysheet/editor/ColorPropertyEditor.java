/**
 * L2FProd.com Common Components 6.11 License.
 *
 * Copyright 2005-2006 L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jwildfire.swing.propertysheet.editor;

import java.awt.Color;

//import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import org.jwildfire.swing.propertysheet.renderer.ColorCellRenderer;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.util.ResourceManager;


/**
 * ColorPropertyEditor. <br>
 *  
 */
public class ColorPropertyEditor extends AbstractPropertyEditor {

  private ColorCellRenderer label;
  //private JButton button;
  private Color color;

  public ColorPropertyEditor() {
    editor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
    ((JPanel) editor).add("*", label = new ColorCellRenderer());
    // label.setOpaque(false);
    /*    
        ((JPanel) editor).add(button = ComponentFactory.Helper.getFactory().createMiniButton());    
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            selectColor();
          }
        });
    */
    editor.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent e) {
        selectColor();
      }
    });

    //  ((JPanel) editor).setOpaque(false);
  }

  public Object getValue() {
    return color;
  }

  public void setValue(Object value) {
    color = (Color) value;
    label.setValue(color);
  }

  protected void selectColor() {
    ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
    String title = rm.getString("ColorPropertyEditor.title");
    Color selectedColor = JColorChooser.showDialog(editor, title, color);

    if (selectedColor != null) {
      Color oldColor = color;
      Color newColor = selectedColor;
      label.setValue(newColor);
      color = newColor;
      firePropertyChange(oldColor, newColor);
    }
  }

  protected void selectNull() {
    Color oldColor = color;
    label.setValue(null);
    color = null;
    firePropertyChange(oldColor, null);
  }

  public static class AsInt extends ColorPropertyEditor {
    public void setValue(Object arg0) {
      if (arg0 instanceof Integer) {
        super.setValue(new Color(((Integer) arg0).intValue()));
      }
      else {
        super.setValue(arg0);
      }
    }

    public Object getValue() {
      Object value = super.getValue();
      if (value == null) {
        return null;
      }
      else {
        return Integer.valueOf(((Color) value).getRGB());
      }
    }

    protected void firePropertyChange(Object oldValue, Object newValue) {
      if (oldValue instanceof Color) {
        oldValue = Integer.valueOf(((Color) oldValue).getRGB());
      }
      if (newValue instanceof Color) {
        newValue = Integer.valueOf(((Color) newValue).getRGB());
      }
      super.firePropertyChange(oldValue, newValue);
    }
  }
}
