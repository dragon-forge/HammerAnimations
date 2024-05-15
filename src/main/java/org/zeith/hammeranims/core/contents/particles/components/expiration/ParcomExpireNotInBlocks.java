package org.zeith.hammeranims.core.contents.particles.components.expiration;

import com.google.gson.JsonElement;
import org.zeith.hammeranims.api.particles.components.itf.IParticleUpdate;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomExpireNotInBlocks
		extends ParcomExpireBlocks
		implements IParticleUpdate
{
	public ParcomExpireNotInBlocks(JsonElement elem)
	{
		super(elem);
	}
	
	@Override
	public void update(ParticleEmitter emitter, BedrockParticle particle)
	{
		if(particle.dead || emitter.world == null) return;
		var current = getBlockState(emitter, particle);
		if(!matches(current)) particle.dead = true;
	}
}