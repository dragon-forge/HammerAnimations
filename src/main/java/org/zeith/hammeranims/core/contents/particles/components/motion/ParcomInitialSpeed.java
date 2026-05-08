package org.zeith.hammeranims.core.contents.particles.components.motion;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.components.itf.IParticleInitialize;
import org.zeith.hammeranims.api.particles.emitter.*;

public class ParcomInitialSpeed
		implements IParticleComponent
{
	public LzFactory speed = InterpolatedDouble.one();
	public LzFactory[] direction;
	
	public ParcomInitialSpeed(JsonElement element)
	{
		if(element.isJsonArray())
		{
			JsonArray array = element.getAsJsonArray();
			
			if(array.size() >= 3)
			{
				this.direction = new LzFactory[] {
						InterpolatedDouble.parse(array.get(0)),
						InterpolatedDouble.parse(array.get(1)),
						InterpolatedDouble.parse(array.get(2))
				};
			}
		} else if(element.isJsonPrimitive())
		{
			this.speed = InterpolatedDouble.parse(element);
		}
	}
	
	@Override
	public int getSortingIndex()
	{
		return 5;
	}
	
	@Override
	public IParticleCompInstance createInstance(LzVariableStore vars)
	{
		return new ParcomInitialSpeedInstance(
				speed.instantiate(vars),
				LzFactory.instantiate(vars, direction)
		);
	}
	
	public static class ParcomInitialSpeedInstance
			implements IParticleInitialize
	{
		public final LzExpression speed;
		public final LzExpression[] direction;
		
		public ParcomInitialSpeedInstance(LzExpression speed, LzExpression[] direction)
		{
			this.speed = speed;
			this.direction = direction;
		}
		
		@Override
		public void apply(ParticleEmitter emitter, BedrockParticle particle)
		{
			if(this.direction == null)
			{
				float v = (float) this.speed.get();
				particle.speed.mul(v);
				return;
			}
			
			particle.speed.set(
					(float) this.direction[0].get(),
					(float) this.direction[1].get(),
					(float) this.direction[2].get()
			);
		}
	}
}