/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR a PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Vibration2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_DIR = "dir";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_PHASE = "phase";
  private static final String PARAM_DIR2 = "dir2";
  private static final String PARAM_ANGLE2 = "angle2";
  private static final String PARAM_FREQ2 = "freq2";
  private static final String PARAM_AMP2 = "amp2";
  private static final String PARAM_PHASE2 = "phase2";
  private static final String PARAM_DM = "dm";
  private static final String PARAM_DMFREQ = "dmfreq";
  private static final String PARAM_TM = "tm";
  private static final String PARAM_TMFREQ = "tmfreq";
  private static final String PARAM_FM = "fm";
  private static final String PARAM_FMFREQ = "fmfreq";
  private static final String PARAM_AM = "am";
  private static final String PARAM_AMFREQ = "amfreq";
  private static final String PARAM_D2M = "d2m";
  private static final String PARAM_D2MFREQ = "d2mfreq";
  private static final String PARAM_T2M = "t2m";
  private static final String PARAM_T2MFREQ = "t2mfreq";
  private static final String PARAM_F2M = "f2m";
  private static final String PARAM_F2MFREQ = "f2mfreq";
  private static final String PARAM_A2M = "a2m";
  private static final String PARAM_A2MFREQ = "a2mfreq";
  private static final String[] paramNames = {PARAM_DIR, PARAM_ANGLE, PARAM_FREQ, PARAM_AMP, PARAM_PHASE, PARAM_DIR2,
          PARAM_ANGLE2, PARAM_FREQ2, PARAM_AMP2, PARAM_PHASE2, PARAM_DM, PARAM_DMFREQ, PARAM_TM, PARAM_TMFREQ,
          PARAM_FM, PARAM_FMFREQ, PARAM_AM, PARAM_AMFREQ, PARAM_D2M, PARAM_D2MFREQ, PARAM_T2M, PARAM_T2MFREQ,
          PARAM_F2M, PARAM_F2MFREQ, PARAM_A2M, PARAM_A2MFREQ};
  private double dir = 0.0;
  private double angle = 1.5708;
  private double freq = 1.0;
  private double amp = 0.25;
  private double phase = 0.0;
  private double dir2 = 1.5708;
  private double angle2 = 1.5708;
  private double freq2 = 1.0;
  private double amp2 = 0.25;
  private double phase2 = 0.0;
  private double dm = 0.0;
  private double dmfreq = 0.1;
  private double tm = 0.0;
  private double tmfreq = 0.1;
  private double fm = 0.0;
  private double fmfreq = 0.1;
  private double am = 0.0;
  private double amfreq = 0.1;
  private double d2m = 0.0;
  private double d2mfreq = 0.1;
  private double t2m = 0.0;
  private double t2mfreq = 0.1;
  private double f2m = 0.0;
  private double f2mfreq = 0.1;
  private double a2m = 0.0;
  private double a2mfreq = 0.1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    /*
     * vibration2 by FarDareisMai
     * https://www.deviantart.com/fardareismai/art/Apo-Plugins-Vibration-1-and-2-252001851 converted by Brad Stefanov
     */

    double d_along_dir = pAffineTP.x * cos(dir) + pAffineTP.y * sin(dir);
    double dirL = dir + modulate(dm, dmfreq, d_along_dir);
    double angleL = angle + modulate(tm, tmfreq, d_along_dir);
    double freqL = modulate(fm, fmfreq, d_along_dir) / freq;
    double ampL = amp + amp * modulate(am, amfreq, d_along_dir);

    double total_angle = angleL + dirL;
    double cos_dir = cos(dirL);
    double sin_dir = sin(dirL);
    double cos_tot = cos(total_angle);
    double sin_tot = sin(total_angle);
    double scaled_freq = freq * M_2PI;
    double phase_shift = M_2PI * phase / freq;
    d_along_dir = pAffineTP.x * cos_dir + pAffineTP.y * sin_dir;
    double local_amp = ampL * sin((d_along_dir * scaled_freq) + freqL + phase_shift);

    double x = pAffineTP.x + local_amp * cos_tot;
    double y = pAffineTP.y + local_amp * sin_tot;

    d_along_dir = pAffineTP.x * cos(dir2) + pAffineTP.y * sin(dir2);
    dirL = dir2 + modulate(d2m, d2mfreq, d_along_dir);
    angleL = angle2 + modulate(t2m, t2mfreq, d_along_dir);
    freqL = modulate(f2m, f2mfreq, d_along_dir) / freq2;
    ampL = amp2 + amp2 * modulate(a2m, a2mfreq, d_along_dir);

    total_angle = angleL + dirL;
    cos_dir = cos(dirL);
    sin_dir = sin(dirL);
    cos_tot = cos(total_angle);
    sin_tot = sin(total_angle);
    scaled_freq = freq2 * M_2PI;
    phase_shift = M_2PI * phase2 / freq2;
    d_along_dir = pAffineTP.x * cos_dir + pAffineTP.y * sin_dir;
    local_amp = ampL * sin((d_along_dir * scaled_freq) + freqL + phase_shift);

    x += local_amp * cos_tot;
    y += local_amp * sin_tot;

    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  private double modulate(double amp, double freq, double x) {
    return amp * cos(x * freq * M_2PI);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{dir, angle, freq, amp, phase, dir2, angle2, freq2, amp2, phase2, dm, dmfreq, tm, tmfreq,
            fm, fmfreq, am, amfreq, d2m, d2mfreq, t2m, t2mfreq, f2m, f2mfreq, a2m, a2mfreq};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_DIR.equalsIgnoreCase(pName)) {
      dir = pValue;
    } else if (PARAM_ANGLE.equalsIgnoreCase(pName)) {
      angle = pValue;
    } else if (PARAM_FREQ.equalsIgnoreCase(pName)) {
      freq = pValue;
    } else if (PARAM_AMP.equalsIgnoreCase(pName)) {
      amp = pValue;
    } else if (PARAM_PHASE.equalsIgnoreCase(pName)) {
      phase = pValue;
    } else if (PARAM_DIR2.equalsIgnoreCase(pName)) {
      dir2 = pValue;
    } else if (PARAM_ANGLE2.equalsIgnoreCase(pName)) {
      angle2 = pValue;
    } else if (PARAM_FREQ2.equalsIgnoreCase(pName)) {
      freq2 = pValue;
    } else if (PARAM_AMP2.equalsIgnoreCase(pName)) {
      amp2 = pValue;
    } else if (PARAM_PHASE2.equalsIgnoreCase(pName)) {
      phase2 = pValue;
    } else if (PARAM_DM.equalsIgnoreCase(pName)) {
      dm = pValue;
    } else if (PARAM_DMFREQ.equalsIgnoreCase(pName)) {
      dmfreq = pValue;
    } else if (PARAM_TM.equalsIgnoreCase(pName)) {
      tm = pValue;
    } else if (PARAM_TMFREQ.equalsIgnoreCase(pName)) {
      tmfreq = pValue;
    } else if (PARAM_FM.equalsIgnoreCase(pName)) {
      fm = pValue;
    } else if (PARAM_FMFREQ.equalsIgnoreCase(pName)) {
      fmfreq = pValue;
    } else if (PARAM_AM.equalsIgnoreCase(pName)) {
      am = pValue;
    } else if (PARAM_AMFREQ.equalsIgnoreCase(pName)) {
      amfreq = pValue;
    } else if (PARAM_D2M.equalsIgnoreCase(pName)) {
      d2m = pValue;
    } else if (PARAM_D2MFREQ.equalsIgnoreCase(pName)) {
      d2mfreq = pValue;
    } else if (PARAM_T2M.equalsIgnoreCase(pName)) {
      t2m = pValue;
    } else if (PARAM_T2MFREQ.equalsIgnoreCase(pName)) {
      t2mfreq = pValue;
    } else if (PARAM_F2M.equalsIgnoreCase(pName)) {
      f2m = pValue;
    } else if (PARAM_F2MFREQ.equalsIgnoreCase(pName)) {
      f2mfreq = pValue;
    } else if (PARAM_A2M.equalsIgnoreCase(pName)) {
      a2m = pValue;
    } else if (PARAM_A2MFREQ.equalsIgnoreCase(pName)) {
      a2mfreq = pValue;
    } else {
      System.out.println("pName not recognized: " + pName);
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "vibration2";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float d_along_dir = __x * cosf(__vibration2_dir) + __y * sinf(__vibration2_dir);\n"
        + "float dirL = __vibration2_dir + vibration2_modulate(__vibration2_dm, __vibration2_dmfreq, d_along_dir);\n"
        + "float angleL = __vibration2_angle + vibration2_modulate(__vibration2_tm, __vibration2_tmfreq, d_along_dir);\n"
        + "float freqL = vibration2_modulate(__vibration2_fm, __vibration2_fmfreq, d_along_dir) / __vibration2_freq;\n"
        + "float ampL = __vibration2_amp + __vibration2_amp * vibration2_modulate(__vibration2_am, __vibration2_amfreq, d_along_dir);\n"

        + "\n"
        + "float total_angle = angleL + dirL;\n"
        + "float cos_dir = cosf(dirL);\n"
        + "float sin_dir = sinf(dirL);\n"
        + "float cos_tot = cosf(total_angle);\n"
        + "float sin_tot = sinf(total_angle);\n"
        + "float scaled_freq = __vibration2_freq * 2.0f*PI;\n"
        + "float phase_shift = 2.0f*PI * __vibration2_phase / __vibration2_freq;\n"
        + "d_along_dir = __x * cos_dir + __y * sin_dir;\n"


        + "float local_amp = ampL * sinf((d_along_dir * scaled_freq) + freqL + phase_shift);\n"
        + "\n"
        + "float x = __x + local_amp * cos_tot;\n"
        + "float y = __y + local_amp * sin_tot;\n"
        + "\n"

        + "d_along_dir = __x * cosf(__vibration2_dir2) + __y * sinf(__vibration2_dir2);\n"
        + "dirL = __vibration2_dir2 + vibration2_modulate(__vibration2_d2m, __vibration2_d2mfreq, d_along_dir);\n"
        + "angleL = __vibration2_angle2 + vibration2_modulate(__vibration2_t2m, __vibration2_t2mfreq, d_along_dir);\n"
        + "freqL = vibration2_modulate(__vibration2_f2m, __vibration2_f2mfreq, d_along_dir) / __vibration2_freq2;\n"
        + "ampL = __vibration2_amp2 + __vibration2_amp2 * vibration2_modulate(__vibration2_a2m, __vibration2_a2mfreq, d_along_dir);\n"

        + "\n"
        + "total_angle = angleL + dirL;\n"
        + "cos_dir = cosf(dirL);\n"
        + "sin_dir = sinf(dirL);\n"
        + "cos_tot = cosf(total_angle);\n"
        + "sin_tot = sinf(total_angle);\n"
        + "scaled_freq = __vibration2_freq2 * 2.0f*PI;\n"
        + "phase_shift = 2.0f*PI * __vibration2_phase2 / __vibration2_freq2;\n"
        + "d_along_dir = __x * cos_dir + __y * sin_dir;\n"
        + "local_amp = ampL * sinf((d_along_dir * scaled_freq) + freqL + phase_shift);\n"
        + "\n"
        + "x += local_amp * cos_tot;\n"
        + "y += local_amp * sin_tot;\n"
        + "\n"
        + "__px += __vibration2 * x;\n"
        + "__py += __vibration2 * y;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __vibration2 * __z;\n": "");
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float vibration2_modulate(float amp, float freq, float x) {\n"
        + "    return amp * cosf(x * freq * PI * 2.f);\n"
        + "}\n";
  }
}
