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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class RenderOptionsParser {

  public static void addOptions(Options options) {
    OptionsParserUtil.addOption(options, CliOptions.W);
    OptionsParserUtil.addOption(options, CliOptions.H);
    OptionsParserUtil.addOption(options, CliOptions.Q);
    OptionsParserUtil.addOption(options, CliOptions.DN);
    OptionsParserUtil.addOption(options, CliOptions.FDN);
    OptionsParserUtil.addOption(options, CliOptions.GPU);
  }

  public static RenderOptions parseOptions(CommandLine cmd) {
    RenderOptions renderOptions = new RenderOptions();
    renderOptions.setRenderWidth(OptionsParserUtil.getOptionValue(cmd, CliOptions.W, renderOptions.getRenderWidth()));
    renderOptions.setRenderHeight(OptionsParserUtil.getOptionValue(cmd, CliOptions.H, renderOptions.getRenderHeight()));
    renderOptions.setRenderQuality(OptionsParserUtil.getOptionValue(cmd, CliOptions.Q, renderOptions.getRenderQuality()));
    renderOptions.setAiPostDenoiserType(OptionsParserUtil.getOptionValue(cmd, CliOptions.DN, renderOptions.getAiPostDenoiserType()));
    renderOptions.setForceAiPostDenoiser(OptionsParserUtil.getSwitchValue(cmd, CliOptions.FDN));
    renderOptions.setUseGPU(OptionsParserUtil.getSwitchValue(cmd, CliOptions.GPU));
    return renderOptions;
  }
}
