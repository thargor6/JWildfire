/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

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

package org.jwildfire.cli;

import org.apache.commons.cli.*;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.AllRandomGradientGenerator;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.render.filter.FilteringType;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;

import java.util.UUID;

/**
 * CLI for rendering flames (replaces the old HeadlessBatchRendererController)
 */

public class FlameRenderer {
  private final CommandLine cmd;

  public FlameRenderer(String[] args) throws Exception {
    Options options = new Options();
    try {
      RenderOptionsParser.addOptions(options);
      OptionsParserUtil.addOption(options, CliOptions.F);
      CommandLineParser parser = new DefaultParser();
      cmd = parser.parse( options, args);
    }
    catch( ParseException ex ) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(this.getClass().getSimpleName(), options);
      System.exit(1);
      throw ex;
    }
  }

  public void execute() {
    try {
      long t0 = System.currentTimeMillis();

      RenderOptions renderOptions = RenderOptionsParser.parseOptions(cmd);
      renderOptions.validate();
      String flamePath = OptionsParserUtil.getOptionValue(cmd, CliOptions.F, "");
      if(flamePath==null || flamePath.length()==0) {
        throw new RuntimeException("You must specify an input-flame-path");
      }
      Flame flame = new FlameReader(Prefs.getPrefs()).readFlames(flamePath).get(0);
      CliUtils.renderFlame(flame, renderOptions, flamePath);

      long t1 = System.currentTimeMillis();
      double elapsedTime = (t1 - t0) / 1000.0;
      System.out.println("###########################################################");
      System.out.println(String.format("  ELAPSED TIME: %f seconds", elapsedTime));
      System.out.println("###########################################################");
    }
    catch(Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
  }

  public static void main (String[] args) throws Exception {
     new FlameRenderer(args).execute();
  }

}
