
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.floor;
import static java.lang.Math.*;

public class Cell3DFunc extends VariationFunc {
    private static final long serialVersionUID = 1L;

    // --- Parameter Name Constants ---
    // General
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_A = "a";

    // Grid Manipulation
    private static final String PARAM_GRID_ROT_X = "grid_rot_x";
    private static final String PARAM_GRID_ROT_Y = "grid_rot_y";
    private static final String PARAM_GRID_ROT_Z = "grid_rot_z";
    private static final String PARAM_CELL_IDX_POWER_X = "cell_idx_power_x";
    private static final String PARAM_CELL_IDX_POWER_Y = "cell_idx_power_y";
    private static final String PARAM_CELL_IDX_POWER_Z = "cell_idx_power_z";

    // Cell Content - Intra-cell coordinates & transformations
    private static final String PARAM_DXDYDZ_MODE = "dxdydz_mode";
    private static final String PARAM_CELL_SCALE_X = "cell_scale_x";
    private static final String PARAM_CELL_SCALE_Y = "cell_scale_y";
    private static final String PARAM_CELL_SCALE_Z = "cell_scale_z";
    private static final String PARAM_CELL_POWER_X = "cell_power_x";
    private static final String PARAM_CELL_POWER_Y = "cell_power_y";
    private static final String PARAM_CELL_POWER_Z = "cell_power_z";
    private static final String PARAM_CELL_ROT_X = "cell_rot_x";
    private static final String PARAM_CELL_ROT_Y = "cell_rot_y";
    private static final String PARAM_CELL_ROT_Z = "cell_rot_z";

    // Cell Spacing/Moving - Primary Set
    private static final String SPACE_XA = "space_xa"; private static final String SPACE_YA = "space_ya";
    private static final String SPACE_XB = "space_xb"; private static final String SPACE_YB = "space_yb";
    private static final String MOVE_XA = "move_xa";
    private static final String SPACE_XC = "space_xc"; private static final String SPACE_YC = "space_yc";
    private static final String MOVE_YA = "move_ya";
    private static final String SPACE_XD = "space_xd"; private static final String SPACE_YD = "space_yd";
    private static final String MOVE_XB = "move_xb"; private static final String MOVE_YB = "move_yb";
    private static final String PARAM_MOVE_TR_X = "move_tr_x"; private static final String PARAM_MOVE_TR_Y = "move_tr_y";
    private static final String PARAM_MOVE_BR_X = "move_br_x";
    private static final String PARAM_SPACE_Z = "space_z"; private static final String PARAM_MOVE_Z = "move_z";

    // Checkerboard Control
    private static final String PARAM_CHECKERBOARD_MODE = "checker_mode";
    private static final String PARAM_CHECKERBOARD_INVERT = "checker_invert";

    // Cell Spacing/Moving - Alternate Set (for Checkerboard)
    private static final String SPACE_XA2 = "space_xa2"; private static final String SPACE_YA2 = "space_ya2";
    private static final String SPACE_XB2 = "space_xb2"; private static final String SPACE_YB2 = "space_yb2";
    private static final String MOVE_XA2 = "move_xa2";
    private static final String SPACE_XC2 = "space_xc2"; private static final String SPACE_YC2 = "space_yc2";
    private static final String MOVE_YA2 = "move_ya2";
    private static final String SPACE_XD2 = "space_xd2"; private static final String SPACE_YD2 = "space_yd2";
    private static final String MOVE_XB2 = "move_xb2"; private static final String MOVE_YB2 = "move_yb2";
    private static final String PARAM_MOVE_TR_X2 = "move_tr_x2"; private static final String PARAM_MOVE_TR_Y2 = "move_tr_y2";
    private static final String PARAM_MOVE_BR_X2 = "move_br_x2";
    private static final String PARAM_SPACE_Z2 = "space_z2"; private static final String PARAM_MOVE_Z2 = "move_z2";

