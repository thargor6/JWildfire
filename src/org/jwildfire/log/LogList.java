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
package org.jwildfire.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


public class LogList extends ArrayList<LogEntry> {
  private static final long serialVersionUID = 1L;

  public LogEntry addInfoEntry(String pMessage) {
    LogEntry res = new LogEntry();
    res.setCategory(Category.INFO);
    res.setMessage(pMessage);
    return res;
  }

  public LogEntry addErrorEntry(Throwable pThrowable) {
    LogEntry res = new LogEntry();
    res.setCategory(Category.ERROR);
    try {
      res.setMessage(pThrowable.getMessage());
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      pThrowable.printStackTrace(new PrintStream(os));
      os.flush();
      os.close();
      res.setStackTrace(new String(os.toByteArray()));
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
    return res;
  }
}
