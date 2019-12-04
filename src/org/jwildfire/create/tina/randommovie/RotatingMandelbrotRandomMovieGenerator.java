/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.randommovie;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.FlameMovie;
import org.jwildfire.create.tina.animate.FlameMoviePart;
import org.jwildfire.create.tina.animate.GlobalScript;
import org.jwildfire.create.tina.animate.GlobalScriptType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.randomflame.MandelbrotRandomFlameGenerator;

public class RotatingMandelbrotRandomMovieGenerator extends RandomMovieGenerator {

  @Override
  protected FlameMovie prepareMovie(Prefs pPrefs) {
    FlameMovie movie = new FlameMovie(pPrefs);
    Flame flame = genRandomFlame(new MandelbrotRandomFlameGenerator(), pPrefs);
    flame.setCamPerspective(0.1 + 0.2 * Math.random());
    if (Math.random() < 0.5) {
      flame.setCamDOF(0.05 + 0.1 * Math.random());
    }

    FlameMoviePart part = new FlameMoviePart();
    part.setFlame(flame);
    part.setFrameCount(320);
    part.setFrameMorphCount(0);
    movie.addPart(part);
    {
      double amplitude = -0.75 * Math.random() * 1.5;
      movie.getGlobalScripts()[0] = (new GlobalScript(GlobalScriptType.ROTATE_YAW, amplitude));
    }

    {
      double amplitude = -0.25 * Math.random() * 0.5;
      movie.getGlobalScripts()[1] = new GlobalScript(GlobalScriptType.ROTATE_ROLL, amplitude);
    }

    {
      double amplitude = -0.125 * Math.random() * 0.25;
      movie.getGlobalScripts()[2] = new GlobalScript(GlobalScriptType.ROTATE_PITCH, amplitude);
    }

    {
      double amplitude = -0.75 * Math.random() * 1.5;
      movie.getGlobalScripts()[0] = (new GlobalScript(GlobalScriptType.ROTATE_BANK, amplitude));
    }

    {
      double amplitude = 0.2 + Math.random() * 0.6;
      movie.getGlobalScripts()[2] = new GlobalScript(GlobalScriptType.MOVE_CAM_Z, amplitude);
    }
    {
      double amplitude = 0.2 + Math.random() * 1.4;
      movie.getGlobalScripts()[2] = new GlobalScript(GlobalScriptType.MOVE_CAM_Y, amplitude);
    }

    return movie;
  }

  @Override
  public String getName() {
    return "Rotating Mandelbrots";
  }

}
