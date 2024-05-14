package org.zeith.hammeranims.core.contents.particles.components.expiration;

import com.google.gson.JsonElement;
import org.zeith.hammeranims.api.particles.components.itf.IParticleUpdate;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomExpireInBlocks
		extends ParcomExpireBlocks
		implements IParticleUpdate
{
	public ParcomExpireInBlocks(JsonElement element)
	{
		super(element);
	}
	
	@Override
	public void update(ParticleEmitter emitter, BedrockParticle particle)
	{
		if(particle.dead || emitter.world == null) return;
		var current = this.getBlock(emitter, particle);
		if(this.blocks.contains(current)) particle.dead = true;
	}
}