    // Output Control
    private static final String PARAM_MIRROR_X = "mirror_x";
    private static final String PARAM_MIRROR_Y = "mirror_y";
    private static final String PARAM_MIRROR_Z = "mirror_z";
    private static final String PARAM_INVERT_Y_OUTPUT = "invert_y";
    private static final String PARAM_INVERT_Z_OUTPUT = "invert_z";
    private static final String PARAM_OFFSET_X = "offset_x";
    private static final String PARAM_OFFSET_Y = "offset_y";
    private static final String PARAM_OFFSET_Z = "offset_z";

    // Coloring
    private static final String PARAM_COLOR_MODE = "color_mode";
    private static final String PARAM_CIDX_SCALE_X = "cidx_scale_x";
    private static final String PARAM_CIDX_SCALE_Y = "cidx_scale_y";
    private static final String PARAM_CIDX_SCALE_Z = "cidx_scale_z";
    private static final String PARAM_CIDX_OFFSET = "cidx_offset";
    private static final String PARAM_CDIST_SCALE = "cdist_scale";
    private static final String PARAM_COLOR_Q1 = "color_q1"; private static final String PARAM_COLOR_Q2 = "color_q2";
    private static final String PARAM_COLOR_Q3 = "color_q3"; private static final String PARAM_COLOR_Q4 = "color_q4";
    private static final String PARAM_COLOR_USE_Z_SIGN = "color_use_z_sign";
    private static final String PARAM_COLOR_Z_NEG_OFFSET = "color_z_neg_offset";
    private static final String PARAM_CZ_SCALE = "cz_scale";
    private static final String PARAM_CZ_OFFSET = "cz_offset";

    private static final String RESSOURCE_DESCRIPTION = "description";

    private static final String[] paramNames = {
            PARAM_SIZE, PARAM_A,
            PARAM_GRID_ROT_X, PARAM_GRID_ROT_Y, PARAM_GRID_ROT_Z,
            PARAM_CELL_IDX_POWER_X, PARAM_CELL_IDX_POWER_Y, PARAM_CELL_IDX_POWER_Z,
            PARAM_DXDYDZ_MODE,
            PARAM_CELL_SCALE_X, PARAM_CELL_SCALE_Y, PARAM_CELL_SCALE_Z,
            PARAM_CELL_POWER_X, PARAM_CELL_POWER_Y, PARAM_CELL_POWER_Z,
            PARAM_CELL_ROT_X, PARAM_CELL_ROT_Y, PARAM_CELL_ROT_Z,
            SPACE_XA, SPACE_YA, SPACE_XB, SPACE_YB, MOVE_XA,
            SPACE_XC, SPACE_YC, MOVE_YA, SPACE_XD, SPACE_YD,
            MOVE_XB, MOVE_YB, PARAM_MOVE_TR_X, PARAM_MOVE_TR_Y, PARAM_MOVE_BR_X,
            PARAM_SPACE_Z, PARAM_MOVE_Z,
            PARAM_CHECKERBOARD_MODE, PARAM_CHECKERBOARD_INVERT,
            SPACE_XA2, SPACE_YA2, SPACE_XB2, SPACE_YB2, MOVE_XA2,
            SPACE_XC2, SPACE_YC2, MOVE_YA2, SPACE_XD2, SPACE_YD2,
            MOVE_XB2, MOVE_YB2, PARAM_MOVE_TR_X2, PARAM_MOVE_TR_Y2, PARAM_MOVE_BR_X2,
            PARAM_SPACE_Z2, PARAM_MOVE_Z2,
            PARAM_MIRROR_X, PARAM_MIRROR_Y, PARAM_MIRROR_Z,
            PARAM_INVERT_Y_OUTPUT, PARAM_INVERT_Z_OUTPUT,
            PARAM_OFFSET_X, PARAM_OFFSET_Y, PARAM_OFFSET_Z,
            PARAM_COLOR_MODE,
            PARAM_CIDX_SCALE_X, PARAM_CIDX_SCALE_Y, PARAM_CIDX_SCALE_Z, PARAM_CIDX_OFFSET,
            PARAM_CDIST_SCALE,
            PARAM_COLOR_Q1, PARAM_COLOR_Q2, PARAM_COLOR_Q3, PARAM_COLOR_Q4,
            PARAM_COLOR_USE_Z_SIGN, PARAM_COLOR_Z_NEG_OFFSET,
            PARAM_CZ_SCALE, PARAM_CZ_OFFSET
    };
    private static final String[] ressourceNames = {RESSOURCE_DESCRIPTION};

