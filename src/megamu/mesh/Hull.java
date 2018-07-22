package megamu.mesh;

import quickhull3d.QuickHull3D;

public class Hull {
	
	int[] extrema;
	MPolygon region;

	public Hull( float[][] points ){
		
		if( points.length < 3 ){
			extrema = new int[points.length];
			region = new MPolygon(points.length);
			for(int i=0; i<points.length; i++){
				extrema[i] = i;
				region.add(points[i][0], points[i][1]);
			}
			return;
		}

		// build points array for qhull
		double qPoints[] = new double[ points.length*3 + 3 ];
		double avgX = 0;
		double avgY = 0;
		for(int i=0; i<points.length; i++){
			qPoints[i*3] = points[i][0];
			qPoints[i*3+1] = points[i][1];
			qPoints[i*3+2] = 0;
			avgX += points[i][0];
			avgY += points[i][1];
		}
		qPoints[ qPoints.length-3 ] = avgX/(qPoints.length-1);
		qPoints[ qPoints.length-2 ] = avgY/(qPoints.length-1);
		qPoints[ qPoints.length-1 ] = 1000D;

		// prepare quickhull
		QuickHull3D quickHull = new QuickHull3D(qPoints);
		int[][] faces = quickHull.getFaces(QuickHull3D.POINT_RELATIVE + QuickHull3D.CLOCKWISE);
		
		// find extrema
		for(int i=0; i<faces.length; i++){
			boolean isFace = true;
			for(int j=0; j<faces[i].length; j++){
				if(faces[i][j]==points.length){
					isFace = false;
					break;
				}
			}
			if(isFace){
				extrema = faces[i];
				break;
			}
		}
		
		// make polygon
		region = new MPolygon(extrema.length);
		for(int i=0; i<extrema.length; i++){
			region.add(points[extrema[i]][0], points[extrema[i]][1]);
		}
		
	}
	
	public int[] getExtrema(){
		return extrema;
	}
	
	public MPolygon getRegion(){
		return region;
	}

}
