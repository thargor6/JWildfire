package org.jwildfire.create.tina.random;

import java.util.*;
import java.util.Map.Entry;

import org.junit.*;
import org.jwildfire.base.Prefs;

public class RandomTests 
{
	static enum bucket{Q1,Q2,Q3,Q4}
	@Test
	/**
	 * make sure it is uniform distribution
	 */
	public void distributionRangeZiggurat()
	{
		AbstractRandomGenerator zr = new ZigguratRandomGenerator();
		HashMap<bucket, Long> buckets = new HashMap<>();
		for(bucket bi:bucket.values())
			buckets.put(bi,0l);
		for(int ii=0;ii<100000;ii++)
		{
			double i =zr.random();
			if(i<.25&&i>0)
				buckets.put(bucket.Q1, buckets.get(bucket.Q1)+1);
			else if(i<.5&&i>=.25)
				buckets.put(bucket.Q2, buckets.get(bucket.Q2)+1);
			else if(i<.75&&i>=.5)
				buckets.put(bucket.Q3, buckets.get(bucket.Q3)+1);
			else if(i<1&&i>=.75)
				buckets.put(bucket.Q4, buckets.get(bucket.Q4)+1);
		}
		for(Entry<bucket, Long> s:buckets.entrySet())
		{
			System.out.println(s.getKey()+"\t"+s.getValue());
			Assert.assertTrue(s.getValue()>23000);
		}
	}
	@Test
	/**
	 * Note this was returning ~100 duplicates using ziggurat/integer 
	 * resulting in poor pictures for high quality settings, this seems resolved by using 
	 * long LBSR (64bit) instead of integer.
	 */
	public void distributionQualityZiggurat()
	{
		AbstractRandomGenerator zr = new ZigguratRandomGenerator();
		HashMap<Double, Integer> buckets = new HashMap<>();
		int dupes=0;
		for(int ii=0;ii<4000000;ii++)
		{
			Double d = zr.random();
			if(!buckets.keySet().contains(d))
				buckets.put(d,1);
			else
			{
				dupes++;
			}
			
		}
		Assert.assertTrue("Error, duplicates mean poor picture quality because it isn't doing full distribution"
				,0==dupes);
	}
	@Test
	public void testAbstractRandomGenerator() throws Exception
	{
		Prefs p = Prefs.newInstance();
		for(RandomGeneratorType grt:RandomGeneratorType.values())
		{
			AbstractRandomGenerator gr = RandomGeneratorFactory.getInstance(p, grt);
			for (int i=0;i<20;i++)
				Assert.assertTrue(gr.getClass().getSimpleName(),gr.random()<1);
			for (int i=0;i<20;i++)
				Assert.assertTrue(gr.getClass().getSimpleName(),gr.random(100)<100);
			gr.randomize(16L);
			if(!(gr instanceof MarsagliaOpenCLRandomGenerator))
				gr.cleanup();
		}
	}
	@Test
	public void testNullFactory() throws Exception
	{
		try {
			RandomGeneratorFactory.getInstance(null,null);
			Assert.fail("Expecting error");
		} catch (Exception e) 
		{
			Assert.assertNotNull(e);
		}
	}
}