    // --- Member Variables (with defaults) ---
    private double size = 0.60; private double a = 1.0;
    private double grid_rot_x = 0.0, grid_rot_y = 0.0, grid_rot_z = 0.0;
    private double cell_idx_power_x = 1.0, cell_idx_power_y = 1.0, cell_idx_power_z = 1.0;
    private int dxdydz_mode = 0;
    private double cell_scale_x = 1.0, cell_scale_y = 1.0, cell_scale_z = 1.0;
    private double cell_power_x = 1.0, cell_power_y = 1.0, cell_power_z = 1.0;
    private double cell_rot_x = 0.0, cell_rot_y = 0.0, cell_rot_z = 0.0;
    // Primary Space/Move
    private double space_xa = 2.0, space_ya = 2.0, space_xb = 2.0, space_yb = 2.0, move_xa = 1.0;
    private double space_xc = 2.0, space_yc = 2.0, move_ya = 1.0;
    private double space_xd = 2.0, space_yd = 2.0, move_xb = 1.0, move_yb = 1.0;
    private double move_tr_x = 0.0, move_tr_y = 0.0, move_br_x = 0.0;
    private double space_z = 1.0, move_z = 0.0;
    // Checkerboard
    private int checkerboard_mode = 0, checkerboard_invert = 0;
    // Alternate Space/Move (defaults same as primary for safety)
    private double space_xa2 = 2.0, space_ya2 = 2.0, space_xb2 = 2.0, space_yb2 = 2.0, move_xa2 = 1.0;
    private double space_xc2 = 2.0, space_yc2 = 2.0, move_ya2 = 1.0;
    private double space_xd2 = 2.0, space_yd2 = 2.0, move_xb2 = 1.0, move_yb2 = 1.0;
    private double move_tr_x2 = 0.0, move_tr_y2 = 0.0, move_br_x2 = 0.0;
    private double space_z2 = 1.0, move_z2 = 0.0;
    // Output
    private int mirror_x = 0, mirror_y = 0, mirror_z = 0;
    private int invert_y_output = 1, invert_z_output = 0;
    private double offset_x = 0.0, offset_y = 0.0, offset_z = 0.0;
    // Coloring
    private int color_mode = 0;
    private double cidx_scale_x = 0.05, cidx_scale_y = 0.05, cidx_scale_z = 0.05, cidx_offset = 0.0;
    private double cdist_scale = 1.0;
    private double color_q1 = 0.0, color_q2 = 0.25, color_q3 = 0.5, color_q4 = 0.75;
    private int color_use_z_sign = 0; private double color_z_neg_offset = 0.1;
    private double cz_scale = 0.05, cz_offset = 0.0;

    private String description = "org.jwildfire.create.tina.variation.reference.ReferenceFile cell3D.txt";

    private double normalizeColor(double val) {
        double normalized = abs(val);
        return normalized - floor(normalized); // Correct modulo for positive/negative
    }

