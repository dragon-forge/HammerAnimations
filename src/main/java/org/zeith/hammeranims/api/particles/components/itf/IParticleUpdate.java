package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.emitter.*;

public interface IParticleUpdate
		extends IParticleCompInstance
{
	void update(ParticleEmitter emitter, BedrockParticle particle);
}