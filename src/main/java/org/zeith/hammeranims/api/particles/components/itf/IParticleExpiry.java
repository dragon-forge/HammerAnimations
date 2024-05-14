package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public interface IParticleExpiry
		extends IParticleComponent
{
	void expire(ParticleEmitter emitter, BedrockParticle particle);
}