/*
 * JWildfire - an image and animation processor written in Java
 * Copyright (C) 1995-2021 Andreas Maschke
 */
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import static java.lang.Math.*;

public class TessShapeFunc extends VariationFunc {
    private static final long serialVersionUID = 1L;

    // Parameter Name Constants
    private static final String PARAM_STRENGTH = "strength";
    private static final String PARAM_SHAPE_SIZE = "shapeSize";
    private static final String PARAM_SHAPE_TYPE = "shapeType";
    private static final String PARAM_SHAPE_ASPECT_RATIO = "shapeAspectRatio";

    private static final String PARAM_LR_RADIAL_AMP = "lrRadialAmp";
    private static final String PARAM_LR_RADIAL_WIDTH_FREQ = "lrRadialWidthFreq";
    private static final String PARAM_TB_AMP = "tbAmp";
    private static final String PARAM_TB_WIDTH_FREQ = "tbWidthFreq";
    private static final String PARAM_LR_RADIAL_POS_PHASE_DUTY = "lrRadialPosPhaseDuty";
    private static final String PARAM_TB_POS_PHASE_DUTY = "tbPosPhaseDuty";
    private static final String PARAM_LR_RADIAL_PROFILE_TYPE = "lrRadialProfileType";
    private static final String PARAM_TB_PROFILE_TYPE = "tbProfileType";

    private static final String PARAM_OPERATION_MODE = "operationMode";

    private static final String[] paramNames = {
            PARAM_STRENGTH, PARAM_SHAPE_SIZE, PARAM_SHAPE_TYPE, PARAM_SHAPE_ASPECT_RATIO,
            PARAM_LR_RADIAL_AMP, PARAM_LR_RADIAL_WIDTH_FREQ, PARAM_TB_AMP, PARAM_TB_WIDTH_FREQ,
            PARAM_LR_RADIAL_POS_PHASE_DUTY, PARAM_TB_POS_PHASE_DUTY, PARAM_LR_RADIAL_PROFILE_TYPE, PARAM_TB_PROFILE_TYPE,
            PARAM_OPERATION_MODE
    };

    // Member variables
    private double strength = 1.0;
    private double shapeSize = 0.8;
    private int shapeType = 0; 
    private double shapeAspectRatio = 1.0;

    private double lrRadialAmp = 0.2;
    private double lrRadialWidthFreq = 0.6;
    private double tbAmp = 0.2;
    private double tbWidthFreq = 0.6;
    private double lrRadialPosPhaseDuty = 0.0;
    private double tbPosPhaseDuty = 0.0;
    private int lrRadialProfileType = 0; 
    private int tbProfileType = 0;

    private int operationMode = 0;

    private static final double SQRT3 = sqrt(3.0);
    private static final int MAX_REJECTION_SAMPLES = 100;
    private static final double TWO_PI = 2.0 * PI;

