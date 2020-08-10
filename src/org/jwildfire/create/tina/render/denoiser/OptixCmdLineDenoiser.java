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

import org.jwildfire.base.Tools;
import org.jwildfire.create.GradientCreator;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.transform.ComposeTransformer;
import org.jwildfire.transform.CropTransformer;
import org.jwildfire.transform.RectangleTransformer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class OptixCmdLineDenoiser extends CmdLineDenoiser {
  private static boolean selfTestResult = false;
  private static boolean selfTestExecuted = false;

  // TODO remove
  public static boolean isAvailableStatic() {
    return new OptixCmdLineDenoiser().isAvailable();
  }

  @Override
  protected String[] getDenoiserCmd(String denoiserPath, String inputFilename, String outputFilename, double blend) {
    return new String[]{String.format("\"%s\\OIDNDenoiser.exe\"", denoiserPath), "-i", String.format("\"%s\"", inputFilename.replace('\\','/')), "-o", String.format("\"%s\"", outputFilename.replace('\\','/')), "-b", String.format("%s", Tools.doubleToString(blend))};
  }

  @Override
  public boolean isAvailable() {
    if(!selfTestExecuted) {
      selfTestResult = new OptixCmdLineDenoiser().performSelfTests();
      selfTestExecuted = true;
    }
    return selfTestResult;
  }

}
