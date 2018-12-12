/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.variation.iflames;

import org.jwildfire.base.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IFlamesAnimatorMotionStore {
  private final Map<Float, StoreEntry> store = new HashMap<Float, StoreEntry>();

  private static class StoreEntry {
    private static final int MAX_PERMANENT_STORE = 1000;
    private static final int INITIAL_CAPACITY = 10000;
    private final float dt;
    @SuppressWarnings("rawtypes")
    private List[] entries = new ArrayList[INITIAL_CAPACITY];

    public StoreEntry(float pDt) {
      dt = pDt;
    }

    private int reserveAndGetStoreIndex(float pTime) {
      int index = Tools.FTOI(pTime / dt);
      if (index >= entries.length) {
        int newSize = (index / INITIAL_CAPACITY) * INITIAL_CAPACITY;
        if (newSize <= index) {
          newSize += INITIAL_CAPACITY;
        }
        @SuppressWarnings("rawtypes")
        List[] newEntries = new ArrayList[newSize];
        System.arraycopy(entries, 0, newEntries, 0, entries.length);
        entries = newEntries;
      }
      return index;
    }

    public void storeToIndex(float pTime, List<Particle> pParticles) {
      int index = reserveAndGetStoreIndex(pTime);
      List<Particle> dest = new ArrayList<Particle>();
      entries[index] = dest;
      for (Particle src : pParticles) {
        dest.add(src.makeCopy());
      }
      for (int i = index - MAX_PERMANENT_STORE; i >= 0; i--) {
        @SuppressWarnings("unchecked")
        List<Particle> p = entries[i];
        if (p != null) {
          p.clear();
          entries[i] = null;
        }
      }

    }

    public float getMaxStoredMotionTime(float pReferenceTime) {
      int maxIndex = (int) (pReferenceTime / dt);
      if (maxIndex >= entries.length) {
        maxIndex = entries.length - 1;
      }
      for (int i = maxIndex; i >= 0; i--) {
        if (entries[i] != null) {
          return i * dt;
        }
      }
      return 0.0f;
    }

    public void readFromIndex(float pTime, List<Particle> pParticles) {
      int index = reserveAndGetStoreIndex(pTime);
      @SuppressWarnings("unchecked")
      List<Particle> srcParticles = entries[index];
      if (pParticles.size() != srcParticles.size()) {
        throw new RuntimeException("Stored motion does not match (" + pParticles.size() + "!=" + srcParticles.size() + ")");
      }
      for (int i = 0; i < pParticles.size(); i++) {
        pParticles.set(i, srcParticles.get(i).makeCopy());
      }
    }
  }

  private StoreEntry getStoreEntry(float pDt) {
    Float key = Float.valueOf(pDt);
    StoreEntry res = store.get(key);
    if (res == null) {
      res = new StoreEntry(pDt);
      store.put(key, res);
    }
    return res;
  }

  public float getMaxStoredMotionTime(float pReferenceTime, float pDt) {
    StoreEntry storeEntry = getStoreEntry(pDt);
    return storeEntry.getMaxStoredMotionTime(pReferenceTime);
  }

  public void storeMotion(float pTime, float pDt, List<Particle> pParticles) {
    StoreEntry storeEntry = getStoreEntry(pDt);
    storeEntry.storeToIndex(pTime, pParticles);
  }

  public void readFromStore(float pTime, float pDt, List<Particle> pParticles) {
    StoreEntry storeEntry = getStoreEntry(pDt);
    storeEntry.readFromIndex(pTime, pParticles);
  }
}
