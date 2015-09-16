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

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.script.swing.JWFScriptController.ScriptNode;

public class JWFScriptUserNode extends DefaultMutableTreeNode implements ScriptNode {
  private static final long serialVersionUID = 1L;
  private String caption;
  private final String filename;
  private String script;
  private String description;

  public JWFScriptUserNode(String pCaption, String pFilename) {
    super(pCaption, false);
    filename = pFilename;
    caption = pCaption;
  }

  @Override
  public String getScript() throws Exception {
    if (script == null) {
      script = Tools.readUTF8Textfile(filename);
    }
    return script;
  }

  private String getDescFilename(String pFilename) {
    return pFilename.substring(0, pFilename.length() - Tools.FILEEXT_JWFSCRIPT.length()) + Tools.FILEEXT_TXT;
  }

  @Override
  public String getDescription() throws Exception {
    if (description == null) {
      String descFilename = getDescFilename(filename);
      if (new File(descFilename).exists()) {
        description = Tools.readUTF8Textfile(descFilename);
      }
      else {
        description = "";
      }
    }
    return description;
  }

  public void saveScript(String pScript, String pDescription) throws Exception {
    if (pScript == null) {
      pScript = "";
    }
    Tools.writeUTF8Textfile(filename, pScript);
    script = pScript;

    if (pDescription == null) {
      pDescription = "";
    }
    Tools.writeUTF8Textfile(getDescFilename(filename), pDescription);
    description = pDescription;
  }

  public void deleteScript() {
    new File(filename).delete();
    String descFilename = getDescFilename(filename);
    File descFile = new File(descFilename);
    if (descFile.exists()) {
      descFile.delete();
    }
  }

  public void rename(String pNewName) throws Exception {
    File file = new File(filename);
    String scriptFilename = file.getParentFile().getAbsolutePath() + File.separator + pNewName + "." + Tools.FILEEXT_JWFSCRIPT;
    File scriptFile = new File(scriptFilename);
    if (scriptFile.exists()) {
      throw new Exception("File <" + scriptFile.getAbsolutePath() + "> already exists");
    }

    String descFilename = file.getParentFile().getAbsolutePath() + File.separator + pNewName + "." + Tools.FILEEXT_TXT;
    File descFile = new File(descFilename);
    if (descFile.exists()) {
      throw new Exception("File <" + descFile.getAbsolutePath() + "> already exists");
    }

    getDescription();
    String oldFilename = filename;
    Tools.writeUTF8Textfile(scriptFilename, getScript());
    try {
      Tools.writeUTF8Textfile(descFilename, getDescription());
    }
    catch (Exception ex) {
      new File(scriptFilename).delete();
      throw ex;
    }

    new File(oldFilename).delete();
    new File(getDescFilename(oldFilename)).delete();

    setCaption(pNewName);
  }

  private void setCaption(String pNewName) {
    caption = pNewName;
    setUserObject(pNewName);
  }

  @Override
  public String getCaption() {
    return caption;
  }

  public String getFilename() {
    return filename;
  }
}