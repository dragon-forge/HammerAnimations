package org.zeith.hammeranims.api.particles.components;

import dev.zeith.lzvm.LzVariableStore;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;

public interface INonAnimatedParticleComponent
		extends IParticleComponent, IParticleCompInstance
{
	@Override
	default IParticleCompInstance createInstance(LzVariableStore vars)
	{
		return this;
	}
}