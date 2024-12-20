package org.zeith.hammeranims.standalone.jobs;

import java.util.ArrayList;
import java.util.List;

public interface IRenderJobSource
{
	IRenderJob asJob();
	
	default IRenderJobSource then(IRenderJobSource second)
	{
		IRenderJobSource first = this;
		return () -> first.asJob().then(second.asJob());
	}
	
	static RenderJobAccumulator start()
	{
		return new RenderJobAccumulator();
	}
	
	static IRenderJob of(IRenderJob job)
	{
		return job;
	}
	
	class RenderJobAccumulator
			implements IRenderJob
	{
		private final List<IRenderJob> jobs = new ArrayList<>();
		
		@Override
		public RenderJobAccumulator then(IRenderJobSource second)
		{
			jobs.add(second.asJob());
			return this;
		}
		
		@Override
		public RenderJobAccumulator then(IRenderJob second)
		{
			jobs.add(second);
			return this;
		}
		
		@Override
		public void render(GlWindow window)
		{
			for(IRenderJob job : jobs)
			{
				job.render(window);
			}
		}
	}
}