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
package org.jwildfire.launcher;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;

public class AppLauncher {
  private final LauncherPrefs prefs;
  private static final String JWFILDFIRE_JAR = "j-wildfire.jar";
  private static final String JWILDFIRE_MAIN_CLASS = "org.jwildfire.swing.JWildfire";

  public AppLauncher(LauncherPrefs pPrefs) {
    prefs = pPrefs;
  }

  public String[] getLaunchCmd() throws Exception {
    String jdkPath = prefs.getJavaPath();
    if (jdkPath == null || jdkPath.length() == 0)
      throw new Exception("No JDK specified");
    if (jdkPath.charAt(jdkPath.length() - 1) == '\'' || jdkPath.charAt(jdkPath.length() - 1) == '/') {
      jdkPath = jdkPath.substring(0, jdkPath.length() - 1);
    }

    File java = new File(jdkPath + File.separator + "bin" + File.separator + "java");

    String minMemOption = "-Xms256m";
    String maxMemOption = "-Xmx" + prefs.getMaxMem() + "m";

    String classpath = File.separator + "lib" + File.separator + JWFILDFIRE_JAR;
    //URL launcherURL = ((URLClassLoader) this.getClass().getClassLoader()).getURLs()[0];
    //File currentDir = new File(launcherURL.toURI());

    File currentDir = new File(new File(".").getAbsolutePath());

    String javaCmd = java.getAbsolutePath();
    if (javaCmd.indexOf(" ") >= 0) {
      javaCmd = "\"" + javaCmd + "\"";
    }
    // MacOS
    if (javaCmd.indexOf("Internet Plug-Ins") > 0) {
      javaCmd = "java";
    }

    String cp = currentDir.getParentFile().getAbsolutePath() + classpath;
    if (cp.indexOf(" ") >= 0) {
      cp = "\"" + cp + "\"";
    }

    //String cmd = javaCmd + " " + options + " -cp " + cp + " " + JWILDFIRE_MAIN_CLASS;
    List<String> cmd = new ArrayList<String>();
    if (isWindows() && prefs.isLowPriority()) {
      cmd.add("cmd");
      cmd.add("/C");
      cmd.add("start");
      cmd.add("/low");
      cmd.add("/b");
      cmd.add("\"" + Tools.APP_TITLE + "\"");
    }

    cmd.add(javaCmd);
    cmd.add(minMemOption);
    cmd.add(maxMemOption);

    //if (prefs.getUiScale() > 1.0)
    cmd.add("-Dsun.java2d.uiScale=" + prefs.getUiScale() + "");

    if (isWindows()) {
      boolean is64bit = (System.getenv("ProgramFiles(x86)") != null);
      String libPath = new File(new File(currentDir.getParentFile().getAbsolutePath(), "lib"), (is64bit ? "x64" : "x86")).getAbsolutePath();
      String libraryPathOption;
      if (libPath.contains(" ")) {
        libraryPathOption = "\"-Djava.library.path=" + libPath + "\"";
      }
      else {
        libraryPathOption = "-Djava.library.path=" + libPath;
      }
      cmd.add(libraryPathOption);
    }

    cmd.add("-cp");
    cmd.add(cp);
    cmd.add(JWILDFIRE_MAIN_CLASS);
    return cmd.toArray(new String[cmd.size()]);
  }

  public static boolean isWindows() {
    String os = System.getProperty("os.name").toLowerCase();
    return os.startsWith("win");
  }

  public void launchSync(String[] pCmd) throws Exception {
    Runtime runtime = Runtime.getRuntime();
    try {
      runtime.exec(pCmd);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public int launchAsync(String pCmd[], OutputStream pOS) throws Exception {
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
    int exitVal = proc.waitFor();
    return exitVal;
  }
}
