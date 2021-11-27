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
import org.jwildfire.base.Tools;

/** Util for parsing the options inside the JWildfire CLI */

public class OptionsParserUtil {

  public static void addOption(Options options, CliOptions option) {
    options.addOption(option.getShortName(), option.getLongName(), option.isHasArg(), option.getDescription());
  }

  public static int getOptionValue(CommandLine cmd, CliOptions option, int defaultValue) {
    return Integer.parseInt(cmd.getOptionValue(option.getShortName(), String.valueOf(defaultValue)));
  }

  public static double getOptionValue(CommandLine cmd, CliOptions option, double defaultValue) {
    return Tools.stringToDouble(cmd.getOptionValue(option.getShortName(), Tools.doubleToString(defaultValue)));
  }

  public static String getOptionValue(CommandLine cmd, CliOptions option, String defaultValue) {
    return cmd.getOptionValue(option.getShortName(), defaultValue);
  }

  public static boolean getSwitchValue(CommandLine cmd, CliOptions option) {
    return cmd.hasOption(option.getShortName());
  }

}
