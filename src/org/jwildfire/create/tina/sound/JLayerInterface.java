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
package org.jwildfire.create.tina.sound;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

public class JLayerInterface {
  private boolean playing;
  private BufferedInputStream bin;
  private FileInputStream fin;
  private AudioDevice _dev;
  private Player player;

  public void stop() throws Exception {
    if (playing) {
      if (player != null) {
        player.close();
        player = null;
      }
      if (bin != null) {
        bin.close();
        bin = null;
      }
      if (fin != null) {
        fin.close();
        fin = null;
      }
      playing = false;
    }
  }

  private AudioDevice createAudioDevice() throws Exception {
    if (_dev == null)
      _dev = FactoryRegistry.systemRegistry().createAudioDevice();
    return _dev;
  }

  public void play(String pFilename) throws Exception {
    stop();
    fin = new FileInputStream(pFilename);
    bin = new BufferedInputStream(fin);
    final Player player = new Player(bin, createAudioDevice());
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          playing = true;
          player.play();
        }
        catch (JavaLayerException e) {
          e.printStackTrace();
          playing = false;
        }
      }
    }).start();
  }
}
