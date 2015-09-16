/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

import java.io.InputStream;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.io.Flam3GradientReader;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.script.swing.JWFScriptController.ScriptNode;

public class JWFScriptInternalNode extends DefaultMutableTreeNode implements ScriptNode {
  private static final long serialVersionUID = 1L;
  private final String resFilename;
  private final String caption;
  private String script;
  private String description;

  public JWFScriptInternalNode(String pCaption, String pResFilename) {
    super(pCaption, false);
    caption = pCaption;
    resFilename = pResFilename;
  }

  @Override
  public String getScript() throws Exception {
    if (script == null) {
      // for the base path inside the jar file
      RGBPaletteReader reader = new Flam3GradientReader();
      InputStream is = reader.getClass().getResourceAsStream(resFilename);
      if (is != null) {
        script = Tools.readUTF8Textfile(is);
      }
      else {
        script = "";
      }
    }
    return script;
  }

  @Override
  public String getDescription() throws Exception {
    if (description == null) {
      // for the base path inside the jar file
      RGBPaletteReader reader = new Flam3GradientReader();
      String filename = resFilename.substring(0, resFilename.length() - Tools.FILEEXT_JWFSCRIPT.length()) + Tools.FILEEXT_TXT;
      InputStream is = reader.getClass().getResourceAsStream(filename);
      if (is != null) {
        description = Tools.readUTF8Textfile(is);
      }
      else {
        description = "";
      }
    }
    return description;
  }

  @Override
  public String getCaption() {
    return caption;
  }

  public String getResFilename() {
    return resFilename;
  }
}