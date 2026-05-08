package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.emitter.*;

public interface IParticleInitialize
		extends IParticleCompInstance
{
	void apply(ParticleEmitter emitter, BedrockParticle particle);
}