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
package org.jwildfire.create.tina.mutagen;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;

public abstract class AbstractUserMutation implements Mutation {

  protected abstract String getMutationsString();

  private List<MutationType> getMutationTypes() {
    List<MutationType> res = new ArrayList<MutationType>();
    String mutationsStr = getMutationsString();
    if (mutationsStr != null && mutationsStr.length() > 0) {
      String mutations[] = mutationsStr.split(",");
      for (String mutation : mutations) {
        try {
          MutationType mt = MutationType.valueOf(mutation.trim().toUpperCase());
          res.add(mt);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    return res;
  }

  @Override
  public void execute(Layer pLayer) {
    List<MutationType> types = getMutationTypes();
    if (types.size() > 0) {
      Mutation mutation = types.get(Tools.randomInt(types.size())).createMutationInstance();
      mutation.execute(pLayer);
    }
  }

}
