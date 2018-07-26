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
package org.jwildfire.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.UIManager;

public enum LookAndFeelType {
  METAL("Metal") {
    private static final String DEFAULT = "Default";
    private static final String OCEAN = "Ocean";

    private String[] themes = new String[] { DEFAULT, OCEAN };

    @Override
    public void changeTo() throws Exception {
      javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }

    @Override
    public List<String> getSubThemes() {
      return Arrays.asList(themes);
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      if (OCEAN.equalsIgnoreCase(pTheme))
        javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.OceanTheme());
      else
        javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
    }
  },
  NIMBUS("Nimbus") {
    @Override
    public void changeTo() throws Exception {
      try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
      }
      catch(Exception ex) {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
      }
    }

    @Override
    public List<String> getSubThemes() {
      return Collections.emptyList();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      // empty      
    }
  },
  SYSTEM("System") {
    @Override
    public void changeTo() throws Exception {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    @Override
    public List<String> getSubThemes() {
      return Collections.emptyList();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      // empty            
    }
  },
  ACRYL("Acryl") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.acryl.AcrylLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme(pTheme);
    }
  },
  AERO("Aero") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.aero.AeroLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.aero.AeroLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.aero.AeroLookAndFeel.setTheme(pTheme);
    }
  },
  ALUMINIUM("Aluminium") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.aluminium.AluminiumLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.aluminium.AluminiumLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.aluminium.AluminiumLookAndFeel.setTheme(pTheme);
    }
  },
  BERNSTEIN("Bernstein") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.bernstein.BernsteinLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.bernstein.BernsteinLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.bernstein.BernsteinLookAndFeel.setTheme(pTheme);
    }
  },
  FAST("Fast") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.fast.FastLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.fast.FastLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.fast.FastLookAndFeel.setTheme(pTheme);
    }
  },
  GRAPHITE("Graphite") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.graphite.GraphiteLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.graphite.GraphiteLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.graphite.GraphiteLookAndFeel.setTheme(pTheme);
    }
  },
  HIFI("HiFi") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.hifi.HiFiLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.hifi.HiFiLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.hifi.HiFiLookAndFeel.setTheme(pTheme);
    }
  },
  LUNA("Luna") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.luna.LunaLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.luna.LunaLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.luna.LunaLookAndFeel.setTheme(pTheme);
    }
  },
  MCWIN("McWin") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.mcwin.McWinLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme(pTheme);
    }
  },
  MINT("Mint") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.mint.MintLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.mint.MintLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.mint.MintLookAndFeel.setTheme(pTheme);
    }
  },
  NOIRE("Noire") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.noire.NoireLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.noire.NoireLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.noire.NoireLookAndFeel.setTheme(pTheme);
    }
  },
  SMART("Smart") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.smart.SmartLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.smart.SmartLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.smart.SmartLookAndFeel.setTheme(pTheme);
    }
  },
  TEXTURE("Texture") {
    @Override
    public void changeTo() throws Exception {
      com.jtattoo.plaf.texture.TextureLookAndFeel.setTheme("Default");
      UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSubThemes() {
      return com.jtattoo.plaf.texture.TextureLookAndFeel.getThemes();
    }

    @Override
    public void changeTheme(String pTheme) throws Exception {
      com.jtattoo.plaf.texture.TextureLookAndFeel.setTheme(pTheme);
    }
  };

  private final String caption;

  private LookAndFeelType(String pCaption) {
    caption = pCaption;
  }

  public abstract void changeTo() throws Exception;

  public abstract void changeTheme(String pTheme) throws Exception;

  public String getCaption() {
    return caption;
  }

  protected abstract List<String> getSubThemes();

  public List<String> getFilteredSubThemes() {
    List<String> res = new ArrayList<String>();
    for (String theme : getSubThemes()) {
      if (!theme.contains("Font")) {
        res.add(theme);
      }
    }
    return res;
  }

}
