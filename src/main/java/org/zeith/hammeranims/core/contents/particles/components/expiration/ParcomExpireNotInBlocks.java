package org.zeith.hammeranims.core.contents.particles.components.expiration;

import com.google.gson.JsonElement;
import dev.zeith.lzvm.LzVariableStore;
import lombok.var;
import org.zeith.hammeranims.api.particles.components.itf.IParticleUpdate;
import org.zeith.hammeranims.api.particles.emitter.*;

public class ParcomExpireNotInBlocks
		extends ParcomExpireBlocks
{
	public ParcomExpireNotInBlocks(JsonElement element)
	{
		super(element);
	}
	
	@Override
	public ParcomExpireBlocksInstance createInstance(LzVariableStore vars)
	{
		return new ParcomExpireNotInBlocksInstance(this);
	}
	
	public static class ParcomExpireNotInBlocksInstance
			extends ParcomExpireBlocksInstance
			implements IParticleUpdate
	{
		public ParcomExpireNotInBlocksInstance(ParcomExpireBlocks owner)
		{
			super(owner);
		}
		
		@Override
		public void update(ParticleEmitter emitter, BedrockParticle particle)
		{
			if(particle.dead || emitter.world == null) return;
			var current = getBlockState(emitter, particle);
			if(!matches(current)) particle.dead = true;
		}
	}
}
