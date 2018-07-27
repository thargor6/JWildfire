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
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.transform.Mesh3D;

public class BufferList implements Collection<Buffer> {
  private List<Buffer> buffers = new ArrayList<Buffer>();
  private boolean syncWithStaticBufferList = false;

  private boolean hasBuffer(String pName) {
    for (Buffer buffer : buffers) {
      if (buffer.getName().equalsIgnoreCase(pName)) {
        return true;
      }
    }
    return false;
  }

  private String getUniqueBuffername(String pName) {
    String name = pName;
    int postfix = 1;
    while (true) {
      if (!hasBuffer(name))
        break;
      name = pName + " - " + (postfix++);
    }
    return name;
  }

  public Buffer addImageBuffer(JFrame pRootFrame, String pName, SimpleImage pSimpleImage) {
    Buffer buffer = new Buffer(pRootFrame, getUniqueBuffername(pName), pSimpleImage);
    buffers.add(buffer);
    if (syncWithStaticBufferList)
      StaticBufferList.add(buffer);
    return buffer;
  }

  public Buffer addHDRImageBuffer(JFrame pRootFramn, String pName, SimpleHDRImage pSimpleHDRImage) {
    Buffer buffer = new Buffer(pRootFramn, getUniqueBuffername(pName), pSimpleHDRImage);
    buffers.add(buffer);
    if (syncWithStaticBufferList)
      StaticBufferList.add(buffer);
    return buffer;
  }

  public Buffer addMesh3DBuffer(JFrame pRootFrame, String pName, Mesh3D pMesh3D,
      SimpleImage pPreviewImage) {
    Buffer buffer = new Buffer(pRootFrame, getUniqueBuffername(pName + " (3D)"), pMesh3D,
        pPreviewImage);
    buffers.add(buffer);
    if (syncWithStaticBufferList)
      StaticBufferList.add(buffer);
    return buffer;
  }

  public Buffer bufferByName(String pName) {
    for (Buffer buffer : buffers) {
      if (buffer.getName().equalsIgnoreCase(pName)) {
        return buffer;
      }
    }
    return null;
  }

  public List<Buffer> getBufferList() {
    return buffers;
  }

  public List<Buffer> getBufferList(BufferType pBufferType) {
    List<Buffer> res = new ArrayList<Buffer>();
    for (Buffer buffer : buffers) {
      if (buffer.getBufferType() == pBufferType) {
        res.add(buffer);
      }
    }
    return res;
  }

  public int size() {
    return buffers.size();
  }

  public Buffer get(int pIndex) {
    return buffers.get(pIndex);
  }

  @Override
  public boolean add(Buffer pBuffer) {
    boolean res = buffers.add(pBuffer);
    if (syncWithStaticBufferList)
      StaticBufferList.add(pBuffer);
    return res;
  }

  @Override
  public boolean addAll(Collection<? extends Buffer> pBuffers) {
    boolean res = buffers.addAll(pBuffers);
    if (syncWithStaticBufferList)
      StaticBufferList.addAll(pBuffers);
    return res;
  }

  @Override
  public void clear() {
    buffers.clear();
    if (syncWithStaticBufferList)
      StaticBufferList.clear();
  }

  @Override
  public boolean contains(Object pObject) {
    return buffers.contains(pObject);
  }

  @Override
  public boolean containsAll(Collection<?> pCollection) {
    return buffers.containsAll(pCollection);
  }

  @Override
  public boolean isEmpty() {
    return buffers.isEmpty();
  }

  @Override
  public Iterator<Buffer> iterator() {
    return buffers.iterator();
  }

  @Override
  public boolean remove(Object pObject) {
    boolean res = buffers.remove(pObject);
    if (syncWithStaticBufferList) {
      StaticBufferList.remove(pObject);
    }
    return res;
  }

  @Override
  public boolean removeAll(Collection<?> pCollection) {
    boolean res = buffers.removeAll(pCollection);
    if (syncWithStaticBufferList)
      StaticBufferList.removeAll(pCollection);
    return res;
  }

  @Override
  public boolean retainAll(Collection<?> pCollection) {
    return buffers.retainAll(pCollection);
  }

  @Override
  public Object[] toArray() {
    return buffers.toArray();
  }

  @Override
  public <T> T[] toArray(T[] pArg) {
    return buffers.toArray(pArg);
  }

  public void setSyncWithStaticBufferList(boolean syncWithStaticBufferList) {
    this.syncWithStaticBufferList = syncWithStaticBufferList;
  }
}
