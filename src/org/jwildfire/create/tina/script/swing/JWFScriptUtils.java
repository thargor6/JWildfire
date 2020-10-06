/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2020 Andreas Maschke

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
package org.jwildfire.create.tina.script.swing;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.io.Flam3GradientReader;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.swing.FileDialogTools;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class JWFScriptUtils {

  private JWFScriptUtils() {
  }

  public interface ScriptNode {
    public String getScript() throws Exception;

    public String getDescription() throws Exception;

    public String getCaption();

  }

  private static class InvalidScriptFolderNode extends JWFScriptFolderNode {
    private static final long serialVersionUID = 1L;

    public InvalidScriptFolderNode() {
      super("(user-script-path is empty, check the Prefs)", null, true);
    }
  }

  public static void initScriptLibrary(JTree scriptTree, Consumer<DefaultMutableTreeNode> userScriptsRootNodeConsumer) {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Script library", true);
    // Internal scripts
    {
      String[] ressources = {
              "Custom_Variation_Loader_GH",
              "LoonieSplits DT",
              "Buckballs by DT",
              "M&M Flower DT",
              "Add Random Final FX by MH and MO",
              "Textured_Cylinders_Rev01_by_MH",
              "Crackle_Styles_Chooser_Rev01_by_MH",
              "Escher Flux", "Mobius Dragon", "Soft Julian", "Wrap into Bubble", "Wrap into Heart", "Wrap into SubFlame",
              "HypertilePoincare_Rev2", "Bwraps-bubble-Julian2", "Bwraps and bubbles", "Oily_Juliascope_Rev1", "Oily_Rev3",
              "Plastic", "SphericalCross_Rev2", "SuperSusan_Rev1", "TomsSpiralSpiral_Rev3",

              "Keep 'Em Separated and Framed BS", "Minkowscope-Painting-BS",

              "UG-", "UG-Ran", "UG-Ran2", "UG-Ran2sym", "UG-Ran2symPersp", "UG-Ran2symPerspYax", "UG-RanRan", "UG-RanRanRan", "UG-Sym", "UG-Sym2",

              "HB  0 Final 2 Galaxies", "HB 0 2 Galaxy Final Test", "HB 0 Final 2 Galaxies-cloudier", "HB 0 Final 2 Galaxiesv2",
              "HB 0 Final 2 Gassv2", "HB 0 Single  Galaxy", "HB 1 planetoid", "HB 2  Gas Clouds", "HB 2  Gas CloudsRan",
              "HB 2 Galaxies Gas Broad", "HB 2 Galaxies Gas Cluster", "HB 2 Galaxies Gas DoubleBroad", "HB 2 Galaxies Positive",
              "HB 2 Galaxy Spirals Test", "HB 2 Moon Singularity", "HB 2 Moons Crater Test C", "HB 2 Moons Test Circ",
              "HB 2 planetoid smooth", "HB 2 planetoids cloudy", "HB 2 planetoids", "HB 2moons-craters", "HB Galaxy 2 Gas Clouds",
              "HB Galaxy1", "HB Galaxy1a", "HB Galaxy2moontest", "HB Moon Generator", "HB Moon Surface Generator", "  HB Moon Zoom Generator II",
              "HB Planet Generator", "HB Single Galaxy",

              "LU--Lumiere-", "LU--Lumiere-CurlyCross1", "LU--Lumiere-HyLog", "LU--Lumiere-POL", "LU-Choose-Lumiere-", "LU-ChooseJS-Lumiere-",
              "LU-ChooseJS2-Lumiere-", "LU-Epi-Lumiere-", "LU-EpipluPAX-Lumiere-", "LU-Epiplus-Lumiere-",  "LU-Epipluspoly-Lumiere-",
              "LU-JULF-Lumiere-", "LU-JULFLOW-Lumiere-", "LU-Mob-Lumiere-", "LU-RandFinal-Mirror-Lumiere-", "LU-Rays-Lumiere-",
              "LU-SPHERE-3D-PIF-Mirror-Lumiere-", "LU-SPHEREPIF-Lumiere-", "LU-SPHEREPIF-Mirror-Lumiere-", "LU-SPHERF-Lumiere-", "LU-TanEpi-Lumiere-", "LU-TanF-Lumiere-",

              "SX 00 I am Ace  in space", "SX 00 I am Ace  in space2", "SX 00 I am Ace  in space3", "SX 00 I am Julia in space pitch",
              "SX 00 I am Julia in space", "SX 00 I am Julia", "SX 00 I am RanJulia in space", "SX 00 I am RanJulia",
              "SX 0 I am Lovely", "SX 1 I am Peaceful", "SX 1 I am PeacefulRC", "SX 2 I make you think", "SX 3 I need printing",
              "SX 4 I need love", "SX 5 I need patience", "SX 6 I need Tweaks", "SX 7 I make you wonder", "SX hole2Split",
              "SX jispostz", "SX LoonieSplit", "SX octagon2Split", "SX rings2Split", "SX squarize2Split", "SX tancos2Split",

              "Wedge_Sph_Marble",

              "YU-", "YU--Choose", "YU--cot", "YU--coth", "YU--cothtest", "YU--csc",
              "YU--csch", "YU--curl", "YU--curlZO", "YU--foci", "YU--ho", "YU--invpolar",
              "YU--log", "YU--secant", "YU--spiralwing", "YU--tancos", "YU-Yugen"

      };
      List<String> resLst = Arrays.asList(ressources);
      Collections.sort(resLst);
      ressources = resLst.toArray(new String[]{});

      // for the base path inside the jar file
      RGBPaletteReader reader = new Flam3GradientReader();
      DefaultMutableTreeNode defaultFolderNode = null;
      for (String ressource : ressources) {
        try {
          String resFilename = "scripts/" + ressource + "." + Tools.FILEEXT_JWFSCRIPT;
          InputStream is = reader.getClass().getResourceAsStream(resFilename);
          if (is != null) {
            is.close();
            JWFScriptInternalNode node = new JWFScriptInternalNode(ressource, resFilename);

            if (defaultFolderNode == null) {
              defaultFolderNode = new JWFScriptFolderNode("Built-in scripts (read-only)", null, false);
              root.add(defaultFolderNode);
            }
            defaultFolderNode.add(node);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    // External flames
    {
      String baseDrawer = Prefs.getPrefs().getTinaJWFScriptPath();
      if (baseDrawer == null || baseDrawer.equals("") || baseDrawer.equals(".") || baseDrawer.equals("/") || baseDrawer.equals("\\")) {
        root.add(new InvalidScriptFolderNode());
      } else {
        if(new File(baseDrawer).exists()) {
          FileDialogTools.ensureFileAccess(new java.awt.Frame(), scriptTree, baseDrawer);
        }
        DefaultMutableTreeNode userScriptsRootNode;
        DefaultMutableTreeNode parentNode = userScriptsRootNode = new JWFScriptFolderNode("Your scripts", baseDrawer, true);
        if(userScriptsRootNodeConsumer!=null) {
          userScriptsRootNodeConsumer.accept(userScriptsRootNode);
        }
        root.add(parentNode);
        scanUserScripts(baseDrawer, parentNode);
      }
    }
    scriptTree.setRootVisible(false);
    scriptTree.setModel(new DefaultTreeModel(root));
  }

  public static void scanUserScripts(String path, DefaultMutableTreeNode pParentNode) {
    File root = new File(path);
    File[] list = root.listFiles();
    if (list != null) {
      try {
        Arrays.sort(list, new Comparator<File>() {

          @Override
          public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
          }

        });
      } catch (Exception ex) {
        // ex.printStackTrace();
      }
      for (File f : list) {
        if (f.isDirectory()) {
          DefaultMutableTreeNode newParentNode = new JWFScriptFolderNode(f.getName(), f.getAbsolutePath(), true);
          pParentNode.add(newParentNode);
          scanUserScripts(f.getAbsolutePath(), newParentNode);
        } else {
          String filename = f.getAbsolutePath();
          String lcFilename = filename.toLowerCase();
          if (lcFilename.length() != filename.length()) {
            lcFilename = filename;
          }
          int pos = lcFilename.lastIndexOf("." + Tools.FILEEXT_JWFSCRIPT.toLowerCase());
          if (pos > 0 && pos == filename.length() - Tools.FILEEXT_JWFSCRIPT.length() - 1) {
            String caption = f.getName().substring(0, f.getName().length() - Tools.FILEEXT_JWFSCRIPT.length() - 1);
            JWFScriptUserNode node = new JWFScriptUserNode(caption, filename);
            pParentNode.add(node);
          }
        }
      }
    }
  }

}
