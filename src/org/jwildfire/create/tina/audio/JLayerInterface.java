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
package org.jwildfire.create.tina.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class JLayerInterface {
  private boolean playing;
  private Player player;
  private PlayerAudioProcessor processor;
  private JWFAudioDevice audioDevice;
  private boolean muted;

  public void stop() throws Exception {
    if (playing) {
      if (player != null) {
        try {
          player.close();
        }
        catch (Exception ex) {
          ex.printStackTrace();
          player = null;
        }
      }
      playing = false;
    }
  }

  public RecordedFFT recordFFT(String pFilename) throws Exception {
    FileInputStream fin = new FileInputStream(pFilename);
    BufferedInputStream bin = new BufferedInputStream(fin);
    FFTRecordingAudioProcessor processor = new FFTRecordingAudioProcessor();
    JWFAudioDevice audioDevice = new JWFAudioDevice(processor);
    Player player = new Player(bin, audioDevice);
    try {
      player.play();
    }
    finally {
      processor.finish();
    }
    return processor.getFFT();
  }

  public void play(String pFilename) throws Exception {
    stop();
    FileInputStream fin = new FileInputStream(pFilename);
    BufferedInputStream bin = new BufferedInputStream(fin);
    processor = new PlayerAudioProcessor();
    processor.setMuted(muted);
    audioDevice = new JWFAudioDevice(processor);
    audioDevice.close();
    player = new Player(bin, audioDevice);
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          playing = true;
          try {
            player.play();
          }
          finally {
            processor.finish();
          }
        }
        catch (JavaLayerException e) {
          e.printStackTrace();
          playing = false;
        }
      }
    }).start();
  }

  public long getPosition() {
    return audioDevice != null ? audioDevice.getFramePosition() : 0;
  }

  public void setMuted(boolean pMuted) {
    muted = pMuted;
    if (processor != null)
      processor.setMuted(pMuted);
  }
}
