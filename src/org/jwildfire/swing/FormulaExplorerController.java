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

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.nfunk.jep.Node;


public class FormulaExplorerController {
  private final FormulaPanel formulaPanel;
  private final JTextField formula1REd;
  private final JTextField formula2REd;
  private final JTextField formula3REd;
  private final JTextField xMinREd;
  private final JTextField xMaxREd;
  private final JTextField xCountREd;
  private final JTextArea valuesTextArea;

  public FormulaExplorerController(FormulaPanel pFormulaPanel, JTextField pFormula1REd,
      JTextField pFormula2REd, JTextField pFormula3REd, JTextField pXMinREd, JTextField pXMaxREd,
      JTextField pXCountREd, JTextArea pValuesTextArea) {
    super();
    formulaPanel = pFormulaPanel;
    formula1REd = pFormula1REd;
    formula2REd = pFormula2REd;
    formula3REd = pFormula3REd;
    xMinREd = pXMinREd;
    xMaxREd = pXMaxREd;
    xCountREd = pXCountREd;
    valuesTextArea = pValuesTextArea;
  }

  public void calculate() throws Exception {
    final int MAX_FORMULA_COUNT = 3;
    StringBuilder sb = new StringBuilder();
    double xmin = Double.parseDouble(xMinREd.getText());
    double xmax = Double.parseDouble(xMaxREd.getText());
    double xstep;
    int xCount = Integer.parseInt(xCountREd.getText());
    if (xCount < 2)
      xCount = 2;
    xstep = (xmax - xmin) / (double) (xCount - 1);
    JEPWrapper parser = new JEPWrapper();
    parser.addVariable("x", 0.0);
    Node[] fNode = new Node[MAX_FORMULA_COUNT];
    int fCount = 0;
    if (formula1REd.getText() != null && formula1REd.getText().length() > 0)
      fNode[fCount++] = parser.parse(formula1REd.getText());
    if (formula2REd.getText() != null && formula2REd.getText().length() > 0)
      fNode[fCount++] = parser.parse(formula2REd.getText());
    if (formula3REd.getText() != null && formula3REd.getText().length() > 0)
      fNode[fCount++] = parser.parse(formula3REd.getText());
    if (fCount == 0)
      fNode[fCount++] = parser.parse("0");
    double x[] = new double[xCount];
    double y[][] = new double[fCount][xCount];
    double xc = xmin;
    for (int i = 0; i < xCount; i++) {
      x[i] = xc;
      parser.setVarValue("x", xc);
      sb.append("  f(" + Tools.doubleToString(x[i]) + ")  \t");
      for (int j = 0; j < fCount; j++) {
        y[j][i] = (Double) parser.evaluate(fNode[j]);
        sb.append(Tools.doubleToString(y[j][i]) + " \t");
      }
      sb.append("\n");
      xc += xstep;
    }
    formulaPanel.setData(x, y);
    formulaPanel.repaint();
    valuesTextArea.setText(sb.toString());
    valuesTextArea.setCaretPosition(0);
  }
}
