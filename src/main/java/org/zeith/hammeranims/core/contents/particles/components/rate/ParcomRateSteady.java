package org.zeith.hammeranims.core.contents.particles.components.rate;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IParticlePostRender;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomRateSteady
		extends ParcomRate
{
	public static final LzFactory DEFAULT_PARTICLES = InterpolatedDouble.constant(50);
	
	public LzFactory spawnRate = InterpolatedDouble.one();
	
	public ParcomRateSteady(JsonElement elem)
	{
		this.particles = DEFAULT_PARTICLES;
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("spawn_rate")) this.spawnRate = InterpolatedDouble.parse(element.get("spawn_rate"));
		if(element.has("max_particles")) this.particles = InterpolatedDouble.parse(element.get("max_particles"));
	}
	
	@Override
	public int getSortingIndex()
	{
		return 10;
	}
	
	@Override
	public ParcomRateInstance createInstance(LzVariableStore vars)
	{
		return new ParcomRateSteadyInstance(
				particles.instantiate(vars),
				spawnRate.instantiate(vars)
		);
	}
	
	public static class ParcomRateSteadyInstance
			extends ParcomRateInstance
			implements IParticlePostRender
	{
		protected final LzExpression spawnRate;
		
		public ParcomRateSteadyInstance(LzExpression particles, LzExpression spawnRate)
		{
			super(particles);
			this.spawnRate = spawnRate;
		}
		
		@Override
		public void postRender(ParticleEmitter emitter, float partialTicks)
		{
			if(emitter.playing)
			{
				double particles = emitter.getAge(partialTicks) * this.spawnRate.get();
				double diff = particles - emitter.spawnedParticles;
				double spawn = Math.round(diff);
				
				if(spawn > 0)
				{
					emitter.setEmitterVariables(partialTicks);
					
					double track = spawn;
					
					for(int i = 0; i < spawn; i++)
					{
						if(emitter.particles.size() < this.particles.get())
						{
							emitter.spawnParticle();
						} else
						{
							track -= 1;
						}
					}
					
					emitter.spawnedParticles += track;
				}
			}
		}
	}
}