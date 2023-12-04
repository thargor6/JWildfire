/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2023 Andreas Maschke

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
package org.jwildfire.create.tina.render.backdrop;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;

public class FlameBackgroundRenderContext {
  private final Flame preparedFlame;

  private final FlameTransformationContext ctx;

  public FlameBackgroundRenderContext(Flame pFlame, int pThreadID) {
    this.preparedFlame = pFlame.makeCopy();
    this.ctx = new FlameTransformationContext(new FlameRenderer(preparedFlame, Prefs.getPrefs(), false, true), new MarsagliaRandomGenerator(), pThreadID, 1);

    for(Layer layer: preparedFlame.getLayers()) {
      for(XForm xform: layer.getBGXForms()) {
        xform.initTransform();
        if(ctx.getThreadId()==0) {
          for (Variation var : xform.getVariations()) {
            var.getFunc().initOnce(ctx, layer, xform, var.getAmount());
          }
        }
        for (Variation var : xform.getVariations()) {
          var.getFunc().init(ctx, layer, xform, var.getAmount());
        }
      }
    }
  }

  public Flame getPreparedFlame() {
    return preparedFlame;
  }

  public FlameTransformationContext getFlameTransformationCtx() {
    return ctx;
  }
}
