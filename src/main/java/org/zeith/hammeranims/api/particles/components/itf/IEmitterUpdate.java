package org.zeith.hammeranims.api.particles.components.itf;

import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public interface IEmitterUpdate
		extends IParticleCompInstance
{
	void update(ParticleEmitter emitter);
}