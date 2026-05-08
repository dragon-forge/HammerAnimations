package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.emitter.*;

public interface IParticleExpiry
		extends IParticleCompInstance
{
	void expire(ParticleEmitter emitter, BedrockParticle particle);
}