    // v_input: For Cosine Bump (type 0), this is v_on_edge_normalized (-1 to 1).
    //          For periodic waves (types 1-4), this is t_norm (0 to 1).
    // profile_type: 0=Cosine Bump, 1=Sine, 2=Triangle, 3=Square, 4=Sawtooth
    // amplitude: peak height/depth
    // param_wf: WidthFactor for Bump; Frequency for Waves
    // param_ppd: PositionOffset for Bump; Phase for Sine/Triangle/Sawtooth; DutyCycle for Square
    private double calculate_profile_offset(double v_input,
                                            int profile_type, // Expects int
                                            double amplitude,
                                            double param_wf,
                                            double param_ppd) {
        if (amplitude == 0.0) return 0.0;
        
        // For periodic waves (types 1-4), their logic expects t_norm (0 to 1) as the primary domain.
        // Cosine bump (type 0) uses v_input (-1 to 1) directly with its position_offset.
        // The mapping from v_on_edge_normalized (-1 to 1) to t_norm (0 to 1) is done at call site if needed for v_input.

        if (profile_type != 0 && (abs(param_wf) <= 1e-6)) return 0.0; // Check for waves frequency

        switch (profile_type) {
            case 0: { // Cosine Bump (expects v_input as v_on_edge_normalized: -1 to 1)
                double profile_width_factor = param_wf;
                double position_offset_norm = param_ppd; 
                if (profile_width_factor <= 1e-6) return 0.0;

                double effective_v = v_input - position_offset_norm;
                double half_profile_width_norm = profile_width_factor / 2.0;

                if (abs(effective_v) < half_profile_width_norm) {
                    double angle_bump = (effective_v / half_profile_width_norm) * PI;
                    return amplitude * (1.0 + cos(angle_bump)) / 2.0;
                }
                return 0.0;
            }
            // For periodic waves (1-4), v_input is expected to be t_norm (0 to 1)
            case 1: { // Sine Wave
                double frequency = param_wf;
                double phase_norm = param_ppd; 
                return amplitude * sin(v_input * frequency * TWO_PI + phase_norm * TWO_PI);
            }
            case 2: { // Triangle Wave
                double frequency = param_wf;
                double phase_norm = param_ppd;
                double scaled_t = v_input * frequency + phase_norm;
                double period_t = scaled_t - floor(scaled_t);
                double y_tri_01 = (period_t < 0.5) ? (2.0 * period_t) : (2.0 * (1.0 - period_t));
                return amplitude * (y_tri_01 * 2.0 - 1.0);
            }
            case 3: { // Square Wave
                double frequency = param_wf;
                double duty_cycle = max(0.01, min(0.99, abs(param_ppd))); 
                double scaled_t = v_input * frequency; 
                double period_t = scaled_t - floor(scaled_t);
                return (period_t < duty_cycle) ? amplitude : -amplitude;
            }
            case 4: { // Sawtooth Wave
                double frequency = param_wf;
                double phase_norm = param_ppd;
                double scaled_t = v_input * frequency + phase_norm;
                double period_t = scaled_t - floor(scaled_t);
                return amplitude * (2.0 * period_t - 1.0);
            }
            default:
                return 0.0;
        }
    }

    // Helper for point-in-triangle check
    private boolean isPointInTriangle(double px, double py, double v1x, double v1y, double v2x, double v2y, double v3x, double v3y) {
        double d1 = (px-v2x)*(v1y-v2y) - (v1x-v2x)*(py-v2y);
        double d2 = (px-v3x)*(v2y-v3y) - (v2x-v3x)*(py-v3y);
        double d3 = (px-v1x)*(v3y-v1y) - (v3x-v1x)*(py-v1y);
        return !(((d1<0)||(d2<0)||(d3<0)) && ((d1>0)||(d2>0)||(d3>0)));
    }

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        double x_aff = pAffineTP.x;
        double y_aff = pAffineTP.y;
        double z_aff = pAffineTP.z;

        double base_h = this.shapeSize;
        double base_w = this.shapeSize * this.shapeAspectRatio;
        double half_h = (base_h == 0) ? 1e-9 : base_h / 2.0;
        double half_w = (base_w == 0) ? 1e-9 : base_w / 2.0;

