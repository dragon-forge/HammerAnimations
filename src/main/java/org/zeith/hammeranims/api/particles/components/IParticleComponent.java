package org.zeith.hammeranims.api.particles.components;

import dev.zeith.lzvm.LzVariableStore;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;

public interface IParticleComponent
{
	default int getSortingIndex()
	{
		return 0;
	}
	
	IParticleCompInstance createInstance(LzVariableStore vars);
}