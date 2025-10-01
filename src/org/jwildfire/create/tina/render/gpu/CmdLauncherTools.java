/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2025 Andreas Maschke

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
package org.jwildfire.create.tina.render.gpu;

import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.jwildfire.launcher.StreamRedirector;

public class CmdLauncherTools {

  public static void launchSync(String[] pCmd) {
    Runtime runtime = Runtime.getRuntime();
    try {
      runtime.exec(pCmd);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static void launchAsyncNoWait(String[] pCmd, OutputStream pOS) {
    try {
      Runtime runtime = Runtime.getRuntime();
      Process proc = runtime.exec(pCmd);
      StreamRedirector outputStreamHandler = new StreamRedirector(proc.getInputStream(), pOS, false);
      StreamRedirector errorStreamHandler = new StreamRedirector(proc.getErrorStream(), pOS, false);
      errorStreamHandler.start();
      outputStreamHandler.start();
      if(!proc.isAlive()) {
        throw new RuntimeException("Swan process terminated immediately with exit code " + proc.exitValue());
      };
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static int launchAsync(String pCmd, OutputStream pOS) {
    try {
      Runtime runtime = Runtime.getRuntime();
      Process proc = runtime.exec(pCmd);

      StreamRedirector outputStreamHandler = new StreamRedirector(proc.getInputStream(), pOS, false);
      StreamRedirector errorStreamHandler = new StreamRedirector(proc.getErrorStream(), pOS, false);
      errorStreamHandler.start();
      outputStreamHandler.start();
      if(proc.waitFor(5, TimeUnit.MINUTES)) {
        return proc.exitValue();
      }
      else {
        return -1;
      }
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
