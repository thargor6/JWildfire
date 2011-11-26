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
package org.jwildfire.swing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jwildfire.swing.Buffer.BufferType;

public class StaticBufferList {
  private static List<Buffer> buffers = new ArrayList<Buffer>();

  public static boolean add(Buffer pBuffer) {
    return buffers.add(pBuffer);
  }

  public static boolean addAll(Collection<? extends Buffer> pBuffers) {
    return buffers.addAll(pBuffers);
  }

  public static void clear() {
    buffers.clear();
  }

  public static boolean remove(Object pObject) {
    return buffers.remove(pObject);
  }

  public static int size() {
    return buffers.size();
  }

  public static boolean removeAll(Collection<?> pCollection) {
    return buffers.removeAll(pCollection);
  }

  public static List<Buffer> getBufferList(BufferType pBufferType) {
    List<Buffer> res = new ArrayList<Buffer>();
    for (Buffer buffer : buffers) {
      if (buffer.getBufferType() == pBufferType) {
        res.add(buffer);
      }
    }
    return res;
  }

}
