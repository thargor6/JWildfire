package org.jwildfire.create.tina.variation; // Assumed package

// Base JWildfire classes
//import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

// Base Variation classes
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncType;

// Utilities
import org.jwildfire.base.Tools;
// Removed static import for MathLib, using standard java.lang.Math now
// import static org.jwildfire.base.mathlib.MathLib.*;


/**
Â * KIFS3DFunc - A JWildfire Variation implementing Kaleidoscopic Iterated Function Systems.
Â * Includes non-uniform scaling, post-symmetry option, multiple color modes,
Â * enhanced plane reflections, edge folding, and selectable rotation/scaling pivots.
Â * Based on https://web.ics.purdue.edu/~tmcgraw/papers/kifs_mcgraw_2015.pdf by Tim McGraw
Â * and features adapted from other IFS implementations.
Â *
Â * * 
 */
public class KIFS3DFunc extends VariationFunc {

    // --- Parameter Definitions (String Constants) ---
    // Iteration & Bailout
    private static final String PARAM_MAX_ITER = "max_iter";
    private static final String PARAM_BAILOUT_RADIUS = "bailout_radius";
    // Scaling
    private static final String PARAM_KIFS_SCALE_X = "kifs_scale_x";
    private static final String PARAM_KIFS_SCALE_Y = "kifs_scale_y";
    private static final String PARAM_KIFS_SCALE_Z = "kifs_scale_z";
    private static final String PARAM_SCALE_PIVOT_TYPE = "scale_pivot"; // 0=Center, 1=Offset
    // Center / Offset
    private static final String PARAM_CENTER_X = "center_x";
    private static final String PARAM_CENTER_Y = "center_y";
    private static final String PARAM_CENTER_Z = "center_z";
    private static final String PARAM_OFFSET_X = "offset_x";
    private static final String PARAM_OFFSET_Y = "offset_y";
    private static final String PARAM_OFFSET_Z = "offset_z";
    // Folding
    private static final String PARAM_MIRROR_FOLD = "mirror_fold"; // NEW: 0=fold only, 1=mirror fold
    private static final String PARAM_FOLD_TYPE = "fold_type";
    private static final String PARAM_FOLD_PLANE1_NX = "fold_plane1_nx";
    private static final String PARAM_FOLD_PLANE1_NY = "fold_plane1_ny";
    private static final String PARAM_FOLD_PLANE1_NZ = "fold_plane1_nz";
    private static final String PARAM_FOLD_PLANE1_DIST = "fold_plane1_dist"; // Distance for custom plane
    private static final String PARAM_FOLD_PLANE1_INTENSITY = "fold_plane1_intensity"; // Intensity for custom plane reflection
    private static final String PARAM_EDGE_X = "edge_x"; // X boundary for edge fold
    private static final String PARAM_EDGE_Y = "edge_y"; // Y boundary for edge fold
    private static final String PARAM_EDGE_Z = "edge_z"; // Z boundary for edge fold
    // Rotation
    private static final String PARAM_ROT_X = "rot_x";
    private static final String PARAM_ROT_Y = "rot_y";
    private static final String PARAM_ROT_Z = "rot_z";
    private static final String PARAM_ROT_ORDER = "rot_order";
    private static final String PARAM_ROT_CENTER_TYPE = "rot_pivot"; // 0=Origin, 1=Center, 2=Offset
    // Ordering & Post-Processing
    private static final String PARAM_TRANSFORM_ORDER = "transform_order";
    private static final String PARAM_POST_SYMMETRY = "post_symmetry";
    // Coloring
    private static final String PARAM_COLOR_MODE = "color_mode";
    private static final String PARAM_COLOR_SCALE = "color_scale";

