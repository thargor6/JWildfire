package org.jwildfire.create.tina.random;

import org.junit.*;
import org.jwildfire.base.Prefs;

public class RandomTests 
{
	@Test
	public void testAbstractRandomGenerator() throws Exception
	{
		Prefs p = Prefs.newInstance();
		p.setTinaUseExperimentalOpenClCode(true);
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
		p.setTinaUseExperimentalOpenClCode(false);
		for(RandomGeneratorType grt:RandomGeneratorType.values())
		{
			AbstractRandomGenerator gr = RandomGeneratorFactory.getInstance(p, grt);
			for (int i=0;i<20;i++)
				Assert.assertTrue(gr.getClass().getSimpleName(),gr.random()<1);
			for (int i=0;i<20;i++)
				Assert.assertTrue(gr.getClass().getSimpleName(),gr.random(100)<100);
			gr.randomize(16L);
			if(!(gr instanceof MarsagliaOpenCLRandomGenerator))
				RandomGeneratorFactory.cleanup();
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
