package org.zeith.hammeranims.core.contents.particles.components.rate;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IEmitterUpdate;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomRateInstant
		extends ParcomRate
{
	public static final LzFactory DEFAULT_PARTICLES = InterpolatedDouble.constant(10);
	
	public ParcomRateInstant(JsonElement elem)
	{
		this.particles = DEFAULT_PARTICLES;
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("num_particles")) this.particles = InterpolatedDouble.parse(element.get("num_particles"));
	}
	
	@Override
	public ParcomRateInstance createInstance(LzVariableStore vars)
	{
		return new ParcomRateInstantInstance(particles.instantiate(vars));
	}
	
	public static class ParcomRateInstantInstance
			extends ParcomRateInstance
			implements IEmitterUpdate
	{
		public ParcomRateInstantInstance(LzExpression particles)
		{
			super(particles);
		}
		
		@Override
		public void update(ParticleEmitter emitter)
		{
			double age = emitter.getAge();
			
			if(emitter.playing && Math.abs(age) < 0.0001)
			{
				emitter.setEmitterVariables(0);
				
				int pc = (int) this.particles.get();
				for(int i = 0; i < pc; i++)
					emitter.spawnParticle();
			}
		}
	}
}