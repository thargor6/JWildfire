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
package org.jwildfire.swing;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageLogMapHolder {
  private Map<Long, ILoggingEvent> eventMap = new ConcurrentHashMap<>();
  private Set<MessageLogEventObserver> observers = new HashSet<>();
  private MessageLogMapHolder(){}

  private static MessageLogMapHolder MAP_INSTANCE = null;

  public static MessageLogMapHolder create(){
    if(MAP_INSTANCE == null){
      MAP_INSTANCE = new MessageLogMapHolder();
    }
    return MAP_INSTANCE;
  }

  public void put(long currentTimeMillis, ILoggingEvent event) {
    eventMap.put(currentTimeMillis, event);
    for(MessageLogEventObserver observer: observers) {
      try {
        observer.update(event);
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public void registerObserver(MessageLogEventObserver observer) {
    observers.add(observer);
  }

  public void unregisterObserver(MessageLogEventObserver observer) {
    observers.remove(observer);
  }
}
