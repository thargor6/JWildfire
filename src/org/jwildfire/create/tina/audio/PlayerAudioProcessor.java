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

public class PlayerAudioProcessor implements AudioProcessor {
  private byte[] byteBuf = new byte[4096];
  private JWFAudioDevice audioDevice;
  private boolean muted = false;

  @Override
  public void process(short[] samples, int offs, int len) {
    byte[] b = toByteArray(samples, offs, len);
    if (isMuted()) {
      byte[] blank = new byte[b.length];
      audioDevice.getSource().write(blank, 0, len * 2);
    }
    else {
      audioDevice.getSource().write(b, 0, len * 2);
    }
  }

  @Override
  public void init(JWFAudioDevice pJWFAudioDevice) {
    audioDevice = pJWFAudioDevice;
  }

  private byte[] getByteArray(int length) {
    if (byteBuf.length < length) {
      byteBuf = new byte[length + 1024];
    }
    return byteBuf;
  }

  protected byte[] toByteArray(short[] samples, int offs, int len) {
    byte[] b = getByteArray(len * 2);
    int idx = 0;
    short s;
    while (len-- > 0) {
      s = samples[offs++];
      b[idx++] = (byte) s;
      b[idx++] = (byte) (s >>> 8);
    }
    return b;
  }

  @Override
  public void finish() {
    // no op
  }

  public boolean isMuted() {
    return muted;
  }

  public void setMuted(boolean muted) {
    this.muted = muted;
  }
}
