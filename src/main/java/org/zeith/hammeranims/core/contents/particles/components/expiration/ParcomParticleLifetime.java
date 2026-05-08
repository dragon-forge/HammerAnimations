package org.zeith.hammeranims.core.contents.particles.components.expiration;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.components.itf.*;
import org.zeith.hammeranims.api.particles.emitter.*;

public class ParcomParticleLifetime
		implements IParticleComponent
{
	public LzFactory expression = InterpolatedDouble.zero();
	public boolean max;
	
	public ParcomParticleLifetime(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		JsonElement expression = null;
		
		if(element.has("expiration_expression"))
		{
			expression = element.get("expiration_expression");
			this.max = false;
		} else if(element.has("max_lifetime"))
		{
			expression = element.get("max_lifetime");
			this.max = true;
		} else
		{
			throw new JsonParseException("No expiration_expression or max_lifetime was found in minecraft:particle_lifetime_expression component");
		}
		
		this.expression = InterpolatedDouble.parse(expression);
	}
	
	@Override
	public IParticleCompInstance createInstance(LzVariableStore vars)
	{
		return new ParcomParticleLifetimeInstance(
				expression.instantiate(vars),
				max
		);
	}
	
	public static class ParcomParticleLifetimeInstance
			implements IParticleInitialize, IParticleUpdate
	{
		public final LzExpression expression;
		public final boolean max;
		
		public ParcomParticleLifetimeInstance(LzExpression expression, boolean max)
		{
			this.expression = expression;
			this.max = max;
		}
		
		@Override
		public void apply(ParticleEmitter emitter, BedrockParticle particle)
		{
			if(this.max)
			{
				particle.lifetime = (int) (this.expression.get() * 20);
			} else
			{
				particle.lifetime = -1;
			}
		}
		
		@Override
		public void update(ParticleEmitter emitter, BedrockParticle particle)
		{
			if(!this.max && this.expression.get() != 0)
			{
				particle.dead = true;
			}
		}
	}
}