    // --- Parameter Names Array (Order Matters!) ---  // MODIFIED: Added PARAM_MIRROR_FOLD
    private static final String[] paramNames = {
            PARAM_MAX_ITER, PARAM_BAILOUT_RADIUS,
            PARAM_KIFS_SCALE_X, PARAM_KIFS_SCALE_Y, PARAM_KIFS_SCALE_Z, PARAM_SCALE_PIVOT_TYPE,
            PARAM_CENTER_X, PARAM_CENTER_Y, PARAM_CENTER_Z,
            PARAM_OFFSET_X, PARAM_OFFSET_Y, PARAM_OFFSET_Z,
            PARAM_MIRROR_FOLD, PARAM_FOLD_TYPE,
            PARAM_FOLD_PLANE1_NX, PARAM_FOLD_PLANE1_NY, PARAM_FOLD_PLANE1_NZ,
            PARAM_FOLD_PLANE1_DIST, PARAM_FOLD_PLANE1_INTENSITY,
            PARAM_EDGE_X, PARAM_EDGE_Y, PARAM_EDGE_Z,
            PARAM_ROT_X, PARAM_ROT_Y, PARAM_ROT_Z, PARAM_ROT_ORDER, PARAM_ROT_CENTER_TYPE,
            PARAM_TRANSFORM_ORDER,
            PARAM_POST_SYMMETRY,
            PARAM_COLOR_MODE, PARAM_COLOR_SCALE
    };

    // --- Parameter Values (Instance Variables) & Defaults --- // MODIFIED: Added mirror_fold
    // Iteration & Bailout
    private int max_iter = 7;
    private double bailout_radius = 1.1;
    // Scaling
    private double kifs_scale_x = 1.4;
    private double kifs_scale_y = 1.4;
    private double kifs_scale_z = -1.0;
    private int scale_pivot_type = 0;
    // Center / Offset
    private double center_x = 0.0;
    private double center_y = 0.0;
    private double center_z = 0.0;
    private double offset_x = -1.0;
    private double offset_y = -1.0;
    private double offset_z = 1.0;
    // Folding
    private int mirror_fold = 0;
    // Fold Type Constants
    private static final int FOLD_TYPE_NONE = 0;
    private static final int FOLD_TYPE_ABS_XYZ = 1;
    private static final int FOLD_TYPE_DIAG_XY_POS = 2;
    private static final int FOLD_TYPE_CUSTOM1 = 3;
    private static final int FOLD_TYPE_EDGE = 4;
    private int fold_type = FOLD_TYPE_ABS_XYZ;
    // Custom Plane 1 Params
    private double fold_plane1_nx = 1.0;
    private double fold_plane1_ny = 0.0;
    private double fold_plane1_nz = 0.0;
    private double fold_plane1_dist = 0.0;
    private double fold_plane1_intensity = 1.0;
    // Edge Fold Params
    private double edge_x = 0.0;
    private double edge_y = 0.0;
    private double edge_z = 0.0;
    // Rotation
    private double rot_x = 5.0; // Degrees
    private double rot_y = 5.0; // Degrees
    private double rot_z = 5.0; // Degrees
    // Rotation Order Constants
    private static final int ROT_ORDER_XYZ = 0;
    private static final int ROT_ORDER_ZYX = 1;
    private int rot_order = ROT_ORDER_XYZ;
    // Rotation Center Type Constants - NEW
    private static final int ROT_CENTER_ORIGIN = 0;
    private static final int ROT_CENTER_CENTER_PARAM = 1;
    private static final int ROT_CENTER_OFFSET_PARAM = 2;
    private int rot_center_type = ROT_CENTER_ORIGIN;
    // Ordering & Post-Processing
    // Transform Order Constants
    private static final int TRANSFORM_ORDER_FOLD_ROT_SCALE = 0;
    private static final int TRANSFORM_ORDER_ROT_FOLD_SCALE = 1;
    private static final int TRANSFORM_ORDER_SCALE_FOLD_ROT = 2;
    private int transform_order = TRANSFORM_ORDER_FOLD_ROT_SCALE;
    // Post-Symmetry Type Constants (Bitmask)
    private static final int POST_SYM_NONE = 0;
    private static final int POST_SYM_X = 1;
    private static final int POST_SYM_Y = 2;
    private static final int POST_SYM_Z = 4;
    private int post_symmetry = 7;
    // Coloring
    // Color Mode Constants
    private static final int COLOR_MODE_ITER = 0;
    private static final int COLOR_MODE_FINAL_R = 1;
    private static final int COLOR_MODE_FINAL_XY_ANGLE = 2;
    private static final int COLOR_MODE_FINAL_X = 3;
    private static final int COLOR_MODE_FINAL_Y = 4;
    private static final int COLOR_MODE_FINAL_Z = 5;
    private int color_mode = COLOR_MODE_ITER;
    private double color_scale = 1.0;

