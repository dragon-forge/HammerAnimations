package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public interface IEmitterUpdate
		extends IParticleComponent
{
	void update(ParticleEmitter emitter);
}