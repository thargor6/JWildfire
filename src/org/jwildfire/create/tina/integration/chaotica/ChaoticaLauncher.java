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
package org.jwildfire.create.tina.integration.chaotica;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.launcher.StreamRedirector;

public class ChaoticaLauncher {

  public void launchChaotica(Flame pFlame) throws Exception {
    String xml = new ChaosFlameWriter(pFlame).getFlameXML();

    storeToClipboard(xml);

    File tempFile = storeToTempFile(pFlame, xml);

    File chaoticaExe = getChaoticaExecutable();

    launchChaotica(chaoticaExe, tempFile);
  }

  private void launchChaotica(File chaoticaExe, File tempFile) throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    String exePath = chaoticaExe.getAbsolutePath();
    if (exePath.contains(" ")) {
      exePath = "\"" + exePath + "\"";
    }
    String flamePath = tempFile.getAbsolutePath();
    if (flamePath.contains(" ")) {
      flamePath = "\"" + flamePath + "\"";
    }
    //    int retVal = launchSync(new String[] { exePath, flamePath }, os);
    //    if (retVal != 0) {
    //      throwError(new String(os.toByteArray()) + "\nReturn code was " + retVal + " - see below for details:");
    //    }
    launchASync(new String[] { exePath, flamePath }, os);
  }

  //  private int launchSync(String pCmd[], OutputStream pOS) throws Exception {
  //    Runtime runtime = Runtime.getRuntime();
  //
  //    Process proc;
  //    try {
  //      proc = runtime.exec(pCmd);
  //    }
  //    catch (Exception ex) {
  //      throw new RuntimeException(ex);
  //    }
  //
  //    StreamRedirector outputStreamHandler = new StreamRedirector(proc.getInputStream(), pOS, false);
  //    StreamRedirector errorStreamHandler = new StreamRedirector(proc.getErrorStream(), pOS, false);
  //    errorStreamHandler.start();
  //    outputStreamHandler.start();
  //    int exitVal = proc.waitFor();
  //    return exitVal;
  //  }

  private void launchASync(String pCmd[], OutputStream pOS) throws Exception {
    Runtime runtime = Runtime.getRuntime();

    Process proc;
    try {
      proc = runtime.exec(pCmd);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }

    StreamRedirector outputStreamHandler = new StreamRedirector(proc.getInputStream(), pOS, false);
    StreamRedirector errorStreamHandler = new StreamRedirector(proc.getErrorStream(), pOS, false);
    errorStreamHandler.start();
    outputStreamHandler.start();
  }

  private File storeToTempFile(Flame pFlame, String xml) throws Exception {
    File tempDrawer = getTempFlamePath();
    String name = pFlame.getName();
    if (name == null || name.length() == 0) {
      name = "JWF_" + System.currentTimeMillis();
    }
    name = name.replaceAll(" :\\/", "_") + "." + Tools.FILEEXT_CHAOS;
    File flameFile = new File(tempDrawer, name);
    if (flameFile.exists()) {
      if (!flameFile.delete()) {
        throwError("Could not delete file \"" + flameFile.getAbsolutePath() + "\"");
      }
    }
    Tools.writeUTF8Textfile(flameFile.getAbsolutePath(), xml);
    if (!flameFile.exists()) {
      throwError("Could not create file \"" + flameFile.getAbsolutePath() + "\"");
    }
    return flameFile;
  }

  private void storeToClipboard(String xml) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection data = new StringSelection(xml);
    clipboard.setContents(data, data);
  }

  private File getTempFlamePath() throws Exception {
    String path = Prefs.getPrefs().getTinaIntegrationChaoticaFlameDrawer();
    if (path == null || path.length() == 0) {
      throwError("In order to export a flame directly to Chaotica you must configure a temporary drawer in Preferences-window (see property \"tinaIntegrationChaoticaFlameDrawer\")");
    }
    File file = new File(path);
    if (!file.exists() || !file.isDirectory()) {
      if (!file.mkdirs()) {
        throwError("Could not access or create the temporary flame-drawer, check your settings in the Preferences-window (see property \"tinaIntegrationChaoticaFlameDrawer\")");
      }
    }
    return file;
  }

  private File getChaoticaPath() throws Exception {
    String path = Prefs.getPrefs().getTinaIntegrationChaoticaDrawer();
    if (path == null || path.length() == 0) {
      throwError("In order to export a flame directly to Chaotica you must configure the Chaotica installation-path in Preferences-window (see property \"tinaIntegrationChaoticaDrawer\")");
    }
    File file = new File(path);
    if (!file.exists()) {
      throwError("Could not find the Chaotica-installation-path, check your settings in the Preferences-window (see property \"tinaIntegrationChaoticaDrawer\")");
    }
    return file;
  }

  private File getChaoticaExecutable() throws Exception {
    String exe = Prefs.getPrefs().getTinaIntegrationChaoticaExecutable();
    if (exe == null || exe.length() == 0) {
      throwError("In order to export a flame directly to Chaotica you must configure the name of Chaotica-executable in the Preferences-window (see property \"tinaIntegrationChaoticaExecutable\")");
    }
    File file = new File(getChaoticaPath(), exe);
    if (!file.exists()) {
      throwError("Could not find the Chaotica-executable, check your settings in the Preferences-window (see property \"tinaIntegrationChaoticaExecutable\")");
    }
    return file;
  }

  private void throwError(String pMessage) throws Exception {
    throw new Exception(pMessage + "\nPlease note that the params were also put to the clipbaord, you may paste them manually into Chaotica.");
  }
}
