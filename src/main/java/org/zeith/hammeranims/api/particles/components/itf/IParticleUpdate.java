package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public interface IParticleUpdate
		extends IParticleComponent
{
	void update(ParticleEmitter emitter, BedrockParticle particle);
}