    private void rotatePoint3D(double[] coords, double angX, double angY, double angZ) {
        double x = coords[0]; double y = coords[1]; double z = coords[2];
        double temp_x, temp_y, temp_z;

        if (abs(angZ) > 1e-9) { // Z axis rotation (first)
            double radZ = toRadians(angZ); double cosZ = cos(radZ); double sinZ = sin(radZ);
            temp_x = x * cosZ - y * sinZ;
            temp_y = x * sinZ + y * cosZ;
            x = temp_x; y = temp_y;
        }
        if (abs(angY) > 1e-9) { // Y' axis rotation (second)
            double radY = toRadians(angY); double cosY = cos(radY); double sinY = sin(radY);
            temp_x = x * cosY + z * sinY;
            temp_z = -x * sinY + z * cosY;
            x = temp_x; z = temp_z;
        }
        if (abs(angX) > 1e-9) { // X'' axis rotation (third)
            double radX = toRadians(angX); double cosX = cos(radX); double sinX = sin(radX);
            temp_y = y * cosX - z * sinX;
            temp_z = y * sinX + z * cosX;
            y = temp_y; z = temp_z;
        }
        coords[0] = x; coords[1] = y; coords[2] = z;
    }

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                          double pAmount) {

        double[] p = {pAffineTP.x, pAffineTP.y, pAffineTP.z};
        if (abs(grid_rot_x) > 1e-9 || abs(grid_rot_y) > 1e-9 || abs(grid_rot_z) > 1e-9) {
            rotatePoint3D(p, grid_rot_x, grid_rot_y, grid_rot_z);
        }
        double p_x = p[0], p_y = p[1], p_z = p[2];

        double actual_cell_size;
        if (abs(a) < 1e-9) actual_cell_size = (abs(size) < 1e-9) ? 1.0 : size;
        else actual_cell_size = size / a;

        if (abs(actual_cell_size) < 1e-9) {
            pVarTP.x += pAmount * p_x; pVarTP.y += pAmount * p_y; pVarTP.z += pAmount * p_z;
            return;
        }
        double inv_actual_cell_size = 1.0 / actual_cell_size;

        double original_x_idx_raw = floor(p_x * inv_actual_cell_size);
        double original_y_idx_raw = floor(p_y * inv_actual_cell_size);
        double original_z_idx_raw = floor(p_z * inv_actual_cell_size);

        double processed_original_x_idx = original_x_idx_raw;
        if (abs(cell_idx_power_x - 1.0) > 1e-9) processed_original_x_idx = signum(original_x_idx_raw) * pow(abs(original_x_idx_raw), cell_idx_power_x);
        double processed_original_y_idx = original_y_idx_raw;
        if (abs(cell_idx_power_y - 1.0) > 1e-9) processed_original_y_idx = signum(original_y_idx_raw) * pow(abs(original_y_idx_raw), cell_idx_power_y);
        double processed_original_z_idx = original_z_idx_raw;
        if (abs(cell_idx_power_z - 1.0) > 1e-9) processed_original_z_idx = signum(original_z_idx_raw) * pow(abs(original_z_idx_raw), cell_idx_power_z);

        double dx = p_x - original_x_idx_raw * actual_cell_size;
        double dy = p_y - original_y_idx_raw * actual_cell_size;
        double dz = p_z - original_z_idx_raw * actual_cell_size;

        if (dxdydz_mode == 1) {
            dx = actual_cell_size / 2.0 - abs(dx - actual_cell_size / 2.0);
            dy = actual_cell_size / 2.0 - abs(dy - actual_cell_size / 2.0);
            dz = actual_cell_size / 2.0 - abs(dz - actual_cell_size / 2.0);
        }

        if (color_mode != 0) {
            double newColor = 0.0;
            switch (color_mode) {
                case 1: // Cell Index (uses power-processed indices)
                    newColor = processed_original_x_idx * cidx_scale_x + processed_original_y_idx * cidx_scale_y + processed_original_z_idx * cidx_scale_z + cidx_offset;
                    break;
                case 2: // dx/dy/dz Magnitude (uses dx,dy,dz after dxdydz_mode)
                    double dist_sq = dx * dx + dy * dy + dz * dz;
                    double norm_dist = (abs(actual_cell_size) > 1e-9) ? (sqrt(dist_sq) / (actual_cell_size * sqrt(3.0))) : 0.0;
                    newColor = norm_dist * cdist_scale;
                    break;
                case 3: // Quadrant/Octant Influence (uses raw indices for stability)
                    double baseColor;
                    if (original_y_idx_raw >= 0) {
                        if (original_x_idx_raw >= 0) baseColor = color_q1; else baseColor = color_q2;
                    } else {
                        if (original_x_idx_raw >= 0) baseColor = color_q3; else baseColor = color_q4;
                    }
                    if (color_use_z_sign == 1 && original_z_idx_raw < 0) {
                        newColor = baseColor + color_z_neg_offset;
                    } else {
                        newColor = baseColor;
                    }
                    break;
                case 4: // Z-Index Based Color (raw Z index)
                    newColor = original_z_idx_raw * cz_scale + cz_offset;
                    break;
            }
            pVarTP.color = normalizeColor(newColor);
        }

        // Cell Content Transformations (power, scale, rotation) on dx, dy, dz
        if (abs(actual_cell_size)>1e-9) { // Guard for inv_actual_cell_size
            if (abs(cell_power_x - 1.0) > 1e-9) dx = (abs(dx) < 1e-9) ? 0.0 : signum(dx) * pow(abs(dx * inv_actual_cell_size), cell_power_x) * actual_cell_size;
            if (abs(cell_power_y - 1.0) > 1e-9) dy = (abs(dy) < 1e-9) ? 0.0 : signum(dy) * pow(abs(dy * inv_actual_cell_size), cell_power_y) * actual_cell_size;
            if (abs(cell_power_z - 1.0) > 1e-9) dz = (abs(dz) < 1e-9) ? 0.0 : signum(dz) * pow(abs(dz * inv_actual_cell_size), cell_power_z) * actual_cell_size;
        }

        dx *= cell_scale_x; dy *= cell_scale_y; dz *= cell_scale_z;

        if (abs(cell_rot_x) > 1e-9 || abs(cell_rot_y) > 1e-9 || abs(cell_rot_z) > 1e-9) {
            double center_offset = actual_cell_size / 2.0;
            double[] cell_content_coords = {dx - center_offset, dy - center_offset, dz - center_offset};
            rotatePoint3D(cell_content_coords, cell_rot_x, cell_rot_y, cell_rot_z);
            dx = cell_content_coords[0] + center_offset;
            dy = cell_content_coords[1] + center_offset;
            dz = cell_content_coords[2] + center_offset;
        }
        
        boolean use_alt_params = false;
        if (checkerboard_mode == 1) {
            long ix_check = (long)floor(original_x_idx_raw); long iy_check = (long)floor(original_y_idx_raw);
            // For 3D checkerboard, you might include Z: long iz_check = (long)floor(original_z_idx_raw);
            // boolean is_odd_cell = (ix_check + iy_check + iz_check) % 2 != 0;
            boolean is_odd_cell = (ix_check + iy_check) % 2 != 0; // Current: 2D checkerboard pattern
            if ((is_odd_cell && checkerboard_invert == 0) || (!is_odd_cell && checkerboard_invert == 1)) {
                use_alt_params = true;
            }
        }

        double cur_space_xa = use_alt_params ? space_xa2 : space_xa; double cur_space_ya = use_alt_params ? space_ya2 : space_ya;
        double cur_space_xb = use_alt_params ? space_xb2 : space_xb; double cur_space_yb = use_alt_params ? space_yb2 : space_yb;
        double cur_move_xa  = use_alt_params ? move_xa2  : move_xa;
        double cur_space_xc = use_alt_params ? space_xc2 : space_xc; double cur_space_yc = use_alt_params ? space_yc2 : space_yc;
        double cur_move_ya  = use_alt_params ? move_ya2  : move_ya;
        double cur_space_xd = use_alt_params ? space_xd2 : space_xd; double cur_space_yd = use_alt_params ? space_yd2 : space_yd;
        double cur_move_xb  = use_alt_params ? move_xb2  : move_xb; double cur_move_yb  = use_alt_params ? move_yb2  : move_yb;
        double cur_move_tr_x= use_alt_params ? move_tr_x2: move_tr_x; double cur_move_tr_y= use_alt_params ? move_tr_y2: move_tr_y;
        double cur_move_br_x= use_alt_params ? move_br_x2: move_br_x;
        double cur_space_z  = use_alt_params ? space_z2  : space_z;   double cur_move_z   = use_alt_params ? move_z2   : move_z;

        double modified_x_idx = processed_original_x_idx;
        double modified_y_idx = processed_original_y_idx;

        if (processed_original_y_idx >= 0) {
            if (processed_original_x_idx >= 0) { // Q1
                modified_y_idx = cur_space_ya * processed_original_y_idx + cur_move_tr_y;
                modified_x_idx = cur_space_xa * processed_original_x_idx + cur_move_tr_x;
            } else { // Q2
                modified_y_idx = cur_space_yb * processed_original_y_idx;
                modified_x_idx = -(cur_space_xb * processed_original_x_idx + cur_move_xa);
            }
        } else { 
            if (processed_original_x_idx >= 0) { // Q3
                modified_y_idx = -(cur_space_yc * processed_original_y_idx + cur_move_ya);
                modified_x_idx = cur_space_xc * processed_original_x_idx + cur_move_br_x;
            } else { // Q4
                modified_y_idx = -(cur_space_yd * processed_original_y_idx + cur_move_yb);
                modified_x_idx = -(cur_space_xd * processed_original_x_idx + cur_move_xb);
            }
        }
        
        double modified_z_idx = processed_original_z_idx * cur_space_z + cur_move_z;
        
        double final_x = dx + modified_x_idx * actual_cell_size;
        double final_y = dy + modified_y_idx * actual_cell_size;
        double final_z = dz + modified_z_idx * actual_cell_size;

        pVarTP.x += pAmount * final_x;
        pVarTP.y += pAmount * (invert_y_output == 1 ? -final_y : final_y);
        pVarTP.z += pAmount * (invert_z_output == 1 ? -final_z : final_z);

        if (mirror_x > 0 && pContext.random() < 0.5) pVarTP.x = -pVarTP.x;
        if (mirror_y > 0 && pContext.random() < 0.5) pVarTP.y = -pVarTP.y;
        if (mirror_z > 0 && pContext.random() < 0.5) pVarTP.z = -pVarTP.z;
        
        pVarTP.x += offset_x; pVarTP.y += offset_y; pVarTP.z += offset_z;
    }

    @Override
    public String[] getParameterNames() { return paramNames; }

    @Override
    public Object[] getParameterValues() {
        return new Object[]{
                size, a,
                grid_rot_x, grid_rot_y, grid_rot_z,
                cell_idx_power_x, cell_idx_power_y, cell_idx_power_z,
                dxdydz_mode,
                cell_scale_x, cell_scale_y, cell_scale_z,
                cell_power_x, cell_power_y, cell_power_z,
                cell_rot_x, cell_rot_y, cell_rot_z,
                space_xa, space_ya, space_xb, space_yb, move_xa,
                space_xc, space_yc, move_ya, space_xd, space_yd,
                move_xb, move_yb, move_tr_x, move_tr_y, move_br_x,
                space_z, move_z,
                checkerboard_mode, checkerboard_invert,
                space_xa2, space_ya2, space_xb2, space_yb2, move_xa2,
                space_xc2, space_yc2, move_ya2, space_xd2, space_yd2,
                move_xb2, move_yb2, move_tr_x2, move_tr_y2, move_br_x2,
                space_z2, move_z2,
                mirror_x, mirror_y, mirror_z,
                invert_y_output, invert_z_output,
                offset_x, offset_y, offset_z,
                color_mode,
                cidx_scale_x, cidx_scale_y, cidx_scale_z, cidx_offset,
                cdist_scale,
                color_q1, color_q2, color_q3, color_q4,
                color_use_z_sign, color_z_neg_offset,
                cz_scale, cz_offset
        };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_SIZE.equalsIgnoreCase(pName)) size = pValue;
        else if (PARAM_A.equalsIgnoreCase(pName)) a = pValue;
        else if (PARAM_GRID_ROT_X.equalsIgnoreCase(pName)) grid_rot_x = pValue;
        else if (PARAM_GRID_ROT_Y.equalsIgnoreCase(pName)) grid_rot_y = pValue;
        else if (PARAM_GRID_ROT_Z.equalsIgnoreCase(pName)) grid_rot_z = pValue;
        else if (PARAM_CELL_IDX_POWER_X.equalsIgnoreCase(pName)) cell_idx_power_x = pValue;
        else if (PARAM_CELL_IDX_POWER_Y.equalsIgnoreCase(pName)) cell_idx_power_y = pValue;
        else if (PARAM_CELL_IDX_POWER_Z.equalsIgnoreCase(pName)) cell_idx_power_z = pValue;
        else if (PARAM_DXDYDZ_MODE.equalsIgnoreCase(pName)) dxdydz_mode = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_CELL_SCALE_X.equalsIgnoreCase(pName)) cell_scale_x = pValue;
        else if (PARAM_CELL_SCALE_Y.equalsIgnoreCase(pName)) cell_scale_y = pValue;
        else if (PARAM_CELL_SCALE_Z.equalsIgnoreCase(pName)) cell_scale_z = pValue; // Ensured this line is correct
        else if (PARAM_CELL_POWER_X.equalsIgnoreCase(pName)) cell_power_x = pValue;
        else if (PARAM_CELL_POWER_Y.equalsIgnoreCase(pName)) cell_power_y = pValue;
        else if (PARAM_CELL_POWER_Z.equalsIgnoreCase(pName)) cell_power_z = pValue;
        else if (PARAM_CELL_ROT_X.equalsIgnoreCase(pName)) cell_rot_x = pValue;
        else if (PARAM_CELL_ROT_Y.equalsIgnoreCase(pName)) cell_rot_y = pValue;
        else if (PARAM_CELL_ROT_Z.equalsIgnoreCase(pName)) cell_rot_z = pValue;
        // Primary Space/Move
        else if (SPACE_XA.equalsIgnoreCase(pName)) space_xa = pValue; else if (SPACE_YA.equalsIgnoreCase(pName)) space_ya = pValue;
        else if (SPACE_XB.equalsIgnoreCase(pName)) space_xb = pValue; else if (SPACE_YB.equalsIgnoreCase(pName)) space_yb = pValue;
        else if (MOVE_XA.equalsIgnoreCase(pName)) move_xa = pValue;
        else if (SPACE_XC.equalsIgnoreCase(pName)) space_xc = pValue; else if (SPACE_YC.equalsIgnoreCase(pName)) space_yc = pValue;
        else if (MOVE_YA.equalsIgnoreCase(pName)) move_ya = pValue;
        else if (SPACE_XD.equalsIgnoreCase(pName)) space_xd = pValue; else if (SPACE_YD.equalsIgnoreCase(pName)) space_yd = pValue;
        else if (MOVE_XB.equalsIgnoreCase(pName)) move_xb = pValue; else if (MOVE_YB.equalsIgnoreCase(pName)) move_yb = pValue;
        else if (PARAM_MOVE_TR_X.equalsIgnoreCase(pName)) move_tr_x = pValue; else if (PARAM_MOVE_TR_Y.equalsIgnoreCase(pName)) move_tr_y = pValue;
        else if (PARAM_MOVE_BR_X.equalsIgnoreCase(pName)) move_br_x = pValue;
        else if (PARAM_SPACE_Z.equalsIgnoreCase(pName)) space_z = pValue; else if (PARAM_MOVE_Z.equalsIgnoreCase(pName)) move_z = pValue;
        // Checkerboard
        else if (PARAM_CHECKERBOARD_MODE.equalsIgnoreCase(pName)) checkerboard_mode = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_CHECKERBOARD_INVERT.equalsIgnoreCase(pName)) checkerboard_invert = limitIntVal(Tools.FTOI(pValue), 0, 1);
        // Alt Space/Move
        else if (SPACE_XA2.equalsIgnoreCase(pName)) space_xa2 = pValue; else if (SPACE_YA2.equalsIgnoreCase(pName)) space_ya2 = pValue;
        else if (SPACE_XB2.equalsIgnoreCase(pName)) space_xb2 = pValue; else if (SPACE_YB2.equalsIgnoreCase(pName)) space_yb2 = pValue;
        else if (MOVE_XA2.equalsIgnoreCase(pName)) move_xa2 = pValue;
        else if (SPACE_XC2.equalsIgnoreCase(pName)) space_xc2 = pValue; else if (SPACE_YC2.equalsIgnoreCase(pName)) space_yc2 = pValue;
        else if (MOVE_YA2.equalsIgnoreCase(pName)) move_ya2 = pValue;
        else if (SPACE_XD2.equalsIgnoreCase(pName)) space_xd2 = pValue; else if (SPACE_YD2.equalsIgnoreCase(pName)) space_yd2 = pValue;
        else if (MOVE_XB2.equalsIgnoreCase(pName)) move_xb2 = pValue; else if (MOVE_YB2.equalsIgnoreCase(pName)) move_yb2 = pValue;
        else if (PARAM_MOVE_TR_X2.equalsIgnoreCase(pName)) move_tr_x2 = pValue; else if (PARAM_MOVE_TR_Y2.equalsIgnoreCase(pName)) move_tr_y2 = pValue;
        else if (PARAM_MOVE_BR_X2.equalsIgnoreCase(pName)) move_br_x2 = pValue;
        else if (PARAM_SPACE_Z2.equalsIgnoreCase(pName)) space_z2 = pValue; else if (PARAM_MOVE_Z2.equalsIgnoreCase(pName)) move_z2 = pValue;
        // Output
        else if (PARAM_MIRROR_X.equalsIgnoreCase(pName)) mirror_x = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_MIRROR_Y.equalsIgnoreCase(pName)) mirror_y = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_MIRROR_Z.equalsIgnoreCase(pName)) mirror_z = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_INVERT_Y_OUTPUT.equalsIgnoreCase(pName)) invert_y_output = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_INVERT_Z_OUTPUT.equalsIgnoreCase(pName)) invert_z_output = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_OFFSET_X.equalsIgnoreCase(pName)) offset_x = pValue;
        else if (PARAM_OFFSET_Y.equalsIgnoreCase(pName)) offset_y = pValue;
        else if (PARAM_OFFSET_Z.equalsIgnoreCase(pName)) offset_z = pValue;
        // Coloring
        else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) color_mode = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_CIDX_SCALE_X.equalsIgnoreCase(pName)) cidx_scale_x = pValue;
        else if (PARAM_CIDX_SCALE_Y.equalsIgnoreCase(pName)) cidx_scale_y = pValue;
        else if (PARAM_CIDX_SCALE_Z.equalsIgnoreCase(pName)) cidx_scale_z = pValue;
        else if (PARAM_CIDX_OFFSET.equalsIgnoreCase(pName)) cidx_offset = pValue;
        else if (PARAM_CDIST_SCALE.equalsIgnoreCase(pName)) cdist_scale = pValue;
        else if (PARAM_COLOR_Q1.equalsIgnoreCase(pName)) color_q1 = normalizeColor(pValue);
        else if (PARAM_COLOR_Q2.equalsIgnoreCase(pName)) color_q2 = normalizeColor(pValue);
        else if (PARAM_COLOR_Q3.equalsIgnoreCase(pName)) color_q3 = normalizeColor(pValue);
        else if (PARAM_COLOR_Q4.equalsIgnoreCase(pName)) color_q4 = normalizeColor(pValue);
        else if (PARAM_COLOR_USE_Z_SIGN.equalsIgnoreCase(pName)) color_use_z_sign = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_COLOR_Z_NEG_OFFSET.equalsIgnoreCase(pName)) color_z_neg_offset = pValue;
        else if (PARAM_CZ_SCALE.equalsIgnoreCase(pName)) cz_scale = pValue;
        else if (PARAM_CZ_OFFSET.equalsIgnoreCase(pName)) cz_offset = pValue;
        else throw new IllegalArgumentException(pName);
    }

    @Override
    public String[] getRessourceNames() {
      return ressourceNames;
    }

    @Override
    public byte[][] getRessourceValues() {
      return new byte[][] {description.getBytes()};
    }

    @Override
    public RessourceType getRessourceType(String pName) {
      if (RESSOURCE_DESCRIPTION.equalsIgnoreCase(pName)) {
        return RessourceType.REFERENCE;
      }
      else throw new IllegalArgumentException(pName);
    }
    
    @Override
    public String getName() { return "cell3D"; }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_DC};
    }
}