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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import org.jwildfire.create.tina.audio.RecordedFFT;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.DancingFlameProject;
import org.jwildfire.create.tina.dance.motion.Motion;

public class JWFDanceReader {

  public DancingFlameProject readProject(String pFilename) {
    try {
      ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pFilename)));
      try {
        // read header
        @SuppressWarnings("unused")
        JWFDanceFileHeader header = (JWFDanceFileHeader) in.readObject();
        // read data
        DancingFlameProject project = new DancingFlameProject();
        @SuppressWarnings("unchecked")
        List<Flame> flames = (List<Flame>) in.readObject();
        @SuppressWarnings("unchecked")
        List<Motion> motions = (List<Motion>) in.readObject();
        String soundFilename = (String) in.readObject();
        RecordedFFT fft = (RecordedFFT) in.readObject();
        project.getFlames().addAll(flames);
        project.getMotions().addAll(motions);
        project.setSoundData(soundFilename, fft);
        return project;
      }
      finally {
        in.close();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }
}
