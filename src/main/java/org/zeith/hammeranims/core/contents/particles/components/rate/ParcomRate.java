package org.zeith.hammeranims.core.contents.particles.components.rate;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import lombok.Getter;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;

public abstract class ParcomRate
		implements IParticleComponent
{
	protected LzFactory particles = InterpolatedDouble.constant(10);
	
	public ParcomRate()
	{
	}
	
	@Override
	public abstract ParcomRateInstance createInstance(LzVariableStore vars);
	
	@Getter
	public static class ParcomRateInstance
			implements IParticleCompInstance
	{
		public final LzExpression particles;
		
		public ParcomRateInstance(LzExpression particles)
		{
			this.particles = particles;
		}
	}
}