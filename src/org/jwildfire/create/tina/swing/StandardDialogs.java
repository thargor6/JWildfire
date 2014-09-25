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
package org.jwildfire.create.tina.swing;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.jwildfire.base.Tools;

public final class StandardDialogs {

  private static final String WND_TITLE = Tools.APP_TITLE + " Dialog";

  public static String promptForText(Component pParent, String pCaption, String pDefaultValue) {
    String s = (String) JOptionPane.showInputDialog(
        pParent,
        pCaption,
        WND_TITLE,
        JOptionPane.PLAIN_MESSAGE,
        null,
        null,
        pDefaultValue);
    return s;
  }

  public static boolean confirm(Component pParent, String pCaption) {
    return JOptionPane.showConfirmDialog(
        pParent,
        pCaption,
        WND_TITLE,
        JOptionPane.YES_NO_OPTION) == 0;
  }

  public static void message(Component pParent, String pCaption) {
    JOptionPane.showMessageDialog(
        pParent,
        pCaption,
        WND_TITLE,
        JOptionPane.OK_OPTION);
  }
}
