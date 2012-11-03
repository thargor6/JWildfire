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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;

import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDeviceBase;

public class JWFAudioDevice extends AudioDeviceBase {
  private final AudioProcessor audioProcessor;
  private SourceDataLine source = null;
  private AudioFormat audioFormat = null;

  private int freq;
  private int channels;
  private int samplesPerSecond;

  public JWFAudioDevice(AudioProcessor pAudioProcessor) {
    audioProcessor = pAudioProcessor;
  }

  protected AudioFormat getAudioFormat() {
    if (audioFormat == null) {
      Decoder decoder = getDecoder();
      audioFormat = new AudioFormat(decoder.getOutputFrequency(),
          16,
          decoder.getOutputChannels(),
          true,
          false);
    }
    return audioFormat;
  }

  protected DataLine.Info getSourceLineInfo() {
    AudioFormat fmt = getAudioFormat();
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
    return info;
  }

  protected void createSource() throws JavaLayerException {
    Throwable t = null;
    try {
      Line line = AudioSystem.getLine(getSourceLineInfo());
      if (line instanceof SourceDataLine) {
        source = (SourceDataLine) line;
        source.open(audioFormat);
        source.start();
      }
    }
    catch (Throwable ex) {
      t = ex;
    }
    if (source == null)
      throw new JavaLayerException("Cannot obtain source audio line", t);
  }

  protected void closeImpl() {
    if (source != null) {
      source.close();
    }
  }

  protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
    if (source == null) {
      createSource();
      channels = getDecoder().getOutputChannels();
      freq = getDecoder().getOutputFrequency();
      samplesPerSecond = (freq * channels);
      audioProcessor.init(this);
    }
    audioProcessor.process(samples, offs, len);
  }

  @Override
  protected void flushImpl() {
    if (source != null) {
      source.drain();
    }
  }

  public long getFramePosition() {
    return (source != null) ? source.getLongFramePosition() : 0;
  }

  public int getSamplesPerSecond() {
    return samplesPerSecond;
  }

  public SourceDataLine getSource() {
    return source;
  }

  @Override
  public int getPosition() {
    return source != null ? source.getFramePosition() : 0;
  }

  public int getChannelCount() {
    return source != null ? getDecoder().getOutputChannels() : 0;
  }

}
