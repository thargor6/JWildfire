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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.swing.TinaControllerContextService;

import java.util.UUID;

/**
 * CLI for generating random flames
 */
public class RandomFlameGenerator {
  private final CommandLine cmd;

  public RandomFlameGenerator(String[] args) throws Exception {
    Options options = new Options();
    try {
      RenderOptionsParser.addOptions(options);
      OptionsParserUtil.addOption(options, CliOptions.BC);
      OptionsParserUtil.addOption(options, CliOptions.RGFLAME);
      OptionsParserUtil.addOption(options, CliOptions.RGGRAD);
      OptionsParserUtil.addOption(options, CliOptions.RGSYMM);
      OptionsParserUtil.addOption(options, CliOptions.RGWFIELD);
      OptionsParserUtil.addOption(options, CliOptions.LRGFLAME);
      OptionsParserUtil.addOption(options, CliOptions.LRGGRAD);
      OptionsParserUtil.addOption(options, CliOptions.LRGSYMM);
      OptionsParserUtil.addOption(options, CliOptions.LRGWFIELD);
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
    if(OptionsParserUtil.getSwitchValue(cmd, CliOptions.LRGFLAME)) {
      listRandomFlameGenerators();
    }
    else if(OptionsParserUtil.getSwitchValue(cmd, CliOptions.LRGGRAD)) {
      listRandomGradientGenerators();
    }
    else if(OptionsParserUtil.getSwitchValue(cmd, CliOptions.LRGSYMM)) {
      listRandomSymmetryGenerators();
    }
    else if(OptionsParserUtil.getSwitchValue(cmd, CliOptions.LRGWFIELD)) {
      listRandomWFieldGenerators();
    }
    else {
      generateRandomFlames();
    }
  }

  private void listRandomFlameGenerators() {
    System.out.println("###########################################################");
    System.out.println(String.format("  AVAILABLE RANDOM FLAME GENERATORS: %d",  RandomFlameGeneratorList.getNameList().size()));
    RandomFlameGeneratorList.getNameList().forEach( n-> System.err.println( "    " + n));
    System.out.println("###########################################################");
  }

  private void listRandomGradientGenerators() {
    System.out.println("###########################################################");
    System.out.println(String.format("  AVAILABLE RANDOM GRADIENT GENERATORS: %d", RandomGradientGeneratorList.getNameList().size()));
    RandomGradientGeneratorList.getNameList().forEach( n-> System.err.println( "    " + n));
    System.out.println("###########################################################");
  }

  private void listRandomSymmetryGenerators() {
    System.out.println("###########################################################");
    System.out.println(String.format("  AVAILABLE RANDOM SYMMETRY GENERATORS: %d", RandomSymmetryGeneratorList.getNameList().size()));
    RandomSymmetryGeneratorList.getNameList().forEach( n-> System.err.println( "    " + n));
    System.out.println("###########################################################");
  }

  private void listRandomWFieldGenerators() {
    System.out.println("###########################################################");
    System.out.println(String.format("  AVAILABLE RANDOM WEIGHTING FIELD GENERATORS: %d", RandomWeightingFieldGeneratorList.getNameList().size()));
    RandomWeightingFieldGeneratorList.getNameList().forEach( n-> System.err.println( "    " + n));
    System.out.println("###########################################################");
  }

  private void generateRandomFlames() {
    try {
      long t0 = System.currentTimeMillis();
      RenderOptions renderOptions = RenderOptionsParser.parseOptions(cmd);
      renderOptions.validate();
      if(renderOptions.isUseGPU()) {
        TinaControllerContextService.getContext().setGpuMode(true);
      }
      int batchCount = OptionsParserUtil.getOptionValue(cmd, CliOptions.BC, 1);
      String randGenFlameName = OptionsParserUtil.getOptionValue(cmd, CliOptions.RGFLAME, RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME);
      String randGenGradientName = OptionsParserUtil.getOptionValue(cmd, CliOptions.RGGRAD, RandomGradientGeneratorList.DEFAULT_GENERATOR_NAME);
      String randGenSymmetryName = OptionsParserUtil.getOptionValue(cmd, CliOptions.RGSYMM, RandomSymmetryGeneratorList.DEFAULT_GENERATOR_NAME);
      String randGenWFieldName = OptionsParserUtil.getOptionValue(cmd, CliOptions.RGWFIELD, RandomWeightingFieldGeneratorList.DEFAULT_GENERATOR_NAME);
      for (int i = 1; i <= batchCount; i++) {
        System.out.println(String.format("  Creating random flame %d of %d...", i, batchCount));
        Flame flame = CliUtils.createRandomFlame(randGenFlameName, randGenGradientName, randGenSymmetryName, randGenWFieldName);
        String flameId = UUID.randomUUID().toString();
        String flameFilename = flameId + ".flame";
        new FlameWriter().writeFlame(flame, flameFilename);
        CliUtils.renderFlame(flame, renderOptions, flameFilename);
      }

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
     new RandomFlameGenerator(args).execute();
  }

}
