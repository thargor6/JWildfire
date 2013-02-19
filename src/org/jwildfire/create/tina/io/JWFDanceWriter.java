/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import org.jwildfire.create.tina.dance.DancingFlameProject;

public class JWFDanceWriter {

  public void writeProject(DancingFlameProject pProject, String pFilename) {
    try {
      ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pFilename)));
      try {
        // save header
        JWFDanceFileHeader header = new JWFDanceFileHeader();
        outputStream.writeObject(header);
        // save data
        outputStream.writeObject(pProject.getFlames());
        outputStream.writeObject(pProject.getMotions());
        outputStream.writeObject(pProject.getSoundFilename());
        outputStream.writeObject(pProject.getFFT());
      }
      finally {
        outputStream.flush();
        outputStream.close();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      try {
        new File(pFilename).delete();
      }
      catch (Exception ex2) {

      }
      throw new RuntimeException(ex);
    }
  }

}
