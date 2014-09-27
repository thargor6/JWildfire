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
package org.jwildfire.create.tina.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jwildfire.envelope.Envelope;
import org.jwildfire.swing.ErrorHandler;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.ComponentFactory;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

/**
 * FontPropertyEditor.<br>
 *
 */
public class EnvelopePropertyEditor extends AbstractPropertyEditor {

  private DefaultCellRenderer label;
  private JButton button;
  private Envelope envelope;

  public EnvelopePropertyEditor() {
    editor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
    ((JPanel) editor).add("*", label = new DefaultCellRenderer());
    label.setOpaque(false);
    ((JPanel) editor).add(button = ComponentFactory.Helper.getFactory()
        .createMiniButton());
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectEnvelope();
      }
    });
    ((JPanel) editor).setOpaque(false);
  }

  public Object getValue() {
    return envelope;
  }

  public void setValue(Object value) {
    envelope = (Envelope) value;
    label.setValue(value);
  }

  protected void selectEnvelope() {
    Envelope editEnvelope = envelope.clone();
    EnvelopeDialog dlg = new EnvelopeDialog(SwingUtilities.getWindowAncestor(editor), new ErrorHandler() {

      @Override
      public void handleError(Throwable pThrowable) {
        pThrowable.printStackTrace();

      }
    }, editEnvelope, false);
    dlg.setModal(true);
    dlg.setVisible(true);
    if (dlg.isConfirmed()) {
      Envelope oldEnvelope = envelope;
      label.setValue(editEnvelope);
      envelope = editEnvelope;
      firePropertyChange(oldEnvelope, editEnvelope);
    }
  }

}
