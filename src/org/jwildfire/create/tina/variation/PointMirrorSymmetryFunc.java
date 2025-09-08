package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.Complex;
import static org.jwildfire.base.mathlib.MathLib.*;
import java.util.Random;

public class PointMirrorSymmetryFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SYMMETRY = "symmetry";
  private static final String PARAM_SIDECROP = "side crop enable";
  private static final String PARAM_CROPEND = "crop end enable";
  private static final String PARAM_CROPENDDIST = "crop end distance";  
  private static final String PARAM_ZXMUL = "zx Multiply";
  private static final String PARAM_ZYMUL = "zy Multiply";
  private static final String PARAM_COLLIDO = "collidoscopic";
  private static final String PARAM_COLOR_PRESERVE = "color_preserve";
  private static final String PARAM_BUFFER_SIZE = "buffer_size";

  private static final String[] paramNames = { 
      PARAM_SYMMETRY, PARAM_SIDECROP, PARAM_CROPEND, PARAM_CROPENDDIST, 
      PARAM_ZXMUL, PARAM_ZYMUL, PARAM_COLLIDO, PARAM_COLOR_PRESERVE, 
      PARAM_BUFFER_SIZE 
  };
  
  private int symmetry = 3;
  private int sideCrop = 1;
  private int cropEnd = 1;
  private double cropEndDist = 1.0;
  private double zx = 1.0;  
  private double zy = 0.0;
  private int collidoscopic = 1;
  private int colorPreserve = 0;
  private int bufferSize = 12;
  
  // Buffer for point storage
  private double[] bufferX;
  private double[] bufferY;
  private double[] bufferZ;
  private double[] bufferColor;
  private int bufferIndex = 0;
  private boolean hasValidPoints = false;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    initBuffers();
  }
  
  private void initBuffers() {
    bufferX = new double[bufferSize];
    bufferY = new double[bufferSize];
    bufferZ = new double[bufferSize];
    bufferColor = new double[bufferSize];
    
    // Initialize with small non-zero values to prevent issues
    for (int i = 0; i < bufferSize; i++) {
      bufferX[i] = 0.1;
      bufferY[i] = 0.1;
      bufferZ[i] = 0.0;
      bufferColor[i] = 0.5;
    }
    
    bufferIndex = 0;
    hasValidPoints = false;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Point_Mirror_Symmetry by Whittaker Courtney with improved buffer dot removal

    // Ensure buffers are initialized
    if (bufferX == null) {
      initBuffers();
    }

    // Generate random number from 0 to symmetry number
    Random rand = new Random();
    int randSym = rand.nextInt(symmetry);

    // Radian angle based on number of symmetry
    double rotationSym = (M_PI / (double)symmetry);
    // Rotation in radians * random number of symmetry * 2 (so it's every other)
    double rotationRS = rotationSym * randSym * 2;

    double x, y, z, x1, y1, z1, x2;
    x = x1 = pAffineTP.x;
    y = y1 = pAffineTP.y;
    z = z1 = pAffineTP.z;

    // Complex Multiplication for rotation and scale
    Complex zz1 = new Complex(x1, y1);
    Complex zzv = new Complex(zx, zy);
    
    zz1.Mul(zzv);
    x = zz1.re;
    y = zz1.im;

    // Crop sides of triangle based on line angle to fit symmetry value
    if (sideCrop == 1) {
        // Radius
        double r = sqrt(x*x + y*y);

        // Crop line for starting crop triangle
        double xLine = r * cos(rotationSym);

        if (collidoscopic == 1) {
            if (y < 0) {
                x = 0;
                y = 0;
            }
        }

        // Crop out a section in proportion to the symmetry if x is less than the line angle
        if (x < xLine) {
            x = 0;
            y = 0;
        }

        // Calculate angled crop lines
        x2 = (x - 0) * cos(rotationSym) + (y - 0) * sin(rotationSym);
    
        // Crop end edges
        if (cropEnd == 1) {
            if (x2 > cropEndDist) {
                x = 0;
                y = 0;     
            }
        }
        
        x2 = (x - 0) * cos(-rotationSym) + (y - 0) * sin(-rotationSym);

        if (cropEnd == 1) {
            if (x2 > cropEndDist) {
                x = 0;
                y = 0;     
            }
        }

        // Improved dot removal using buffer
        if (x == 0 && y == 0) {
            if (hasValidPoints) {
                // Use a random point from the buffer
                int idx = pContext.random(bufferSize);
                x = bufferX[idx];
                y = bufferY[idx];
                z = bufferZ[idx];
                
                // Handle color based on preservation setting
                if (colorPreserve == 0) {
                    pVarTP.color = bufferColor[idx];
                }
                // For colorPreserve == 1, we keep the original color
            }
        } else {
            // Store this point in our buffer
            bufferX[bufferIndex] = x;
            bufferY[bufferIndex] = y;
            bufferZ[bufferIndex] = z;
            bufferColor[bufferIndex] = pVarTP.color;
            
            // Mark that we have valid points and increment buffer index
            hasValidPoints = true;
            bufferIndex = (bufferIndex + 1) % bufferSize;
        }
    }

    // Mirror vertically
    if (collidoscopic == 1) {
        if (pContext.random() < 0.5) {         
            y = -y;
        }
    }

    if (randSym % 2 == 0) {
        y = -y;
    }

    // Rotate each cut section by a random int up to the symmetry value
    x1 = x * cos(rotationRS) - y * sin(rotationRS);
    y1 = y * cos(rotationRS) + x * sin(rotationRS);

    // Assign final values
    pVarTP.x = pAmount * x1;  
    pVarTP.y = pAmount * y1; 
    pVarTP.z = pAmount * z;   
           
    if (pContext.isPreserveZCoordinate()) {
        pVarTP.z = pAmount * pAffineTP.z;
    }
  }
  
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { 
        symmetry, sideCrop, cropEnd, cropEndDist, 
        zx, zy, collidoscopic, colorPreserve, 
        bufferSize 
    };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SYMMETRY.equalsIgnoreCase(pName))
      symmetry = limitIntVal(Tools.FTOI(pValue), 1, 99); 
    else if (PARAM_SIDECROP.equalsIgnoreCase(pName))
      sideCrop = limitIntVal(Tools.FTOI(pValue), 0, 1); 
    else if (PARAM_CROPEND.equalsIgnoreCase(pName))
      cropEnd = limitIntVal(Tools.FTOI(pValue), 0, 1); 
    else if (PARAM_CROPENDDIST.equalsIgnoreCase(pName))
      cropEndDist = pValue;   
    else if (PARAM_ZXMUL.equalsIgnoreCase(pName))
      zx = pValue;
    else if (PARAM_ZYMUL.equalsIgnoreCase(pName))
      zy = pValue;        
    else if (PARAM_COLLIDO.equalsIgnoreCase(pName))
      collidoscopic = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_COLOR_PRESERVE.equalsIgnoreCase(pName)) {
      colorPreserve = limitIntVal(Tools.FTOI(pValue), 0, 1);
    }
    else if (PARAM_BUFFER_SIZE.equalsIgnoreCase(pName)) {
      int newSize = limitIntVal(Tools.FTOI(pValue), 1, 1000);
      if (newSize != bufferSize) {
        bufferSize = newSize;
        initBuffers();  // Reinitialize buffers when size changes
      }
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "point_mirror_symmetry";
  }
  

  
  @Override
  public int getPriority() {
    return 0;
  }
  
}
