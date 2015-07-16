package org.jwildfire.create.tina.variation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.batch.HeadlessBatchRendererController;
import org.jwildfire.create.tina.batch.Job;
import org.jwildfire.create.tina.batch.JobRenderThread;
import org.jwildfire.create.tina.batch.JobRenderThreadController;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.palette.RGBPalette;

public class PolylogarithmTest 
{
	//n - int
	//zpow - double
	
	@Test
	public void testBean()
	{
		PolylogarithmFunc bf = new PolylogarithmFunc();
		bf.setParameter("n", 0);
		bf.setParameter("zpow", 0);
		bf.init(null, null, new XForm(), 10);
		
		Assert.assertNotNull(bf.getName());
		bf.setParameter("n", 5);
		bf.setParameter("zpow", 6);
		try {
			bf.setParameter("doesn't exist", 5);
			Assert.fail("Expected exception");
		} catch (Exception e) {
		}
		Assert.assertEquals(2,bf.getParameterValues().length);
		Assert.assertEquals(5,bf.getParameter("n"));
		Assert.assertEquals(6.0,bf.getParameter("zpow"));
		
	}
	
	@Test @Ignore
	public void visual() throws Exception
	{
		int side = 400;
		ResolutionProfile respro = new ResolutionProfile(true, side, side);
		int quality=200;
		Flame f = new Flame();
		f.setWidth(side);
		f.setHeight(side);
		f.setGamma(4.0);
		f.setBGTransparency(false);
		f.setAntialiasAmount(.1);
		f.setAntialiasRadius(.1);
		Layer l = new Layer();
		XForm xf = new XForm();
		SquishFunc df = new SquishFunc();
		df.setParameter("power", 10);
		xf.addVariation(1, df);
		PolylogarithmFunc bf = new PolylogarithmFunc();
		bf.setParameter("n", 2);
		bf.setParameter("zpow", 1);
		xf.addVariation(.1, bf);
		f.setCamZoom(6);
		xf.setWeight(10);
		l.getXForms().add(xf);
		RGBPalette pal = new RGBPalette();
		for(int i=0;i<256;i++)
			pal.addColor(0, i, 255);
		pal.setFlam3Name("PolylogarithmFunc");
		l.setPalette(pal);
		f.setResolutionProfile(respro);
		f.getLayers().clear();
		f.getLayers().add(l);
		f.setPixelsPerUnit(43.75);
		new FlameWriter().writeFlame(f, "/dev/shm/PolylogarithmFunc.flame");
		JobRenderThreadController controller = new HeadlessBatchRendererController();
		List<Job> joblist = new ArrayList<>();
		Job j = new Job();
		j.setCustomHeight(side);
		j.setCustomWidth(side);
		j.setCustomQuality(quality);
		j.setFlameFilename("/dev/shm/PolylogarithmFunc.flame");
		joblist.add(j);
		
		QualityProfile qualpro = new QualityProfile();
		qualpro.setQuality(quality);
		JobRenderThread job = new JobRenderThread(controller, joblist, respro, qualpro, true);
		job.run();
	}
}