        if (this.operationMode == 1) { // --- Cropping Mode ---
            boolean is_inside = false;
            double px_test = x_aff;
            double py_test = y_aff;

            switch (this.shapeType) {
                case 0: { double s_ext=this.shapeSize/2.0; is_inside=(abs(px_test)<=s_ext && abs(py_test)<=s_ext); break; }
                case 1: { is_inside=(abs(px_test)<=half_w && abs(py_test)<=half_h); break; }
                case 2: { 
                    double s=this.shapeSize, h_tri=s*SQRT3/2.0, v1x=0,v1y=(2.0/3.0)*h_tri,v2x=-s/2.0,v2y=-(1.0/3.0)*h_tri,v3x=s/2.0,v3y=-(1.0/3.0)*h_tri;
                    is_inside = isPointInTriangle(px_test,py_test,v1x,v1y,v2x,v2y,v3x,v3y); break;
                }
                case 3: { 
                    double s=this.shapeSize; is_inside=false;
                    for(int i=0;i<6;i++){double a1=i*PI/3.0,a2=(i+1)*PI/3.0; if(isPointInTriangle(px_test,py_test,0,0,s*cos(a1),s*sin(a1),s*cos(a2),s*sin(a2))){is_inside=true;break;}}
                    break;
                }
                case 4: { // Bump Deformed Rectangle
                    double py_norm=(half_h==0)?0:min(1,max(-1,py_test/half_h)); double px_norm=(half_w==0)?0:min(1,max(-1,px_test/half_w));
                    double v_lr_in=(this.lrRadialProfileType==0)?py_norm:(py_norm+1)/2.0; double v_tb_in=(this.tbProfileType==0)?px_norm:(px_norm+1)/2.0;
                    double lr_off=calculate_profile_offset(v_lr_in,this.lrRadialProfileType,this.lrRadialAmp,this.lrRadialWidthFreq,this.lrRadialPosPhaseDuty);
                    double tb_off=calculate_profile_offset(v_tb_in,this.tbProfileType,this.tbAmp,this.tbWidthFreq,this.tbPosPhaseDuty);
                    double xl=-half_w+lr_off, xr=half_w-lr_off, yb=-half_h+tb_off, yt=half_h-tb_off;
                    is_inside=(px_test>=xl && px_test<=xr && py_test>=yb && py_test<=yt); break;
                }
                case 5: { // Escher-Style Deformed Rectangle - Refined Crop
                    double py_n_lr=(half_h==0)?0:min(1,max(-1,py_test/half_h)); double px_n_tb=(half_w==0)?0:min(1,max(-1,px_test/half_w));
                    double v_in_lr_disp=(this.lrRadialProfileType==0)?py_n_lr:(py_n_lr+1)/2.0;
                    double v_in_tb_disp=(this.tbProfileType==0)?px_n_tb:(px_n_tb+1)/2.0;
                    double disp_x_at_py=calculate_profile_offset(v_in_lr_disp,this.lrRadialProfileType,this.lrRadialAmp,this.lrRadialWidthFreq,this.lrRadialPosPhaseDuty);
                    double disp_y_at_px=calculate_profile_offset(v_in_tb_disp,this.tbProfileType,this.tbAmp,this.tbWidthFreq,this.tbPosPhaseDuty);
                    double prxb=px_test-disp_x_at_py; double pryb=py_test-disp_y_at_px;
                    double eps=1e-7; is_inside=(abs(prxb)<=half_w+eps && abs(pryb)<=half_h+eps); break;
                }
                case 6: { // Asymmetric Profiled Rectangle
                    double pynre=(half_h==0)?0:min(1,max(-1,py_test/half_h)); double pxnte=(half_w==0)?0:min(1,max(-1,px_test/half_w));
                    double vire=(this.lrRadialProfileType==0)?pynre:(pynre+1)/2.0; double vite=(this.tbProfileType==0)?pxnte:(pxnte+1)/2.0;
                    double reo=calculate_profile_offset(vire,this.lrRadialProfileType,this.lrRadialAmp,this.lrRadialWidthFreq,this.lrRadialPosPhaseDuty);
                    double teo=calculate_profile_offset(vite,this.tbProfileType,this.tbAmp,this.tbWidthFreq,this.tbPosPhaseDuty);
                    double rb=half_w+reo, tb=half_h+teo;
                    is_inside=(px_test>=-half_w && px_test<=rb && py_test>=-half_h && py_test<=tb); break;
                }
                case 7: { // Profiled Circle / Star
                    double br=this.shapeSize, angle_aff=atan2(py_test,px_test), radius_aff=sqrt(px_test*px_test+py_test*py_test);
                    double ang_n01=(angle_aff/TWO_PI+1.0)%1.0; 
                    double vip=(this.lrRadialProfileType==0)?(ang_n01*2-1):ang_n01; 
                    double rad_off=calculate_profile_offset(vip,this.lrRadialProfileType,this.lrRadialAmp,this.lrRadialWidthFreq,this.lrRadialPosPhaseDuty);
                    double bound_r=max(0.0,br+rad_off);
                    is_inside=(radius_aff<=bound_r); break;
                }
                default: is_inside = false; break;
            }

            if (is_inside) {
            	  pVarTP.doHide = false;
                pVarTP.x = x_aff; pVarTP.y = y_aff;
            } else {
                pVarTP.doHide = true;
            }
            if (pContext.isPreserveZCoordinate()) {
              pVarTP.z += pAmount * pAffineTP.z;
            }
        } else { // --- Original Fill Mode (operationMode == 0) ---
            double targetShapeX = 0.0; double targetShapeY = 0.0;
            switch (this.shapeType) {
                case 0: { targetShapeX=(random()*2-1)*(this.shapeSize/2.0); targetShapeY=(random()*2-1)*(this.shapeSize/2.0); break; }
                case 1: { targetShapeX=(random()*2-1)*half_w; targetShapeY=(random()*2-1)*half_h; break; }
                case 2: { 
                    double s=this.shapeSize,h_tri=s*SQRT3/2.0,v1x=0,v1y=(2.0/3.0)*h_tri,v2x=-s/2.0,v2y=-(1.0/3.0)*h_tri,v3x=s/2.0,v3y=-(1.0/3.0)*h_tri;
                    double r1=random(),r2=random(); if(r1+r2>1.0){r1=1.0-r1;r2=1.0-r2;} double r3=1.0-r1-r2;
                    targetShapeX=r1*v1x+r2*v2x+r3*v3x; targetShapeY=r1*v1y+r2*v2y+r3*v3y; break;
                }
                case 3: { 
                    double s=this.shapeSize; int ti=(int)floor(random()*6.0); double ao=ti*PI/3.0;
                    double v1xh=s*cos(ao),v1yh=s*sin(ao),v2xh=s*cos(ao+PI/3.0),v2yh=s*sin(ao+PI/3.0);
                    double r1h=random(),r2h=random(); if(r1h+r2h>1.0){r1h=1.0-r1h;r2h=1.0-r2h;}
                    targetShapeX=(1.0-r1h-r2h)*0+r1h*v1xh+r2h*v2xh;targetShapeY=(1.0-r1h-r2h)*0+r1h*v1yh+r2h*v2yh; break;
                }
                case 4: { 
                    double max_lr_off=abs(this.lrRadialAmp),max_tb_off=abs(this.tbAmp);
                    double sx=half_w+max_lr_off,sy=half_h+max_tb_off; boolean pf=false;
                    for(int i=0;i<MAX_REJECTION_SAMPLES;i++){
                        double px=(random()*2-1)*sx,py=(random()*2-1)*sy;
                        double pyn=(half_h==0)?0:min(1,max(-1,py/half_h)),pxn=(half_w==0)?0:min(1,max(-1,px/half_w));
                        double vilr=(this.lrRadialProfileType==0)?pyn:(pyn+1)/2.0,vitb=(this.tbProfileType==0)?pxn:(pxn+1)/2.0;
                        double lro=calculate_profile_offset(vilr,this.lrRadialProfileType,this.lrRadialAmp,this.lrRadialWidthFreq,this.lrRadialPosPhaseDuty);
                        double tbo=calculate_profile_offset(vitb,this.tbProfileType,this.tbAmp,this.tbWidthFreq,this.tbPosPhaseDuty);
                        double xl=-half_w+lro,xr=half_w-lro,yb=-half_h+tbo,yt=half_h-tbo;
                        if(px>=xl && px<=xr && py>=yb && py<=yt){targetShapeX=px;targetShapeY=py;pf=true;break;}
                    } if(!pf){targetShapeX=0;targetShapeY=0;} break;
                }
                case 5: { 
                    double rxb=(random()*2-1)*half_w,ryb=(random()*2-1)*half_h;
                    double rynd=(half_h==0)?0:ryb/half_h,rxnd=(half_w==0)?0:rxb/half_w;
                    double vild=(this.lrRadialProfileType==0)?rynd:(rynd+1)/2.0,vitd=(this.tbProfileType==0)?rxnd:(rxnd+1)/2.0;
                    double dx=calculate_profile_offset(vild,this.lrRadialProfileType,this.lrRadialAmp,this.lrRadialWidthFreq,this.lrRadialPosPhaseDuty);
                    double dy=calculate_profile_offset(vitd,this.tbProfileType,this.tbAmp,this.tbWidthFreq,this.tbPosPhaseDuty);
                    targetShapeX=rxb+dx;targetShapeY=ryb+dy; break;
                }
                case 6: { 
                    double r_amp=this.lrRadialAmp,r_wf=this.lrRadialWidthFreq,r_ppd=this.lrRadialPosPhaseDuty; int r_pt=this.lrRadialProfileType;
                    double t_amp=this.tbAmp,t_wf=this.tbWidthFreq,t_ppd=this.tbPosPhaseDuty; int t_pt=this.tbProfileType;
                    double xmb=-half_w,xab=half_w+abs(r_amp),ymb=-half_h,yab=half_h+abs(t_amp); boolean pf6=false;
                    for(int i=0;i<MAX_REJECTION_SAMPLES;i++){
                        double px=xmb+random()*(xab-xmb),py=ymb+random()*(yab-ymb);
                        if(px<-half_w||py<-half_h)continue;
                        double pynre=(half_h==0)?0:min(1,max(-1,py/half_h)),pxnte=(half_w==0)?0:min(1,max(-1,px/half_w));
                        double vire=(r_pt==0)?pynre:(pynre+1)/2.0,vite=(t_pt==0)?pxnte:(pxnte+1)/2.0;
                        double reo=calculate_profile_offset(vire,r_pt,r_amp,r_wf,r_ppd); if(px>(half_w+reo))continue;
                        double teo=calculate_profile_offset(vite,t_pt,t_amp,t_wf,t_ppd); if(py>(half_h+teo))continue;
                        targetShapeX=px;targetShapeY=py;pf6=true;break;
                    } if(!pf6){targetShapeX=0;targetShapeY=0;} break;
                }
                case 7: { // Profiled Circle / Star
                    double base_radius = this.shapeSize;
                    
                    // Explicitly cast to int when fetching the profile type for clarity and safety
                    int current_radial_profile_type = (int)this.lrRadialProfileType; 
                    
                    double current_radial_amp = this.lrRadialAmp;
                    double current_radial_param_wf = this.lrRadialWidthFreq;
                    double current_radial_param_ppd = this.lrRadialPosPhaseDuty;

                    double random_angle_rad = random() * TWO_PI;
                    double angle_norm_01 = random_angle_rad / TWO_PI; 
                    
                    double v_input_for_profile;
                    if (current_radial_profile_type == 0) { // Cosine Bump expects input [-1,1]
                        v_input_for_profile = (angle_norm_01 * 2.0) - 1.0;
                    } else { // Periodic waves expect input [0,1] (already mapped from v_on_edge_normalized in calculate_profile_offset)
                        v_input_for_profile = angle_norm_01;
                    }
                    
                    double radial_offset = calculate_profile_offset(
                                                v_input_for_profile,        // double
                                                current_radial_profile_type,// int (now explicitly cast from an int member)
                                                current_radial_amp,         // double
                                                current_radial_param_wf,    // double
                                                current_radial_param_ppd    // double
                                            );
                    
                    double boundary_r = max(0.0, base_radius + radial_offset);
                    double r_fill = sqrt(random()) * boundary_r;

                    targetShapeX = r_fill * cos(random_angle_rad);
                    targetShapeY = r_fill * sin(random_angle_rad);
                    break;
                }
                default: targetShapeX = 0; targetShapeY = 0; break;
            }

            double effectiveAmount = pAmount * this.strength;
            double clampedEffectiveAmount = max(0.0, min(1.0, effectiveAmount));
            pVarTP.x = (1.0 - clampedEffectiveAmount) * x_aff + clampedEffectiveAmount * targetShapeX;
            pVarTP.y = (1.0 - clampedEffectiveAmount) * y_aff + clampedEffectiveAmount * targetShapeY;

            if (pContext.isPreserveZCoordinate()) {
              pVarTP.z += pAmount * pAffineTP.z;
            }
        }
    }

    @Override
    public String[] getParameterNames() { return paramNames; }

    @Override
    public Object[] getParameterValues() {
        return new Object[]{
                this.strength, this.shapeSize, this.shapeType, this.shapeAspectRatio,
                this.lrRadialAmp, this.lrRadialWidthFreq, this.tbAmp, this.tbWidthFreq,
                this.lrRadialPosPhaseDuty, this.tbPosPhaseDuty, this.lrRadialProfileType, this.tbProfileType,
                this.operationMode
        };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_STRENGTH.equalsIgnoreCase(pName)) this.strength = pValue;
        else if (PARAM_SHAPE_SIZE.equalsIgnoreCase(pName)) this.shapeSize = pValue;
        else if (PARAM_SHAPE_TYPE.equalsIgnoreCase(pName)) this.shapeType = limitIntVal(Tools.FTOI(pValue), 0, 7);
        else if (PARAM_SHAPE_ASPECT_RATIO.equalsIgnoreCase(pName)) this.shapeAspectRatio = pValue;
        else if (PARAM_LR_RADIAL_AMP.equalsIgnoreCase(pName)) this.lrRadialAmp = pValue;
        else if (PARAM_LR_RADIAL_WIDTH_FREQ.equalsIgnoreCase(pName)) this.lrRadialWidthFreq = pValue;
        else if (PARAM_TB_AMP.equalsIgnoreCase(pName)) this.tbAmp = pValue;
        else if (PARAM_TB_WIDTH_FREQ.equalsIgnoreCase(pName)) this.tbWidthFreq = pValue;
        else if (PARAM_LR_RADIAL_POS_PHASE_DUTY.equalsIgnoreCase(pName)) this.lrRadialPosPhaseDuty = pValue;
        else if (PARAM_TB_POS_PHASE_DUTY.equalsIgnoreCase(pName)) this.tbPosPhaseDuty = pValue;
        else if (PARAM_LR_RADIAL_PROFILE_TYPE.equalsIgnoreCase(pName)) this.lrRadialProfileType = limitIntVal(Tools.FTOI(pValue), 0, 4);
        else if (PARAM_TB_PROFILE_TYPE.equalsIgnoreCase(pName)) this.tbProfileType = limitIntVal(Tools.FTOI(pValue), 0, 4);
        else if (PARAM_OPERATION_MODE.equalsIgnoreCase(pName)) this.operationMode = limitIntVal(Tools.FTOI(pValue), 0, 1);
    }

    @Override
    public String getName() { return "tess_shape"; }
    
    @Override
    public void randomize() {
    	if (Math.random() < 0.25) strength = Math.random() + 0.01;
    	else strength = Math.random() * 0.4 + 0.6;
    	shapeSize = Math.random() * 3.75 + 0.25;
    	shapeType = (int) (Math.random() * 8);
    	shapeAspectRatio = Math.random()* 4.0 + 0.1;
    	lrRadialAmp = Math.random() * TWO_PI - PI;
    	lrRadialWidthFreq = Math.random() * PI;
    	tbAmp = Math.random() * TWO_PI - PI;
    	tbWidthFreq = Math.random() * PI;
    	lrRadialPosPhaseDuty = Math.random() * 2.0 - 1.0;
    	tbPosPhaseDuty = Math.random() * 2.0 - 1.0;
    	lrRadialProfileType = (int) (Math.random() * 5);
    	tbProfileType = (int) (Math.random() * 5);
    	// don't change operationMode
    }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE};
    }
}