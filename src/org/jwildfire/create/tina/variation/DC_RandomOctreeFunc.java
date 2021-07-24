package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import static org.jwildfire.base.mathlib.MathLib.fmod;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;




public class DC_RandomOctreeFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_randomoctree
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_STEPS = "steps";
	private static final String PARAM_RLR = "rotLR";
	private static final String PARAM_RUD = "rotUD";
	private static final String PARAM_DRAWGRID = "grid";
	private static final String PARAM_BORDERS = "borders";
	private static final String PARAM_BLACKBORDERS = "bBorders";


	private int seed = 10000;
	double time=0.0;
	int steps=10;
	double mouseX=0.0;  // 0-1
	double mouseY=0.0;  // 0-1
	double rlr = 0.0;
	double rud = 0.0;
	int grid=0;
	int borders=1;
	int blackborders=1;

	
	  vec3 HASHSCALE3 = new vec3(.1031, .1030, .0973);
	  double emptycells = 0.5;
	  double subdivisions= 0.95; //should be higher than emptycells
	  int detail=5;
	  
		Random randomize=new Random(seed);
		
	 	long last_time=System.currentTimeMillis();
	 	long elapsed_time=0;
	

	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_STEPS,PARAM_RLR,PARAM_RUD,PARAM_DRAWGRID,PARAM_BORDERS,PARAM_BLACKBORDERS};


	

	  public double rnd(vec4 v)
	  {
		  return G.fract(4e4*G.sin(G.dot(v,new vec4(13.46,41.74,-73.36,14.24))+17.34)); 
	}


	  
	//hash function by Dave_Hoskins https://www.shadertoy.com/view/4djSRW
	public vec3 hash33(vec3 p3)
	{
		p3 = G.fract(p3.multiply( HASHSCALE3));
		vec3 t0=new vec3(p3.y,p3.x,p3.z);
	    p3 = p3.add(G.dot(p3, t0.add(19.19)));
	    t0=new vec3(p3.x,p3.x,p3.y).add(new vec3(p3.y,p3.x,p3.x)).multiply(new vec3(p3.z,p3.y,p3.x));
	    return G.fract(t0);
	}
	
	//0 is empty, 1 is subdivide and 2 is full
	public int getvoxel(vec3 p, double size) {
//	#ifdef objects
	    if (p.x==0.0&&p.y==0.0) {
	        return 0;
	    }
//	#endif
	    
	    double val = rnd(new vec4(p,size));
	    
	    if (val < emptycells) {
	        return 0;
	    } else if (val < subdivisions) {
	        return 1;
	    } else {
	        return 2;
	    }
	 //    return (int) (val*val*3.0); //unreachable statement	    
	}

	//ray-cube intersection, on the inside of the cube
	public vec3 voxel(vec3 ro, vec3 rd, vec3 ird, double size)
	{
	    size *= 0.5;
	    
	    vec3 hit = (G.sign(rd).multiply((ro.minus(size) )).minus(size)).multiply(ird).multiply(-1.0);
	    
	    return hit;
	}
	
	public double  map(vec3 p, vec3 fp) {
	    p = p.minus(0.5);
	    
	    vec3 flipping = G.floor(hash33(fp).add(0.5)).multiply(2.0).minus(1.0);
	    
	    p = p.multiply(flipping);
	    
	    double f1=G.abs(G.length(new vec2(p.x,p.y).minus(0.5))-0.5);
	    vec2 q = new vec2(f1,p.z);
	    double len = G.length(q);
	    
	    f1=G.abs(G.length(new vec2(p.y,p.z).minus(new vec2(-0.5,0.5)))-0.5);
	    q = new vec2(f1,p.x);
	    len = G.min(len,G.length(q));
	    f1=G.abs(G.length(new vec2(p.x,p.z).plus(0.5))-0.5);
	    q = new vec2(f1,p.y);
	    len = G.min(len,G.length(q));
	    return len-0.1666;
	}

	public vec3 findnormal(vec3 p, double epsilon, vec3 fp)
	{
	    vec2 eps = new vec2(0.0,epsilon);
	    
	    vec3 vp1= p.add  (new vec3(eps.y, eps.x,eps.x));
	    vec3 vm1= p.minus(new vec3(eps.y,eps.x,eps.x));
	    vec3 vp2= p.add  (new vec3(eps.x,eps.y,eps.x));
	    vec3 vm2= p.minus(new vec3(eps.x,eps.y,eps.x));
	    vec3 vp3= p.add  (new vec3(eps.x,eps.x,eps.y));
	    vec3 vm3= p.minus(new vec3(eps.x,eps.x,eps.y));
	    double v1=map(vp1,fp)-map(vm1,fp);
	    double v2=map(vp2,fp)-map(vm2,fp);
	    double v3=map(vp3,fp)-map(vm3,fp);
	    vec3 normal = new vec3( v1,v2,v3);

	    return G.normalize(normal);
	}
	
	
	public mat2 rot(double spin)
	{
		return new mat2(G.cos(spin),G.sin(spin),-G.sin(spin),G.cos(spin));
	}
	
	public double sqr(double a)
	{
		return a*a;
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{


		vec2 uv = new vec2(xp,yp);


		double size = 1.0;

		double maxdistance=6.0;

		vec3 ro = new vec3(0.5+G.sin(time)*0.4,0.5+G.cos(time)*0.4,time);
		vec3 rd = G.normalize(new vec3(uv,1.0));
		vec2 rdyz= new vec2(rd.y,rd.z);
		vec2 rdxz= new vec2(rd.x,rd.z);

		//if the mouse is in the bottom left corner, don't rotate the camera
		if (G.length(new vec2(mouseX*1000.0,mouseY*1000.0)) > 40.0) 
		{
			vec2 t1=new vec2(rd.y,rd.z);

			t1 = t1.times(rot(mouseY*Math.PI-Math.PI/2.0));
			rd=new vec3(rd.x,t1.x,t1.y);
			vec2 t2=new vec2(rd.x,rd.z);
			t2 = t2.times(rot(2.0*mouseX*Math.PI-Math.PI));
			rd=new vec3(t2.x,rd.y,t2.y);
		}



		vec3 lro = G.mod(ro,size);
		vec3 fro = ro.minus(lro);
		vec3 ird = new vec3(1.0).division(G.max(G.abs(rd),new vec3(0.001)));
		vec3 mask=new vec3(0.0);
		boolean exitoct = false;
		int recursions = 0;
		double dist = 0.0;
		int i=0;
		double edge = 1.0;
		vec3 lastmask=new vec3(0.0);
		vec3 normal = new vec3(0.0);

		//the octree traverser loop
		//each iteration i either:
		// - check if i need to go up a level
		// - check if i need to go down a level
		// - check if i hit a cube
		// - go one step forward if cube is empty
		for (int k = 0; k < steps; k++)
		{
			if (dist > maxdistance) break;
			int voxelstate = getvoxel(fro,size);

			//i go up a level
			if (exitoct)
			{
				vec3 v1=fro.division(size/0.5).add(0.25);
				vec3 newfro = G.floor(v1).multiply(size*2.0);

				lro = lro.add(fro.minus(newfro));
				fro = newfro;

				recursions--;
				size *= 2.0;
				vec3 v0=G.mod(fro.division(size).add(0.5),2.0);
				vec3 v3=mask.multiply(G.sign(rd)).multiply(0.5);
				v1=v0.minus(new vec3(1.0)).add(v3);
				double f1=G.abs(G.dot(v1,mask));
				exitoct = (recursions > 0) && (f1<0.1);
			}
			//subdivide
			else if(voxelstate == 1&&recursions<=detail)
			{
				//if(recursions>detail) break;

				recursions++;
				size *= 0.5;

				//find which of the 8 voxels i will enter
				vec3 mask2 = G.step(new vec3(size),lro);
				fro = fro.add(mask2.multiply(size));
				lro = lro.minus(mask2.multiply(size));
			}
			//move forward
			else if (voxelstate == 0||voxelstate == 2||recursions > detail)
			{
				//raycast and find distance to nearest voxel surface in ray direction
				//i don't need to use voxel() every time, but i do anyway
				vec3 hit = voxel(lro, rd, ird, size);

				if (hit.x < G.min(hit.y,hit.z)) {
					mask = new vec3(1,0,0);
				} else if (hit.y < hit.z) {
					mask = new vec3(0,1,0);
				} else {
					mask = new vec3(0,0,1);
				}
				//mask = vec3(lessThan(hit,min(hit.yzx,hit.zxy)));
				double len = G.dot(hit,mask);
				//	#ifdef objects
				if (voxelstate == 2) {
					/*	#ifdef truchet
	                //if (length(fro-ro) > 20.0*size) break;
	                vec3 p = lro/size;
	                if (map(p,fro) < 0.0) {
	                    normal = -lastmask*sign(rd);
	                    break;
	                }
	                float d = 0.0;
	                bool hit = false;
	                float e = 0.001/size;
	                for (int j = 0; j < 100; j++) {
						float l = map(p,fro);
	                    p += l*rd;
	                    d += l;
	                    if (l < e || d > len/size) {
	                        if (l < e) hit = true;
	                        d = min(len,d);
	                        break;
	                    }
	                }
	                if (hit) {
	                    dist += d*size;
	                    ro += rd*d*size;
	                    normal = findnormal(p,e,fro);//(lro-0.5)*2.0;
	                    break;
	                }
	#else*/
					break;
					//#endif
				}
				//	#endif

				//moving forward in ray direction, and checking if i need to go up a level
				dist += len;
				vec3 v5=rd.multiply(len).minus(mask.multiply(G.sign(rd)).multiply(size));
				lro = lro.add(v5);
				vec3 newfro = fro.add(mask.multiply(G.sign(rd)).multiply(size));
				vec3 v4=newfro.division(size/0.5).add(0.25);
				exitoct = (G.floor(v4)!=G.floor(v4))&&(recursions>0);
				fro = newfro;
				lastmask = mask;
			}
			if(grid==1)
			{
				vec3 q = G.abs(lro.division(size).minus(0.5)).multiply(new vec3(1.0).minus(lastmask));
				edge = G.min(edge,-(G.max(G.max(q.x,q.y),q.z)-0.5)*80.0*size);
			}
		}
		ro = ro.add(rd.multiply(dist));
		vec3 color=new vec3(0.0);
		if(i < steps && dist < maxdistance)
		{
			double val = G.fract(G.dot(fro,new vec3(15.23,754.345,3.454)));
			//	#ifndef truchet
			normal = lastmask.multiply(G.sign(rd)).multiply(-1.0);
			//	#endif
			color = G.sin(new vec3(39.896,57.3225,48.25).multiply(val)).multiply(0.5).add(0.5);
			color= color.multiply((normal.multiply(0.25).add(0.75) ));

			if(borders==1)
			{
				vec3 q = G.abs(lro.division(size).minus(0.5)).multiply(new vec3(1.0).minus(lastmask));
				edge = G.clamp(-(G.max(G.max(q.x,q.y),q.z)-0.5)*20.0*size,0.0,edge);
			}

			if(blackborders==1)
				color = color.multiply(edge);
			else

				color = new vec3(1.0).minus((new vec3(1.0).minus(color)).multiply(edge));

		} else {
			if (blackborders==1)
				color = new vec3(edge);
			else
				color = new vec3(1.0-edge);
		}
		//	#ifdef fog
		//	    gl_FragColor *= 1.0-dist/maxdistance;
		//	#endif
		color = G.sqrt(color);
		return color;	
	}


	public String getName() {
		return "dc_randomoctree";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,steps,rlr,rud,grid,borders,blackborders},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		 if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int) pValue;
	       randomize=new Random(seed);
	          long current_time = System.currentTimeMillis();
	          elapsed_time += (current_time - last_time);
	          last_time = current_time;
	          time = (double) (elapsed_time / 1000.0);
	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_STEPS)) {
			steps =(int)Tools.limitValue(pValue, 3 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_RLR)) {
		    rlr = pValue;
			mouseX = fmod(rlr, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_RUD)) {
            rud = pValue;
            mouseY = fmod(rud, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_DRAWGRID)) {
			grid=(int) Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_BORDERS)) {
			borders=(int) Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_BLACKBORDERS)) {
			blackborders=(int) Tools.limitValue(pValue, 0 , 1);
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
	    		+"if( __dc_randomoctree_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y);"
	    		+"color=dc_randomoctree_getRGBColor(uv,__dc_randomoctree_time,__dc_randomoctree_steps,"
	    		+"              __dc_randomoctree_rotLR,__dc_randomoctree_rotUD,__dc_randomoctree_grid,"
	    		+"              __dc_randomoctree_borders,__dc_randomoctree_bBorders);"
	    		+"if( __dc_randomoctree_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_randomoctree_Gradient ==1 )"
	    		+"{"
	    		+"float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
	    		+"float4 simcol=pal_color;"
	    		+"float diff=1000000000.0f;"
////read palette colors to find the nearest color to pixel color
	    		+" for(int index=0; index<numColors;index++)"
          +" {      pal_color = read_imageStepMode(palette, numColors, (float)index/(float)numColors);"
	    		+"        float3 pal_color3=make_float3(pal_color.x,pal_color.y,pal_color.z);"
          // implement:  float distance(float,float,float,float,float,float) in GPU function
	        	+"    float dvalue= distance_color(color.x,color.y,color.z,pal_color.x,pal_color.y,pal_color.z);"
	        	+ "   if (diff >dvalue) "
	        	+ "    {" 
	        	+"	     diff = dvalue;" 
	        	+"       simcol=pal_color;" 
	        	+"	   }"
          +" }"
////use nearest palette color as the pixel color                
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = simcol.x;"
	    		+"   __colorG  = simcol.y;"
	    		+"   __colorB  = simcol.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_randomoctree_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_randomoctree*x;"
	    		+"__py+= __dc_randomoctree*y;"
	    		+"float dz = z * __dc_randomoctree_scale_z + __dc_randomoctree_offset_z;"
	    		+"if ( __dc_randomoctree_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		return   " __device__ float  dc_randomoctree_rnd (float4 v)"
				+"	{"
				+ "     float t0=dot(v,make_float4(13.46,41.74,-73.36,14.24));"
				+"		return fract(4.0e4*sinf(t0+17.34)); "
				+"	}"
				
				+"	__device__ Mat2  dc_randomoctree_rot (float spin)"
				+"	{   Mat2 M;"
				+"      Mat2_Init(&M,cosf(spin),sinf(spin),-sinf(spin),cosf(spin));"
				+"		return M;"
				+"	}"

//				+"	__device__ float  dc_randomoctree_sqr (float a)"
//				+"	{"
//				+"		return a*a;"
//				+"	}"
		
				+"	__device__ float3  dc_randomoctree_hash33 (float3 p3)"
				+"	{"
				+"      float3 HASHSCALE = make_float3(0.1031,0.1031,0.0973);"
				+"		p3 = fract(p3*( HASHSCALE));"
				+"		float3 t0=make_float3(p3.y,p3.x,p3.z);"
				+"	    p3 = p3 + (dot(p3, t0 + (19.19)));"
				+"	    t0=make_float3(p3.x,p3.x,p3.y) + (make_float3(p3.y,p3.x,p3.x))*(make_float3(p3.z,p3.y,p3.x));"
				+"	    return fract(t0);"
				+"	}"
				
				+"	__device__ int getvoxel(float3 p, float size) {"
				+"      float emptycells=0.5;"
				+ "     float subdivisions=0.95;"
				+"	    if (p.x==0.0 && p.y==0.0) {"
				+"	        return 0;"
				+"	    }"
				+"	    float val =  dc_randomoctree_rnd (make_float4(p.x,p.y,p.z,size));"
				+"	    "
				+"	    if (val < emptycells) {"
				+"	        return 0;"
				+"	    } else if (val < subdivisions) {"
				+"	        return 1;"
				+"	    } else {"
				+"	        return 2;"
				+"	    }"
				+"	}"

				+"	__device__ float3  dc_randomoctree_voxel (float3 ro, float3 rd, float3 ird, float size)"
				+"	{"
				+"	    size *= 0.5;"
				+"	    float3 hit = (sign(rd)*((ro-(size) ))-(size))*(ird)*(-1.0);"
				+"	    return hit;"
				+"	}"

				+"	__device__ float   dc_randomoctree_map (float3 p, float3 fp) {"
				+"	    p = p-(0.5);"
				+"	    float3 flipping = floorf( dc_randomoctree_hash33 (fp)+ 0.5)*2.0-1.0;"
				+"	    p = p*(flipping);"
				+"	    float f1=fabsf(length(make_float2(p.x,p.y)-(0.5))-0.5);"
				+"	    float2 q = make_float2(f1,p.z);"
				+"	    float len = length(q);"
				+"	    f1=fabsf(length(make_float2(p.y,p.z)-(make_float2(-0.5,0.5)))-0.5);"
				+"	    q = make_float2(f1,p.x);"
				+"	    len = fminf(len,length(q));"
				+"	    f1=fabsf(length(make_float2(p.x,p.z)+(0.5))-0.5);"
				+"	    q = make_float2(f1,p.y);"
				+"	    len = fminf(len,length(q));"
				+"	    return len - 0.1666f;"
				+"	}"
				
				+"	__device__ float3  dc_randomoctree_findnormal (float3 p, float epsilon, float3 fp)"
				+"	{"
				+"	    float2 eps = make_float2(0.0,epsilon);"
				+"	    float3 vp1= p + (make_float3(eps.y, eps.x,eps.x));"
				+"	    float3 vm1= p-(make_float3(eps.y,eps.x,eps.x));"
				+"	    float3 vp2= p +  (make_float3(eps.x,eps.y,eps.x));"
				+"	    float3 vm2= p-(make_float3(eps.x,eps.y,eps.x));"
				+"	    float3 vp3= p +  (make_float3(eps.x,eps.x,eps.y));"
				+"	    float3 vm3= p-(make_float3(eps.x,eps.x,eps.y));"
				+"	    float v1= dc_randomoctree_map (vp1,fp)- dc_randomoctree_map (vm1,fp);"
				+"	    float v2= dc_randomoctree_map (vp2,fp)- dc_randomoctree_map (vm2,fp);"
				+"	    float v3= dc_randomoctree_map (vp3,fp)- dc_randomoctree_map (vm3,fp);"
				+"	    float3 normal = make_float3( v1,v2,v3);"
				+"	    return normalize(normal);"
				+"	}"

				+"	__device__ float3  dc_randomoctree_getRGBColor (float2 uv, float time, float steps, float rotLR,float  rotUD, float grid, float borders, float blackborders)"
				+"	{"
				+"		float size = 1.0;"
				+"		float maxdistance=6.0;"
				+"       float mouseX=mod(rotLR,1.0);"
				+"       float mouseY=mod(rotUD,1.0);"
				+"		float3 ro = make_float3(0.5+sinf(time)*0.4,0.5+cosf(time)*0.4,time);"
				+"		float3 rd = normalize(make_float3(uv.x,uv.y,1.0));"
				+"		float2 rdyz= make_float2(rd.y,rd.z);"
				+"		float2 rdxz= make_float2(rd.x,rd.z);"
				+"		"
				+"		if (length(make_float2(mouseX*1000.0,mouseY*1000.0)) > 40.0) "
				+"		{"
				+"			float2 t1=make_float2(rd.y,rd.z);"
				+"          Mat2 rot=dc_randomoctree_rot (-(mouseY*PI-PI/2.0));"
				+"			t1 = times( &rot,t1);"
				+"			rd=make_float3(rd.x,t1.x,t1.y);"
				+"			float2 t2=make_float2(rd.x,rd.z);"
				+"          Mat2 rot1=dc_randomoctree_rot (-(2.0*mouseX*PI-PI));"
				+"			t2 = times(&rot1,t2);"
				+"			rd=make_float3(t2.x,rd.y,t2.y);"
				+"		}"
				+"		float3 lro = mod(ro,size);"
				+"		float3 fro = ro-(lro);"
				+"		float3 ird = make_float3(1.0,1.0,1.0)/(max(abs(rd),make_float3(0.001,0.001,0.001)));"
				+"		float3 mask=make_float3(0.0,0.0,0.0);"
				+"		bool exitoct =false;"
				+"		int recursions = 0;"
				+"		float dist = 0.0;"
				+"		int i=0;"
				+"		float edge = 1.0;"
				+"		float3 lastmask=make_float3(0.0,0.0,0.0);"
				+"		float3 normal = make_float3(0.0,0.0,0.0);"
				+"		for (int k = 0; k < steps; k++)"
				+"		{"
				+"			if (dist > maxdistance) break;"
				+"			int voxelstate = getvoxel(fro,size);"
				+"			if (exitoct)"
				+"			{"
				+"				float3 v1=fro/(size/0.5)+ 0.25;"
				+"				float3 newfro = floorf(v1)*(size*2.0);"
				+"				lro = lro + (fro-newfro);"
				+"				fro = newfro;"
				+"				recursions--;"
				+"				size *= 2.0;"
				+"				float3 v0=mod(fro/(size)+ 0.5,2.0);"
				+"				float3 v3=mask*(sign(rd))*(0.5);"
				+"				v1=v0-(make_float3(1.0,1.0,1.0))+v3;"
				+"				float f1=fabsf(dot(v1,mask));"
				+"				exitoct = (recursions > 0) && (f1<0.1);"
				+"			}"
				+"			else if(voxelstate == 1 && recursions<=5)"
				+"			{"
				+"				recursions++;"
				+"				size *= 0.5;"
				+"				float3 mask2 = step(make_float3(size,size,size),lro);"
				+"				fro = fro + mask2*size;"
				+"				lro = lro - mask2*size;"
				+"			}"
				+"			else if (voxelstate == 0||voxelstate == 2||recursions > 5)"
				+"			{"
				+"				float3 hit =  dc_randomoctree_voxel (lro, rd, ird, size);"
				+"				if (hit.x < fminf(hit.y,hit.z)) {"
				+"					mask = make_float3(1.,0.,0.);"
				+"				} else if (hit.y < hit.z) {"
				+"					mask = make_float3(0.,1.,0.);"
				+"				} else {"
				+"					mask = make_float3(0.,0.,1.);"
				+"				}"
				+"				float len = dot(hit,mask);"
				+"				if (voxelstate == 2) {"
				+"					break;"
				+"				}"
				+"				dist += len;"
				+"				float3 v5=rd*len- mask*sign(rd)*size;"
				+"				lro = lro + v5;"
				+"				float3 newfro = fro + mask*sign(rd)*size;"
				+"				float3 v4=newfro/(size/0.5)+ 0.25;"
//				+"				exitoct = (floorf(v4)!=floorf(v4))&&(recursions>0);"
				+"				exitoct = recursions>0;"				
				+"				fro = newfro;"
				+"				lastmask = mask;"
				+"			}"
				+"			if(grid==1.0)"
				+"			{"
				+"				float3 q = abs(lro/(size)-(0.5))*(make_float3(1.0,1.0,1.0)-(lastmask));"
				+"				edge = fminf(edge,-(fmaxf(fmaxf(q.x,q.y),q.z)-0.5)*80.0*size);"
				+"			}"
				+"		}"
				+"		ro = ro + rd*dist;"
				+"		float3 color=make_float3(0.0,0.0,0.0);"
				+"		if( (i < steps) && (dist < maxdistance) )"
				+"		{"
				+"			float val = fract(dot(fro,make_float3(15.23,754.345,3.454)));"
				+"			normal = lastmask*(sign(rd))*(-1.0);"
				+"			color = sinf(make_float3(39.896,57.3225,48.25)*(val))*(0.5)+ 0.5;"
				+"			color= color*(normal*0.25+ 0.75);"
				+"			if(borders==1.0)"
				+"			{"
				+"				float3 q = abs(lro/(size)-(0.5))*(make_float3(1.0,1.0,1.0)-(lastmask));"
				+"				edge = clamp(-(fmaxf(fmaxf(q.x,q.y),q.z)-0.5)*20.0*size,0.0,edge);"
				+"			}"
				+"			if(blackborders==1.0)"
				+"				color = color*edge;"
				+"			else"
				+"				color = make_float3(1.0,1.0,1.0)-(make_float3(1.0,1.,1.0)-color)*edge;"
				+"		}"
				+"      else {"
				+"			if (blackborders==1.0)"
				+"				color = make_float3(edge,edge,edge);"
				+"			else"
				+"				color = make_float3(1.0-edge, 1.0-edge,1.0-edge);"
				+"		}"
				+"		color =  sqrt(color);"
				+"		return color;"
				+"	}";
	 }	
}

