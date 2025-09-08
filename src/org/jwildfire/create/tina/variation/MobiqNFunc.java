
/*
 * JWildfire - an image and animation processor written in Java
 * Copyright (C) 1995-2021 Andreas Maschke
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * A 3D variation combining quaternion transforms, N-power space mapping,
 * iteration control, and geometric space switching. Includes internal math
 * functions for compatibility with older Java versions. CPU-only version.
 *
 * @author Brad Stefanov, eralex61, chronologicaldot, thargor6, and Gemini
 */
public class MobiqNFunc extends VariationFunc {
    private static final long serialVersionUID = 10L; // Updated serialVersionUID

    // Parameters
    private static final String PARAM_AT = "qat", PARAM_AX = "qax", PARAM_AY = "qay", PARAM_AZ = "qaz";
    private static final String PARAM_BT = "qbt", PARAM_BX = "qbx", PARAM_BY = "qby", PARAM_BZ = "qbz";
    private static final String PARAM_CT = "qct", PARAM_CX = "qcx", PARAM_CY = "qcy", PARAM_CZ = "qcz";
    private static final String PARAM_DT = "qdt", PARAM_DX = "qdx", PARAM_DY = "qdy", PARAM_DZ = "qdz";
    private static final String PARAM_POWER = "power", PARAM_DIST = "dist";
    private static final String PARAM_COLOR_MODE = "colorMode";
    private static final String PARAM_ITERATIONS = "iterations";
    private static final String PARAM_SPACE_TYPE = "spaceType";

    private static final String[] paramNames = {
            PARAM_AT, PARAM_AX, PARAM_AY, PARAM_AZ, PARAM_BT, PARAM_BX, PARAM_BY, PARAM_BZ,
            PARAM_CT, PARAM_CX, PARAM_CY, PARAM_CZ, PARAM_DT, PARAM_DX, PARAM_DY, PARAM_DZ,
            PARAM_POWER, PARAM_DIST, PARAM_COLOR_MODE, PARAM_ITERATIONS, PARAM_SPACE_TYPE
    };

    // Defaults
    private double qat = 1.0, qax = 0.0, qay = 0.0, qaz = 0.0;
    private double qbt = 0.0, qbx = 0.0, qby = 0.0, qbz = 0.0;
    private double qct = 0.0, qcx = 0.0, qcy = 0.0, qcz = 1.0;
    private double qdt = 1.0, qdx = 0.0, qdy = 0.0, qdz = 0.0;
    private double power = 2.0, dist = 1.0;
    private int colorMode = 0;
    private int iterations = 1;
    private int spaceType = 0;

    // --- Helper functions for older Java versions ---
    private double sinh(double x) {
        return (Math.exp(x) - Math.exp(-x)) * 0.5;
    }

