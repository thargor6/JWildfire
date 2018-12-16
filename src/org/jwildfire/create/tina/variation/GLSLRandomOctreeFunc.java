package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;


import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;




public class GLSLRandomOctreeFunc  extends GLSLFunc {

	/*
	 * Variation : glsl_randomoctree
	 * Date: October 31, 2018
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_RESOLUTIONX = "Density Pixels";
	private static final String PARAM_SEED = "Seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_STEPS = "Steps";
	private static final String PARAM_RLR = "Rot. L-R";
	private static final String PARAM_RUD = "Rot. U-D";
	private static final String PARAM_DRAWGRID = "Grid";
	private static final String PARAM_BORDERS = "Borders";
	private static final String PARAM_BLACKBORDERS = "Black Borders";
	private static final String PARAM_GRADIENT = "Gradient"; 

	/*		#define drawgrid
	// #define fog
	#define borders
	#define blackborders
	// #define truchet
	#define objects */
	

	int resolutionX=1000000;
	int resolutionY=resolutionX;
	private int seed = 10000;
	double time=0.0;
	int steps=10;
	double mouseX=0.0;  // 0-1
	double mouseY=0.0;  // 0-1
	int grid=0;
	int borders=1;
	int blackborders=1;
	int gradient=0;
	
	  vec3 HASHSCALE3 = new vec3(.1031, .1030, .0973);
	  double emptycells = 0.5;
	  double subdivisions= 0.95; //should be higher than emptycells
	  int detail=5;
	  
	Random randomize=new Random(seed);
	

	private static final String[] paramNames = { PARAM_RESOLUTIONX,PARAM_SEED,PARAM_TIME,PARAM_STEPS,PARAM_RLR,PARAM_RUD,PARAM_DRAWGRID,PARAM_BORDERS,PARAM_BLACKBORDERS,PARAM_GRADIENT};

	  public double random(double r1, double r2)
	  {
		  return r1+(r2-r1)*randomize.nextDouble();
	  }
	

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
	
	public vec3 getRGBColor(int xp,int yp)
	{

		/*		#define drawgrid
		// #define fog
		#define borders
		#define blackborders
		// #define truchet
		#define objects */

		double x=(double)xp+0.5;
		double y=(double)yp+0.5;

		vec2 uv = new vec2(2.0* x/resolutionX-1.0, 2.0*y/resolutionY-1.0);


		double size = 1.0;

		double maxdistance=6.0;

		vec3 ro = new vec3(0.5+G.sin(time)*0.4,0.5+G.cos(time)*0.4,time);
		vec3 rd = G.normalize(new vec3(uv,1.0));
		vec2 rdyz= new vec2(rd.y,rd.z);
		vec2 rdxz= new vec2(rd.x,rd.z);

		//if the mouse is in the bottom left corner, don't rotate the camera
		if (G.length(new vec2(mouseX*resolutionX,mouseY*resolutionY)) > 40.0) 
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
 	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{

        
        int i=(int) (pContext.random()*resolutionX);
        int j=(int) (pContext.random()*resolutionY);
        
        vec3 color=new vec3(0.0);   
        
        color=getRGBColor(i,j);
       
        if(gradient==0)
        {
       	  int[] tcolor=new int[3];    	
           tcolor=dbl2int(color);  
     	
    	  pVarTP.rgbColor  =true;;
    	  pVarTP.redColor  =tcolor[0];
    	  pVarTP.greenColor=tcolor[1];
    	  pVarTP.blueColor =tcolor[2];
    		
        }
        else
        {
        	double s=(color.x+color.y+color.z);
        	double red=color.x/s;

        	pVarTP.color=Math.sin(red);

        }
	    pVarTP.x+= pAmount*((double)(i)/resolutionX - 0.5 );
		pVarTP.y+= pAmount*((double)(j)/resolutionY - 0.5 );

	}
	

	public String getName() {
		return "glsl_randomoctree";
	}

	public String[] getParameterNames() {
		return paramNames;
	}



	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { resolutionX,seed, time,steps,mouseX,mouseY,grid,borders,blackborders, gradient};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_RESOLUTIONX)) {
			resolutionX = (int)Tools.limitValue(pValue, 100 , 10000000);
		}
		else if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int) pValue;
		       randomize=new Random(seed);
		       time=random(0.0,10000000.0);
	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_STEPS)) {
			steps =(int)Tools.limitValue(pValue, 3 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_RLR)) {
			mouseX =Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_RUD)) {
			mouseY=Tools.limitValue(pValue, 0.0 , 1.0);
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
		else if (pName.equalsIgnoreCase(PARAM_GRADIENT)) {
			gradient = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else
			throw new IllegalArgumentException(pName);
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
	
}

