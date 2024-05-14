package org.zeith.hammeranims.core.contents.particles.components.rate;

import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomRate
		implements IParticleComponent
{
	protected InterpolatedDouble<ParticleVariables> particles = InterpolatedDouble.constant(10);
	
	public ParcomRate()
	{
	}
	
	public InterpolatedDouble<ParticleVariables> getParticles()
	{
		return particles;
	}
}