    private double asinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1.0));
    }
    // ------------------------------------------------

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        // --- 1. N-power pre-transform ---
        double z_scale = (power != 0.0) ? (4.0 * dist / power) : 0.0;
        double rho = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z));
        double theta = atan2(pAffineTP.y, pAffineTP.x);
        double phi = (rho == 0.0) ? 0.0 : acos(pAffineTP.z / rho);
        double new_rho = pow(rho, z_scale);
        double new_theta = theta * power;
        double new_phi = phi * power;
        double sin_phi = sin(new_phi);
        double x_prime = new_rho * sin_phi * cos(new_theta);
        double y_prime = new_rho * sin_phi * sin(new_theta);
        double z_prime = new_rho * cos(new_phi);

        // --- 2. Geometric Space Mapping (Forward) ---
        double x_mapped = x_prime, y_mapped = y_prime, z_mapped = z_prime;
        switch (spaceType) {
            case 1: // Hyperbolic
                x_mapped = sinh(x_prime); y_mapped = sinh(y_prime); z_mapped = sinh(z_prime);
                break;
            case 2: // Spherical
                x_mapped = sin(x_prime); y_mapped = sin(y_prime); z_mapped = sin(z_prime);
                break;
        }

        // --- 3. Iterated Mobiq core transformation ---
        double x_res = x_mapped, y_res = y_mapped, z_res = z_mapped;
        double t_res_quat = 0.0, denom_sqr = 0.0;

        int iter = Math.max(1, iterations);
        for (int i = 0; i < iter; i++) {
            double t2 = x_res, x2 = y_res, y2 = z_res;
            double nt_num = qat*t2-qax*x2-qay*y2+qbt; double nx_num = qat*x2+qax*t2-qaz*y2+qbx;
            double ny_num = qat*y2+qay*t2+qaz*x2+qby; double nz_num = qaz*t2+qax*y2-qay*x2+qbz;
            double dt_den = qct*t2-qcx*x2-qcy*y2+qdt; double dx_den = qct*x2+qcx*t2-qcz*y2+qdx;
            double dy_den = qct*y2+qcy*t2+qcz*x2+qdy; double dz_den = qcz*t2+qcx*y2-qcy*x2+qdz;
            denom_sqr = sqr(dt_den) + sqr(dx_den) + sqr(dy_den) + sqr(dz_den);
            
            if (denom_sqr != 0.0) {
                double inv_denom = 1.0 / denom_sqr;
                t_res_quat = (nt_num*dt_den+nx_num*dx_den+ny_num*dy_den+nz_num*dz_den)*inv_denom;
                x_res = (nx_num*dt_den-nt_num*dx_den-ny_num*dz_den+nz_num*dy_den)*inv_denom;
                y_res = (ny_num*dt_den-nt_num*dy_den-nz_num*dx_den+nx_num*dz_den)*inv_denom;
                z_res = (nz_num*dt_den-nx_num*dy_den+ny_num*dx_den-nt_num*dz_den)*inv_denom;
            } else {
                t_res_quat = x_res = y_res = z_res = 0.0;
            }
        }

        // --- 4. Geometric Space Mapping (Inverse) ---
        double x_unmapped = x_res, y_unmapped = y_res, z_unmapped = z_res;
        switch (spaceType) {
            case 1: // Hyperbolic
                x_unmapped = asinh(x_res); y_unmapped = asinh(y_res); z_unmapped = asinh(z_res);
                break;
            case 2: // Spherical
                x_unmapped = asin(x_res); y_unmapped = asin(y_res); z_unmapped = asin(z_res);
                break;
        }

        // --- 5. N-power post-transform ---
        double z_inv_scale = (z_scale != 0.0) ? (1.0 / z_scale) : 0.0;
        double rho_res = sqrt(sqr(x_unmapped) + sqr(y_unmapped) + sqr(z_unmapped));
        double theta_res = atan2(y_unmapped, x_unmapped);
        double phi_res = (rho_res == 0.0) ? 0.0 : acos(z_unmapped / rho_res);
        double final_rho = pow(rho_res, z_inv_scale);
        double floored_power = floor(power);
        double n_theta = floor(power * pContext.random());
        double n_phi = floor(power * pContext.random());
        double final_theta = (floored_power!=0.0)?(theta_res+n_theta*M_2PI)/floored_power:theta_res;
        double final_phi = (floored_power!=0.0)?(phi_res+n_phi*M_2PI)/floored_power:phi_res;
        double final_sin_phi = sin(final_phi);
        double final_x = final_rho * final_sin_phi * cos(final_theta);
        double final_y = final_rho * final_sin_phi * sin(final_theta);
        double final_z = final_rho * cos(final_phi);
        
        // --- 6. Coloring ---
        switch (colorMode) {
            case 1: pVarTP.color = new_rho * z_inv_scale * 0.1; break;
            case 2: pVarTP.color = (t_res_quat + 1.0) * 0.5; break;
            case 3: pVarTP.color = atan(sqrt(denom_sqr)) / (M_PI * 0.5); break;
            case 4:
                double len = sqrt(sqr(final_x) + sqr(final_y) + sqr(final_z));
                pVarTP.color = (len > EPSILON) ? ((final_z / len + 1.0) * 0.5) : 0.5;
                break;
            case 5: // NEW: Coloring by Expansion/Contraction
                if (rho > EPSILON) {
                    double ratio = final_rho / rho;
                    pVarTP.color = atan(ratio) / (M_PI * 0.5); // Maps [0, inf) to [0, 1)
                } else {
                    pVarTP.color = 0.5;
                }
                break;
            case 6: // NEW: Coloring by Angular Twist
                double twist = final_theta - theta;
                double normalized_twist = twist / M_2PI;
                pVarTP.color = normalized_twist - floor(normalized_twist); // Gets the fractional part
                break;
        }
        
        // --- 7. Final update ---
        pVarTP.x += pAmount * final_x;
        pVarTP.y += pAmount * final_y;
        pVarTP.z += pAmount * final_z;
    }

    @Override
    public String getName() { return "mobiqN"; }

    @Override
    public String[] getParameterNames() { return paramNames; }

    @Override
    public Object[] getParameterValues() {
        return new Object[]{
                qat, qax, qay, qaz, qbt, qbx, qby, qbz,
                qct, qcx, qcy, qcz, qdt, qdx, qdy, qdz,
                power, dist, colorMode, iterations, spaceType
        };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_AT.equalsIgnoreCase(pName)) qat = pValue;
        else if (PARAM_AX.equalsIgnoreCase(pName)) qax = pValue;
        else if (PARAM_AY.equalsIgnoreCase(pName)) qay = pValue;
        else if (PARAM_AZ.equalsIgnoreCase(pName)) qaz = pValue;
        else if (PARAM_BT.equalsIgnoreCase(pName)) qbt = pValue;
        else if (PARAM_BX.equalsIgnoreCase(pName)) qbx = pValue;
        else if (PARAM_BY.equalsIgnoreCase(pName)) qby = pValue;
        else if (PARAM_BZ.equalsIgnoreCase(pName)) qbz = pValue;
        else if (PARAM_CT.equalsIgnoreCase(pName)) qct = pValue;
        else if (PARAM_CX.equalsIgnoreCase(pName)) qcx = pValue;
        else if (PARAM_CY.equalsIgnoreCase(pName)) qcy = pValue;
        else if (PARAM_CZ.equalsIgnoreCase(pName)) qcz = pValue;
        else if (PARAM_DT.equalsIgnoreCase(pName)) qdt = pValue;
        else if (PARAM_DX.equalsIgnoreCase(pName)) qdx = pValue;
        else if (PARAM_DY.equalsIgnoreCase(pName)) qdy = pValue;
        else if (PARAM_DZ.equalsIgnoreCase(pName)) qdz = pValue;
        else if (PARAM_POWER.equalsIgnoreCase(pName)) power = pValue;
        else if (PARAM_DIST.equalsIgnoreCase(pName)) dist = pValue;
        else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) colorMode = (int) pValue;
        else if (PARAM_ITERATIONS.equalsIgnoreCase(pName)) iterations = (int) pValue;
        else if (PARAM_SPACE_TYPE.equalsIgnoreCase(pName)) spaceType = (int) pValue;
        else throw new IllegalArgumentException(pName);
    }

    @Override
    public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
        if (fabs(power) < 1.0) { power = 1.0; }
    }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_3D};
    }
}
