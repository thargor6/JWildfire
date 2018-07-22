package megamu.mesh;


public class IntArray {

	int[] data;
	int length;
	
	static public int[] expand(int list[], int newSize) {
		    int temp[] = new int[newSize];
		    System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
		    return temp;
		  }
	  
	static public int[] expand(int list[]) {
	    return expand(list, list.length > 0 ? list.length << 1 : 1);
	  }
	
	public IntArray(){
		this(1);
	}

	public IntArray( int l ){
		data = new int[l];
		length = 0;
	}

	public void add( int d ){
		if( length==data.length )
			data = IntArray.expand(data);
		data[length++] = d;
	}

	public int get( int i ){
		return data[i];
	}

	public boolean contains( int d ){
		for(int i=0; i<length; i++)
			if(data[i]==d)
				return true;
		return false;
	}

}