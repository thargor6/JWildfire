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
package org.jwildfire.create.tina.render.denoiser;

import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.image.SimpleImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AIPostDenoiserFactory {
  private static final Map<AIPostDenoiserType, Boolean> selfTestState = new HashMap<>();

  private AIPostDenoiserFactory() {
    // empty
  }

  public static boolean isAvailable(AIPostDenoiserType denoiserType) {
    Boolean selfTestResult = selfTestState.get(denoiserType);
    if (selfTestResult == null) {
      try {
        selfTestResult = Boolean.valueOf(getDenoiserInstance(denoiserType).performSelfTests());
      } catch (Exception ex) {
        ex.printStackTrace();
        selfTestResult = Boolean.FALSE;
      }
      selfTestState.put(denoiserType, selfTestResult);
    }
    return selfTestResult;
  }

  public static AIPostDenoiser getDenoiserInstance(AIPostDenoiserType denoiserType) {
    switch (denoiserType) {
      case NONE:
        return new EmptyAIPostDenoiser();
      case OIDN:
        return new OIDNCmdLineAIPostDenoiser();
      case OPTIX:
        return new OptixCmdLineAIPostDenoiser();
      default:
        throw new RuntimeException("Unknown denoiser type <" + denoiserType + ">");
    }
  }

  public static AIPostDenoiserType getBestAvailableDenoiserType(AIPostDenoiserType reference) {
    if (reference != null) {
      switch (reference) {
        case OPTIX:
          if (isAvailable(AIPostDenoiserType.OPTIX)) {
            return AIPostDenoiserType.OPTIX;
          } else if (isAvailable(AIPostDenoiserType.OIDN)) {
            return AIPostDenoiserType.OIDN;
          }
          break;
        case OIDN:
          if (isAvailable(AIPostDenoiserType.OIDN)) {
            return AIPostDenoiserType.OIDN;
          }
          break;
      }
    }
    return AIPostDenoiserType.NONE;
  }

  public static boolean denoiseImage(String filename, AIPostDenoiserType denoiserType, double denoiserBlend) {
    AIPostDenoiserType postDenoiser = AIPostDenoiserFactory.getBestAvailableDenoiserType(denoiserType);
    if(!AIPostDenoiserType.NONE.equals(postDenoiser)) {
      String outputFilename = AIPostDenoiserFactory.getDenoiserInstance(postDenoiser).denoise(filename, denoiserBlend);
      File outputFile = new File(outputFilename);
      if(outputFile.exists()) {
        File inputFile = new File(filename);
        inputFile.delete();
        if(inputFile.exists()) {
          throw new RuntimeException("Could not delete file <" + filename +">");
        }
        if(!outputFile.renameTo(inputFile)) {
          throw new RuntimeException("Could not rename file <" + outputFilename +"> to <" + filename + ">");
        }

        return true;
      }
      else {
        throw new RuntimeException("Denoising failed");
      }
    }
    return false;
  }
}

