package org.zeith.hammeranims.standalone.jobs;

import java.util.Objects;

public interface IRenderJob
		extends IRenderJobSource
{
	void render(GlWindow window);
	
	@Override
	default IRenderJob asJob()
	{
		return this;
	}
	
	default IRenderJob then(IRenderJob second)
	{
		Objects.requireNonNull(second, "IRenderJob.then(second==null)");
		IRenderJob first = this;
		return window ->
		{
			first.render(window);
			second.render(window);
		};
	}
	
	static IRenderJob singleShot(IRenderJob setup, IRenderJob tick, IRenderJob dispose)
	{
		Objects.requireNonNull(setup, "IRenderJob.singleShot(setup==null)");
		Objects.requireNonNull(tick, "IRenderJob.singleShot(tick==null)");
		Objects.requireNonNull(dispose, "IRenderJob.singleShot(dispose==null)");
		return (window) ->
		{
			setup.render(window);
			tick.render(window);
			dispose.render(window);
		};
	}
}