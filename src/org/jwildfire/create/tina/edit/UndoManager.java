/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
package org.jwildfire.create.tina.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UndoManager<T extends Assignable<T>> implements PropertyChangeListener<T> {
  private final Map<T, List<UndoItem<T>>> undoStack = new HashMap<T, List<UndoItem<T>>>();
  private final Map<T, Integer> undoStackPosition = new HashMap<T, Integer>();
  private boolean enabled = true;

  public void initUndoStack(T pInitialState) {
    List<UndoItem<T>> items = new ArrayList<UndoItem<T>>();
    undoStack.put(pInitialState, items);
    undoStackPosition.put(pInitialState, -1);
  }

  private List<UndoItem<T>> getUndoStack(T pInitialState) {
    List<UndoItem<T>> items = undoStack.get(pInitialState);
    if (items == null)
      throw new RuntimeException("initUndoStack was not called");
    return items;
  }

  private Integer getUndoStackPosition(T pInitialState) {
    Integer pos = undoStackPosition.get(pInitialState);
    if (pos == null)
      throw new RuntimeException("initUndoStack was not called");
    return -1;
  }

  public void saveUndoPoint(T pInitialState) {
    List<UndoItem<T>> stack = getUndoStack(pInitialState);
    stack.add(new UndoItem<T>(pInitialState.makeCopy()));
    undoStackPosition.put(pInitialState, -1);
  }

  public void undo(T pInitialState) {
    List<UndoItem<T>> stack = getUndoStack(pInitialState);
    Integer pos = getUndoStackPosition(pInitialState);
    if (pos < 0) {
      pos = stack.size() - 1;
    }
    else {
      pos--;
    }
    undoStackPosition.put(pInitialState, pos);
    if (pos >= 0 && pos < stack.size()) {
      pInitialState.assign(stack.get(pos).getData());
    }
  }

  public void redo(T pInitialState) {
    List<UndoItem<T>> stack = getUndoStack(pInitialState);
    Integer pos = getUndoStackPosition(pInitialState);
    if (pos < 0) {
      pos = stack.size() - 1;
    }
    else {
      pos++;
    }
    if (pos >= stack.size()) {
      pos = stack.size() - 1;
    }
    undoStackPosition.put(pInitialState, pos);
    if (pos >= 0 && pos < stack.size()) {
      pInitialState.assign(stack.get(pos).getData());
    }
  }

  @Override
  public void propertyChanged(T pOwner, String pName, double pOldValue, double pNewValue) {
    if (enabled && pOldValue != pNewValue) {
      System.out.println("UNDO_D " + pName);
      saveUndoPoint(pOwner);
    }
  }

  @Override
  public void propertyChanged(T pOwner, String pName, int pOldValue, int pNewValue) {
    if (enabled && pOldValue != pNewValue) {
      System.out.println("UNDO_I " + pName);
      saveUndoPoint(pOwner);
    }
  }

  @Override
  public void propertyChanged(T pOwner, String pName, boolean pOldValue, boolean pNewValue) {
    if (enabled && pOldValue != pNewValue) {
      System.out.println("UNDO_B " + pName);
      saveUndoPoint(pOwner);
    }
  }

  @Override
  public void propertyChanged(T pOwner, String pName, String pOldValue, String pNewValue) {
    if (enabled && ((pOldValue == null && pNewValue != null && pNewValue.length() > 0) ||
        (pOldValue != null && pOldValue.length() > 0 && pNewValue == null) ||
        (pOldValue != null && pNewValue != null && !pOldValue.equals(pNewValue)))) {
      System.out.println("UNDO_S " + pName);
      saveUndoPoint(pOwner);
    }

  }

  @Override
  public void propertyChanged(T pOwner, String pName, Object pOldValue, Object pNewValue) {
    if (enabled) {
      if ((pNewValue != null && pNewValue instanceof Integer) || (pOldValue != null && pOldValue instanceof Integer)) {
        int oldValue = pOldValue != null ? ((Integer) pOldValue).intValue() : 0;
        int newValue = pNewValue != null ? ((Integer) pNewValue).intValue() : 0;
        propertyChanged(pOwner, pName, oldValue, newValue);
      }
      else if ((pNewValue != null && pNewValue instanceof Double) || (pOldValue != null && pOldValue instanceof Double)) {
        double oldValue = pOldValue != null ? ((Double) pOldValue).doubleValue() : 0.0;
        double newValue = pNewValue != null ? ((Double) pNewValue).doubleValue() : 0.0;
        propertyChanged(pOwner, pName, oldValue, newValue);
      }
      else if ((pNewValue != null && pNewValue instanceof Boolean) || (pOldValue != null && pOldValue instanceof Boolean)) {
        boolean oldValue = pOldValue != null ? ((Boolean) pOldValue).booleanValue() : false;
        boolean newValue = pNewValue != null ? ((Boolean) pNewValue).booleanValue() : false;
        propertyChanged(pOwner, pName, oldValue, newValue);
      }
      else if (pNewValue != null || pOldValue != null) {
        saveUndoPoint(pOwner);
      }
    }
  }

  @Override
  public void setEnabled(boolean pEnabled) {
    enabled = pEnabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
