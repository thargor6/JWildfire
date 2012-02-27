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

import javax.swing.UIManager;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;

public class LookAndFeel {
  public static final String PLAF_METAL = "javax.swing.plaf.metal.MetalLookAndFeel";
  public static final String PLAF_NIMBUS = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
  public static final String PLAF_WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
  public static final String PLAF_MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
  public static final String PLAF_MAC = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
  public static final String PLAF_ACRYL = "com.jtattoo.plaf.acryl.AcrylLookAndFeel";
  public static final String PLAF_AERO = "com.jtattoo.plaf.aero.AeroLookAndFeel";
  public static final String PLAF_ALUMINIUM = "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel";
  public static final String PLAF_BERNSTEIN = "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel";
  public static final String PLAF_FAST = "com.jtattoo.plaf.fast.FastLookAndFeel";
  public static final String PLAF_GRAPHITE = "com.jtattoo.plaf.graphite.GraphiteLookAndFeel";
  public static final String PLAF_HIFI = "com.jtattoo.plaf.hifi.HiFiLookAndFeel";
  public static final String PLAF_LUNA = "com.jtattoo.plaf.luna.LunaLookAndFeel";
  public static final String PLAF_MCWIN = "com.jtattoo.plaf.mcwin.McWinLookAndFeel";
  public static final String PLAF_MINT = "com.jtattoo.plaf.mint.MintLookAndFeel";
  public static final String PLAF_NOIRE = "com.jtattoo.plaf.noire.NoireLookAndFeel";
  public static final String PLAF_SMART = "com.jtattoo.plaf.smart.SmartLookAndFeel";
  public static final String PLAF_CUSTOM = "com.jtattoo.plaf.custom.systemx.SystemXLookAndFeel";

  public static final String THEME_DEFAULT = "Default";
  public static final String THEME_GREEN = "Green";

  private static boolean setLookAndFeel(String pLookAndFeel, String pTheme) {
    try {
      try {
        if (pLookAndFeel.equalsIgnoreCase(PLAF_FAST)) {
          com.jtattoo.plaf.fast.FastLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_SMART)) {
          com.jtattoo.plaf.smart.SmartLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_ACRYL)) {
          com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_AERO)) {
          com.jtattoo.plaf.aero.AeroLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_BERNSTEIN)) {
          com.jtattoo.plaf.bernstein.BernsteinLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_ALUMINIUM)) {
          com.jtattoo.plaf.aluminium.AluminiumLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_MCWIN)) {
          com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_MINT)) {
          com.jtattoo.plaf.mint.MintLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_HIFI)) {
          com.jtattoo.plaf.hifi.HiFiLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_NOIRE)) {
          com.jtattoo.plaf.noire.NoireLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
        else if (pLookAndFeel.equalsIgnoreCase(PLAF_LUNA)) {
          com.jtattoo.plaf.luna.LunaLookAndFeel.setTheme(pTheme, "", Tools.APP_TITLE);
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      UIManager.setLookAndFeel(pLookAndFeel);
      return true;
    }
    catch (Throwable ex) {
      return false;
    }
  }

  public static void setLookAndFeel() {
    try {
      Prefs prefs = new Prefs();
      prefs.loadFromFile();
      if (setLookAndFeel(prefs.getPlafStyle(), prefs.getPlafTheme()))
        return;
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
    if (setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", null))
      return;
    setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel", null);
  }
}
