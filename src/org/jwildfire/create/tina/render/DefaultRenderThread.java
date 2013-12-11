package org.jwildfire.create.tina.render;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;

public abstract class DefaultRenderThread extends AbstractRenderThread {
  protected long startIter;
  protected long iter;
  protected List<DefaultRenderIterationState> iterationState;

  public DefaultRenderThread(Prefs pPrefs, int pThreadId, FlameRenderer pRenderer, Flame pFlame, long pSamples) {
    super(pPrefs, pThreadId, pRenderer, pFlame, pSamples);
    iterationState = new ArrayList<DefaultRenderIterationState>();
    List<Layer> layers = getValidLayers(pFlame);
    for (Layer layer : layers) {
      DefaultRenderIterationState state = createState(layer);
      iterationState.add(state);
    }
  }

  protected abstract DefaultRenderIterationState createState(Layer layer);

  @Override
  protected RenderThreadPersistentState saveState() {
    DefaultRenderThreadPersistentState res = new DefaultRenderThreadPersistentState();
    res.currSample = currSample;
    res.startIter = iter;
    for (DefaultRenderIterationState state : iterationState) {
      DefaultRenderThreadPersistentState.IterationState persist = new DefaultRenderThreadPersistentState.IterationState();
      persist.xfIndex = (state.xf != null) ? state.layer.getXForms().indexOf(state.xf) : -1;
      persist.affineT = state.affineT != null ? state.affineT.makeCopy() : null;
      persist.varT = state.varT != null ? state.varT.makeCopy() : null;
      persist.p = state.p != null ? state.p.makeCopy() : null;
      persist.q = state.q != null ? state.q.makeCopy() : null;
      res.getLayerState().add(persist);
    }
    return res;
  }

  @Override
  protected void restoreState(RenderThreadPersistentState pState) {
    iterationState.clear();
    DefaultRenderThreadPersistentState state = (DefaultRenderThreadPersistentState) pState;
    currSample = state.currSample;
    startIter = state.startIter;
    List<Layer> layers = getValidLayers(flame);
    int layerIdx = 0;
    for (DefaultRenderThreadPersistentState.IterationState persist : state.getLayerState()) {
      DefaultRenderIterationState restored = createState(layers.get(layerIdx++));
      restored.xf = (persist.xfIndex >= 0) ? restored.layer.getXForms().get(persist.xfIndex) : null;
      restored.affineT = persist.affineT != null ? persist.affineT.makeCopy() : null;
      restored.varT = persist.varT != null ? persist.varT.makeCopy() : null;
      restored.p = persist.p != null ? persist.p.makeCopy() : null;
      restored.q = persist.q != null ? persist.q.makeCopy() : null;
      iterationState.add(restored);
    }
  }

  @Override
  protected void preFuseIter() {
    for (DefaultRenderIterationState state : iterationState) {
      state.preFuseIter();
    }
  }

  @Override
  protected void initState() {
    startIter = 0;
    preFuseIter();
  }

  @Override
  protected void iterate() {
    final int iterInc = iterationState.size();
    if (iterInc < 1) {
      return;
    }
    for (DefaultRenderIterationState state : iterationState) {
      state.init();
    }

    for (iter = startIter; !forceAbort && (samples < 0 || iter < samples); iter += iterInc) {
      if (iter % 10000 == 0) {
        preFuseIter();
      }
      else if (iter % 100 == 0) {
        currSample = iter;
        for (DefaultRenderIterationState state : iterationState) {
          state.validateState();
        }
      }
      for (DefaultRenderIterationState state : iterationState) {
        state.iterateNext();
      }
    }
  }

  @Override
  protected long getIter() {
    return iter;
  }

}
