/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class StreamRedirector extends Thread {
  private final InputStream is;
  private final OutputStream os;
  private final boolean copyToOut;

  public StreamRedirector(InputStream pIs, OutputStream pOs, boolean pCopyToOut) {
    is = pIs;
    os = pOs;
    copyToOut = pCopyToOut;
  }

  public void run() {
    try {
      PrintWriter pw = null;
      try {
        if (os != null) {
          pw = new PrintWriter(os);
        }
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        while ((line = br.readLine()) != null) {
          if (pw != null) {
            pw.println(line);
          }
          if (copyToOut) {
            System.out.println(line);
          }
        }
      }
      finally {
        if (pw != null) {
          pw.flush();
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
