package org.zeith.hammeranims.api.particles.components;

public interface IParticleComponent
{
	default int getSortingIndex()
	{
		return 0;
	}
}