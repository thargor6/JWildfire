package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_HyperbolicFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_hyperbolictile
	 * Date: February 13, 2019
	 * Author:Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;
	private static final String PARAM_ZOOM = "zoom";
	double zoom=1.0;

	private static final String[] additionalParamNames = { PARAM_ZOOM};

	vec2 tocart(vec2 polar) {
		   return new vec2(polar.x * G.cos(polar.y), polar.x * G.sin(polar.y));
		}

		public vec2 topolar(vec2 cart) {
		   double r = G.sqrt(cart.x*cart.x + cart.y*cart.y);
		   double alpha = G.atan2(cart.y, cart.x);
		   return new vec2(r, alpha);
		}

		public vec2 mirror(vec2 line, vec2 point) {
		  double r1 = line.x;


		  if (r1 < 100.0) {
		    vec2 linecenter = tocart(line);
		    vec2 cart = tocart(point);
		    double dx = cart.x - linecenter.x;
		    double dy = cart.y - linecenter.y;
		    r1 = line.x*line.x - 1.0;
		    double r2 = dx*dx+dy*dy;
		    double rr = r1 / r2;
		    double dx2 = dx * rr;
		    double dy2 = dy * rr;
		    return	topolar(linecenter.plus(new vec2(dx2,dy2)));
		  } 
		  else 
		  {
		    return new vec2(point.x, -point.y + 2.0 * line.y + Math.PI);
		  }
		}

		
	public vec3 getRGBColor(double xp,double yp)
	{

		// Half the width and half the height gives the position of the center of the screen
		
		vec2 position = new vec2(xp,yp).multiply(zoom);

		  vec2 l1 = new vec2(1000.0, 0.0);
		  vec2 l2 = new vec2(1000.0, Math.PI / 8.0);
		  vec2 l3 = new vec2(Math.sqrt(Math.sqrt(2.0) + 1.0), Math.PI / 2.0);



		  vec2 p = topolar(position);
		  double color;
		  if (p.x > 1.0) {
		     color = 0.0;
		  }
		  else 
		  {
		    int g2 = 0;
		    int k1 = 1;
		    for (int i = 0; i < 7; ++i) {
		      if (k1 == 0) {
		        continue;
		      }
		      int k = 1;
		      for (int j = 1; j < 8; ++j) {
		        if (k == 0 || (p.y >= (Math.PI / 2.0)) && (p.y < (Math.PI * 0.75)))
		        {
		          k = 0;
		        } else {
		          g2 = g2 + 1;
		          p = mirror(l2, mirror(l1, p));
		        }
		      }
		      vec2 p1 = mirror(l3, p);
		      if ((p1.y >= (Math.PI / 2.0)) && (p1.y < (Math.PI * 0.75))) {
		        k1 = 0;
		      }
		      p = p1;
		    }
		    color = G.mod((double)g2, 2.0) / 1.0;
//		    color = (double)(g2)/70.0;
		  }
		  return new vec3( color, color, color);
		}

	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{

        vec3 color=new vec3(0.0); 
		 vec2 uV=new vec2(0.),p=new vec2(0.);
	       int[] tcolor=new int[3];  


		 
	     if(colorOnly==1)
		 {
			 uV.x=pAffineTP.x;
			 uV.y=pAffineTP.y;
		 }
		 else
		 {
	   			 uV.x=2.0*pContext.random()-1.0;
				 uV.y=2.0*pContext.random()-1.0;
		}
        
        color=getRGBColor(uV.x,uV.y);
        tcolor=dbl2int(color);
        
        //z by color (normalized)
        double z=greyscale(tcolor[0],tcolor[1],tcolor[2]);
        
        if(gradient==0)
        {
  	  	
    	  pVarTP.rgbColor  =true;;
    	  pVarTP.redColor  =tcolor[0];
    	  pVarTP.greenColor=tcolor[1];
    	  pVarTP.blueColor =tcolor[2];
    		
        }
        else if(gradient==1)
        {

            	Layer layer=pXForm.getOwner();
            	RGBPalette palette=layer.getPalette();      	  
          	    RGBColor col=findKey(palette,tcolor[0],tcolor[1],tcolor[2]);
          	    
          	  pVarTP.rgbColor  =true;;
          	  pVarTP.redColor  =col.getRed();
          	  pVarTP.greenColor=col.getGreen();
          	  pVarTP.blueColor =col.getBlue();

        }
        else 
        {
        	pVarTP.color=z;
        }

        pVarTP.x+= pAmount*(uV.x);
        pVarTP.y+= pAmount*(uV.y);
        
        
	    double dz = z * scale_z + offset_z;
	    if (reset_z == 1) {
	      pVarTP.z = dz;
	    }
	    else {
	      pVarTP.z += dz;
	    }
	}
	

	public String getName() {
		return "dc_hyperbolictile";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] {zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.01 , 50.0);
		}
		else
			super.setParameter(pName, pValue);
	}

	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will make them refresh
		return true;
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( varpar->dc_hyperbolictile_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*varpar->dc_hyperbolictile_zoom;"
	    		+"color=dc_hyperbolictile_getRGBColor(uv);"
	    		+"if( varpar->dc_hyperbolictile_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_hyperbolictile_Gradient ==1 )"  
	    		+"{"
	    		+"float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
	    		+"float4 simcol=pal_color;"
	    		+"float diff=1000000000.0f;"

	    		+" for(int index=0; index<numColors;index++)"
                +" {      pal_color = read_imageStepMode(palette, numColors, (float)index/(float)numColors);"
	    		+"        float3 pal_color3=make_float3(pal_color.x,pal_color.y,pal_color.z);"
                
	        	+"    float dvalue= distance_color(color.x,color.y,color.z,pal_color.x,pal_color.y,pal_color.z);"
	        	+ "   if (diff >dvalue) "
	        	+ "    {" 
	        	+"	     diff = dvalue;" 
	        	+"       simcol=pal_color;" 
	        	+"	   }"
                +" }"

	    		+"   __useRgb  = true;"
	    		+"   __colorR  = simcol.x;"
	    		+"   __colorG  = simcol.y;"
	    		+"   __colorB  = simcol.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_hyperbolictile_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_hyperbolictile*x;"
	    		+"__py+= varpar->dc_hyperbolictile*y;"
	    		+"float dz = z * varpar->dc_hyperbolictile_scale_z + varpar->dc_hyperbolictile_offset_z;"
	    		+"if ( varpar->dc_hyperbolictile_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "__device__	float2  dc_hyperbolictile_tocart (float2 polar) {"
				 +"		   return make_float2(polar.x * cosf(polar.y), polar.x * sinf(polar.y));"
				 +"}"
				 
				 +"__device__ float2  dc_hyperbolictile_topolar (float2 cart) {"
				 +"   float r = sqrtf(cart.x*cart.x + cart.y*cart.y);"
				 +"   float alpha = atan2(cart.y, cart.x);"
				 +"   return make_float2(r, alpha);"
				 +"}"
				 
				 +"__device__ float2  dc_hyperbolictile_mirror (float2 line, float2 point) {"
				 +"  float r1 = line.x;"
				 +"  if (r1 < 100.0) {"
				 +"	    float2 linecenter =  dc_hyperbolictile_tocart (line);"
				 +"	    float2 cart =  dc_hyperbolictile_tocart (point);"
				 +"	    float dx = cart.x - linecenter.x;"
				 +"	    float dy = cart.y - linecenter.y;"
				 +"	    r1 = line.x*line.x - 1.0;"
				 +"	    float r2 = dx*dx+dy*dy;"
				 +"	    float rr = r1 / r2;"
				 +"	    float dx2 = dx * rr;"
				 +"	    float dy2 = dy * rr;"
				 +"	    return	 dc_hyperbolictile_topolar (linecenter+(make_float2(dx2,dy2)));"
				 +"  } "
				 +"  else "
				 +"  {"
				 +"    return make_float2(point.x, -point.y + 2.0 * line.y + PI);"
				 +"  }"
				 +"}"
				 
				 +"	__device__ float3  dc_hyperbolictile_getRGBColor (float2 position)"
				 +"	{"
				 +"		  float2 l1 = make_float2(1000.0, 0.0);"
				 +"		  float2 l2 = make_float2(1000.0, PI / 8.0);"
				 +"		  float2 l3 = make_float2(sqrtf(sqrtf(2.0) + 1.0), PI / 2.0);"
				 +"		  float2 p =  dc_hyperbolictile_topolar (position);"
				 +"		  float color;"
				 +"		  if (p.x > 1.0) {"
				 +"		     color = 0.0;"
				 +"		  }"
				 +"		  else "
				 +"		  {"
				 +"		    int g2 = 0;"
				 +"		    int k1 = 1;"
				 +"		    for (int i = 0; i < 7; ++i) {"
				 +"		      if (k1 == 0) {"
				 +"		        continue;"
				 +"		      }"
				 +"		      int k = 1;"
				 +"		      for (int j = 1; j < 8; ++j) {"
				 +"		        if (k == 0 || (p.y >= (PI / 2.0)) && (p.y < (PI * 0.75)))"
				 +"		        {"
				 +"		          k = 0;"
				 +"		        } else {"
				 +"		          g2 = g2 + 1;"
				 +"		          p =  dc_hyperbolictile_mirror (l2,  dc_hyperbolictile_mirror (l1, p));"
				 +"		        }"
				 +"		      }"
				 +"		      float2 p1 =  dc_hyperbolictile_mirror (l3, p);"
				 +"		      if ((p1.y >= (PI / 2.0)) && (p1.y < (PI * 0.75))) {"
				 +"		        k1 = 0;"
				 +"		      }"
				 +"		      p = p1;"
				 +"		    }"
				 +"		    color = mod((float)g2, 2.0) / 1.0;"
				 +""
				 +"		  }"
				 +"		  return make_float3( color, color, color);"
				 +"		}";
	 }
}