    // --- Precomputed values ---
    private double bailout_sq;
    private double rot_x_rad, rot_y_rad, rot_z_rad;
    private double fold_norm1_len_sq;


    // --- JWildfire Variation Boilerplate ---

    // Constructor
    public KIFS3DFunc() {
        super();
    }

    // --- Parameter Handling Methods ---

    @Override
    public String[] getParameterNames() {
        return paramNames; // Updated array reference
    }

    @Override
    public Object[] getParameterValues() {
        // Return values in the SAME order as paramNames // MODIFIED: Added mirror_fold
        return new Object[]{
                max_iter, bailout_radius,
                kifs_scale_x, kifs_scale_y, kifs_scale_z, scale_pivot_type,
                center_x, center_y, center_z,
                offset_x, offset_y, offset_z,
                mirror_fold, fold_type,
                fold_plane1_nx, fold_plane1_ny, fold_plane1_nz,
                fold_plane1_dist, fold_plane1_intensity,
                edge_x, edge_y, edge_z,
                rot_x, rot_y, rot_z, rot_order, rot_center_type,
                transform_order,
                post_symmetry,
                color_mode, color_scale
        };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        // Set the value of the parameter identified by pName // MODIFIED: Added mirror_fold
        if (PARAM_MAX_ITER.equalsIgnoreCase(pName)) max_iter = (int) pValue;
        else if (PARAM_BAILOUT_RADIUS.equalsIgnoreCase(pName)) bailout_radius = pValue;
        else if (PARAM_KIFS_SCALE_X.equalsIgnoreCase(pName)) kifs_scale_x = pValue;
        else if (PARAM_KIFS_SCALE_Y.equalsIgnoreCase(pName)) kifs_scale_y = pValue;
        else if (PARAM_KIFS_SCALE_Z.equalsIgnoreCase(pName)) kifs_scale_z = pValue;
        else if (PARAM_SCALE_PIVOT_TYPE.equalsIgnoreCase(pName)) scale_pivot_type = (int) pValue;
        else if (PARAM_CENTER_X.equalsIgnoreCase(pName)) center_x = pValue;
        else if (PARAM_CENTER_Y.equalsIgnoreCase(pName)) center_y = pValue;
        else if (PARAM_CENTER_Z.equalsIgnoreCase(pName)) center_z = pValue;
        else if (PARAM_OFFSET_X.equalsIgnoreCase(pName)) offset_x = pValue;
        else if (PARAM_OFFSET_Y.equalsIgnoreCase(pName)) offset_y = pValue;
        else if (PARAM_OFFSET_Z.equalsIgnoreCase(pName)) offset_z = pValue;
        else if (PARAM_MIRROR_FOLD.equalsIgnoreCase(pName)) mirror_fold = (int) pValue;
        else if (PARAM_FOLD_TYPE.equalsIgnoreCase(pName)) fold_type = (int) pValue;
        else if (PARAM_FOLD_PLANE1_NX.equalsIgnoreCase(pName)) fold_plane1_nx = pValue;
        else if (PARAM_FOLD_PLANE1_NY.equalsIgnoreCase(pName)) fold_plane1_ny = pValue;
        else if (PARAM_FOLD_PLANE1_NZ.equalsIgnoreCase(pName)) fold_plane1_nz = pValue;
        else if (PARAM_FOLD_PLANE1_DIST.equalsIgnoreCase(pName)) fold_plane1_dist = pValue;
        else if (PARAM_FOLD_PLANE1_INTENSITY.equalsIgnoreCase(pName)) fold_plane1_intensity = pValue;
        else if (PARAM_EDGE_X.equalsIgnoreCase(pName)) edge_x = pValue;
        else if (PARAM_EDGE_Y.equalsIgnoreCase(pName)) edge_y = pValue;
        else if (PARAM_EDGE_Z.equalsIgnoreCase(pName)) edge_z = pValue;
        else if (PARAM_ROT_X.equalsIgnoreCase(pName)) rot_x = pValue;
        else if (PARAM_ROT_Y.equalsIgnoreCase(pName)) rot_y = pValue;
        else if (PARAM_ROT_Z.equalsIgnoreCase(pName)) rot_z = pValue;
        else if (PARAM_ROT_ORDER.equalsIgnoreCase(pName)) rot_order = (int) pValue;
        else if (PARAM_ROT_CENTER_TYPE.equalsIgnoreCase(pName)) rot_center_type = (int) pValue;
        else if (PARAM_TRANSFORM_ORDER.equalsIgnoreCase(pName)) transform_order = (int) pValue;
        else if (PARAM_POST_SYMMETRY.equalsIgnoreCase(pName)) post_symmetry = (int) pValue;
        else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) color_mode = (int) pValue;
        else if (PARAM_COLOR_SCALE.equalsIgnoreCase(pName)) color_scale = pValue;
        else throw new IllegalArgumentException("Unknown parameter name: " + pName);
    }

    // --- Initialization ---
    /**
     * Initializes precomputed values.
     */
    @Override
    public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
        bailout_sq = bailout_radius * bailout_radius;
        if (bailout_sq <= 0) bailout_sq = 1e-6;

        rot_x_rad = Math.toRadians(rot_x);
        rot_y_rad = Math.toRadians(rot_y);
        rot_z_rad = Math.toRadians(rot_z);

        // Precompute length squared for custom fold normal(s)
        fold_norm1_len_sq = fold_plane1_nx * fold_plane1_nx + fold_plane1_ny * fold_plane1_ny + fold_plane1_nz * fold_plane1_nz;
        // Ensure intensity is non-negative
        if(fold_plane1_intensity < 0) fold_plane1_intensity = 0;
    }

    // --- The Core Transformation Logic ---
    /**
     * Applies the KIFS transformation.
     */
    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

        double x = pAffineTP.x;
        double y = pAffineTP.y;
        double z = pAffineTP.z;

        double iter_color = 0.0;
        boolean escaped = false;

        // KIFS internal iteration loop
        for (int i = 0; i < max_iter; i++) {
            double cur_x = x;
            double cur_y = y;
            double cur_z = z;

            Point3D folded, rotated, scaled; // Declare points

            // Determine rotation pivot point - // NEW Pivot Logic
            double rotPivotX = 0.0, rotPivotY = 0.0, rotPivotZ = 0.0;
            if (rot_center_type == ROT_CENTER_CENTER_PARAM) {
                rotPivotX = center_x; rotPivotY = center_y; rotPivotZ = center_z;
            } else if (rot_center_type == ROT_CENTER_OFFSET_PARAM) {
                rotPivotX = offset_x; rotPivotY = offset_y; rotPivotZ = offset_z;
            }
            // Translate to rotation origin, rotate, translate back is handled inside applyRotation call now

            // Apply Transformation Sequence based on transform_order
            if (transform_order == TRANSFORM_ORDER_FOLD_ROT_SCALE) {
                 folded = applyFolding(pContext, cur_x, cur_y, cur_z);
                 rotated = applyRotation(folded.x, folded.y, folded.z, rotPivotX, rotPivotY, rotPivotZ); // Pass pivot
                 scaled = applyScaleTranslate(rotated.x, rotated.y, rotated.z);
                 x = scaled.x; y = scaled.y; z = scaled.z;
            } else if (transform_order == TRANSFORM_ORDER_ROT_FOLD_SCALE) {
                 rotated = applyRotation(cur_x, cur_y, cur_z, rotPivotX, rotPivotY, rotPivotZ); // Pass pivot
                 folded = applyFolding(pContext, rotated.x, rotated.y, rotated.z);
                 scaled = applyScaleTranslate(folded.x, folded.y, folded.z);
                 x = scaled.x; y = scaled.y; z = scaled.z;
            } else if (transform_order == TRANSFORM_ORDER_SCALE_FOLD_ROT) {
                 scaled = applyScaleTranslate(cur_x, cur_y, cur_z);
                 folded = applyFolding(pContext, scaled.x, scaled.y, scaled.z);
                 rotated = applyRotation(folded.x, folded.y, folded.z, rotPivotX, rotPivotY, rotPivotZ); // Pass pivot
                 x = rotated.x; y = rotated.y; z = rotated.z;
            } // Add other orders if needed

            // Bailout Check
            double r_sq = x * x + y * y + z * z;
            if (r_sq > bailout_sq) {
                iter_color = (double) i / max_iter;
                escaped = true;
                break;
            }
        } // End of internal iteration loop

        if (!escaped) {
            iter_color = 1.0; // Assign full color if it didn't escape
        }

        // --- Apply Post-Loop Symmetry --- (Unchanged)
         if (post_symmetry > POST_SYM_NONE) {
             // ... (rest of symmetry code is the same)
            double signX = (pContext.random() < 0.5) ? -1.0 : 1.0;
            double signY = (pContext.random() < 0.5) ? -1.0 : 1.0;
            double signZ = (pContext.random() < 0.5) ? -1.0 : 1.0;

            if ((post_symmetry & POST_SYM_X) != 0) { x *= signX; }
            if ((post_symmetry & POST_SYM_Y) != 0) { y *= signY; }
            if ((post_symmetry & POST_SYM_Z) != 0) { z *= signZ; }
         }

        // --- Calculate Final Color based on color_mode --- (Unchanged)
         double color_value = 0.0;
         double temp_val = 0.0;
         switch (color_mode) {
             // ... (rest of color mode code is the same)
            case COLOR_MODE_FINAL_R: temp_val = Math.sqrt(x * x + y * y + z * z) * color_scale; color_value = temp_val - Math.floor(temp_val); break;
            case COLOR_MODE_FINAL_XY_ANGLE: temp_val = Math.atan2(y, x); color_value = ((temp_val + Math.PI) / (2.0 * Math.PI)) * color_scale; color_value = color_value - Math.floor(color_value); break;
            case COLOR_MODE_FINAL_X: temp_val = x * color_scale; color_value = temp_val - Math.floor(temp_val); break;
            case COLOR_MODE_FINAL_Y: temp_val = y * color_scale; color_value = temp_val - Math.floor(temp_val); break;
            case COLOR_MODE_FINAL_Z: temp_val = z * color_scale; color_value = temp_val - Math.floor(temp_val); break;
            case COLOR_MODE_ITER: default: color_value = iter_color; break;
         }

        // Apply the variation amount (pAmount) (Unchanged)
        double finalX = x * pAmount;
        double finalY = y * pAmount;
        double finalZ = z * pAmount;

        // Write the final coordinates and calculated color index (Unchanged)
        pVarTP.x += finalX;
        pVarTP.y += finalY;
        pVarTP.z += finalZ;
        pVarTP.color = color_value;
    }

    // --- Helper Methods for Transformations ---

    // Internal class to return multiple values (Unchanged)
    private static class Point3D {
        double x, y, z;
        Point3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    // applyFolding // MODIFIED: Added MIRROR_FOLD
    // (pContext needed for random() method
    private Point3D applyFolding(FlameTransformationContext pContext, double x, double y, double z) {
    	if (mirror_fold <= 0) { // fold only
	        switch (fold_type) {
	            case FOLD_TYPE_ABS_XYZ:
	                return new Point3D(Math.abs(x), Math.abs(y), Math.abs(z));
	            case FOLD_TYPE_DIAG_XY_POS:
	                if (x + y < 0.0) { return new Point3D(-y, -x, z); }
	                return new Point3D(x, y, z);
	            case FOLD_TYPE_CUSTOM1:
	                if (fold_norm1_len_sq > 1e-9) {
	                    // Calculate distance from the plane (plane defined by normal n and distance d: n.p - d = 0)
	                    double dot_p_n_minus_d = x * fold_plane1_nx + y * fold_plane1_ny + z * fold_plane1_nz - fold_plane1_dist;
	                    if (dot_p_n_minus_d < 0.0) { // If on the "negative" side of the plane
	                        // Reflect point across the plane p' = p - (1 + intensity) * (n.p - d) / |n|^2 * n
	                        double scale_factor = (1.0 + fold_plane1_intensity) * dot_p_n_minus_d / fold_norm1_len_sq;
	                        return new Point3D(x - scale_factor * fold_plane1_nx,
	                                           y - scale_factor * fold_plane1_ny,
	                                           z - scale_factor * fold_plane1_nz);
	                    }
	                }
	                return new Point3D(x, y, z); // Return original if normal is zero or point is not folded
	            case FOLD_TYPE_EDGE:
	                 // Fold only if edge value is non-zero (positive or negative). Zero means no fold on that axis.
	                 // Folds towards origin relative to the edge boundary.
	                 double foldedX = (edge_x != 0.0) ? edge_x - Math.abs(edge_x - x) : x;
	                 double foldedY = (edge_y != 0.0) ? edge_y - Math.abs(edge_y - y) : y;
	                 double foldedZ = (edge_z != 0.0) ? edge_z - Math.abs(edge_z - z) : z;
	                 return new Point3D(foldedX, foldedY, foldedZ);
	            case FOLD_TYPE_NONE:
	            default:
	                return new Point3D(x, y, z);
	        }
    	} else { // Mirror folding
            double signa = (pContext.random() < 0.5) ? -1.0 : 1.0;
            double signb = (pContext.random() < 0.5) ? -1.0 : 1.0;
            double signc = (pContext.random() < 0.5) ? -1.0 : 1.0;
	        switch (fold_type) {
            case FOLD_TYPE_ABS_XYZ:
                return new Point3D(x * signa, y * signb, z * signc);
            case FOLD_TYPE_DIAG_XY_POS:
                if (signa < 0.0) { return new Point3D(-y, -x, z); }
                return new Point3D(x, y, z);
            case FOLD_TYPE_CUSTOM1:
                if (fold_norm1_len_sq > 1e-9) {
                    // Calculate distance from the plane (plane defined by normal n and distance d: n.p - d = 0)
                    double dot_p_n_minus_d = x * fold_plane1_nx + y * fold_plane1_ny + z * fold_plane1_nz - fold_plane1_dist;
//                    if (dot_p_n_minus_d < 0.0) { // If on the "negative" side of the plane
                    if (signa < 0.0) {
                        // Reflect point across the plane p' = p - (1 + intensity) * (n.p - d) / |n|^2 * n
                        double scale_factor = (1.0 + fold_plane1_intensity) * dot_p_n_minus_d / fold_norm1_len_sq;
                        return new Point3D(x - scale_factor * fold_plane1_nx,
                                           y - scale_factor * fold_plane1_ny,
                                           z - scale_factor * fold_plane1_nz);
                    }
                }
                return new Point3D(x, y, z); // Return original if normal is zero or point is not folded
            case FOLD_TYPE_EDGE:
                 // Fold only if edge value is non-zero (positive or negative). Zero means no fold on that axis.
                 // Folds towards origin relative to the edge boundary.
                 double foldedX = (edge_x != 0.0) ? edge_x - signa * Math.abs(edge_x - x) : x;
                 double foldedY = (edge_y != 0.0) ? edge_y - signb * Math.abs(edge_y - y) : y;
                 double foldedZ = (edge_z != 0.0) ? edge_z - signc * Math.abs(edge_z - z) : z;
                 return new Point3D(foldedX, foldedY, foldedZ);
            case FOLD_TYPE_NONE:
            default:
                return new Point3D(x, y, z);
	        }    		
    	}
    }

    // applyRotation
    private Point3D applyRotation(double x, double y, double z, double pivotX, double pivotY, double pivotZ) {
         if (rot_x_rad == 0.0 && rot_y_rad == 0.0 && rot_z_rad == 0.0) {
             return new Point3D(x, y, z);
         }

         // Translate point so pivot is at origin
         double tx = x - pivotX;
         double ty = y - pivotY;
         double tz = z - pivotZ;

         double cosX = Math.cos(rot_x_rad); double sinX = Math.sin(rot_x_rad);
         double cosY = Math.cos(rot_y_rad); double sinY = Math.sin(rot_y_rad);
         double cosZ = Math.cos(rot_z_rad); double sinZ = Math.sin(rot_z_rad);
         double rx = tx, ry = ty, rz = tz; // Use translated coords
         double temp_x, temp_y, temp_z; // Use temps to avoid overwriting needed values

         if (rot_order == ROT_ORDER_XYZ) {
             // Apply Z rotation
             temp_x = rx * cosZ - ry * sinZ;
             temp_y = rx * sinZ + ry * cosZ;
             rx = temp_x; ry = temp_y;
             // Apply Y rotation
             temp_x = rx * cosY + rz * sinY;
             temp_z = -rx * sinY + rz * cosY;
             rx = temp_x; rz = temp_z;
             // Apply X rotation
             temp_y = ry * cosX - rz * sinX;
             temp_z = ry * sinX + rz * cosX;
             ry = temp_y; rz = temp_z;
         } else if (rot_order == ROT_ORDER_ZYX) {
             // Apply X rotation
             temp_y = ry * cosX - rz * sinX;
             temp_z = ry * sinX + rz * cosX;
             ry = temp_y; rz = temp_z;
             // Apply Y rotation
             temp_x = rx * cosY + rz * sinY;
             temp_z = -rx * sinY + rz * cosY;
             rx = temp_x; rz = temp_z;
             // Apply Z rotation
             temp_x = rx * cosZ - ry * sinZ;
             temp_y = rx * sinZ + ry * cosZ;
             rx = temp_x; ry = temp_y;
         }
         // No else needed if only two orders

         // Translate point back
         rx += pivotX;
         ry += pivotY;
         rz += pivotZ;

         return new Point3D(rx, ry, rz);
    }


    // applyScaleTranslate
    private Point3D applyScaleTranslate(double x, double y, double z) {
         // Determine pivot point for scaling
         double pivotX = center_x, pivotY = center_y, pivotZ = center_z; // Default to center
         boolean pivotIsOffset = false;
         if (scale_pivot_type == 1) { // 1 means use Offset as pivot
             pivotX = offset_x;
             pivotY = offset_y;
             pivotZ = offset_z;
             pivotIsOffset = true;
         }

         // Apply scaling relative to the chosen pivot point
         double sx = kifs_scale_x * (x - pivotX) + pivotX;
         double sy = kifs_scale_y * (y - pivotY) + pivotY;
         double sz = kifs_scale_z * (z - pivotZ) + pivotZ;

         // Apply translation/offset ONLY IF the pivot was NOT the offset itself
         // (Avoids applying offset twice if scaling is already relative to it)
         if (!pivotIsOffset) {
             sx += offset_x;
             sy += offset_y;
             sz += offset_z;
         }

         return new Point3D(sx, sy, sz);
    }


    // --- Variation Naming & Type --- (Unchanged)

    @Override
    public String getName() {
        return "kifs3d"; 
    }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_3D};
    }
}
