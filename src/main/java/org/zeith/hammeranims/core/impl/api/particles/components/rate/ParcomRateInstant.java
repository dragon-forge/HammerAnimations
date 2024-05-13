package org.zeith.hammeranims.core.impl.api.particles.components.rate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IEmitterUpdate;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomRateInstant
		extends ParcomRate
		implements IEmitterUpdate
{
	public static final InterpolatedDouble<ParticleVariables> DEFAULT_PARTICLES = InterpolatedDouble.constant(10);
	
	public ParcomRateInstant(JsonElement elem)
	{
		this.particles = DEFAULT_PARTICLES;
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("num_particles")) this.particles = InterpolatedDouble.parse(element.get("num_particles"));
	}
	
	@Override
	public void update(ParticleEmitter emitter)
	{
		double age = emitter.getAge();
		
		if(emitter.playing && Math.abs(age) < 0.0001)
		{
			emitter.setEmitterVariables(0);
			
			int pc = (int) this.particles.get(emitter.vars);
			for(int i = 0; i < pc; i++)
				emitter.spawnParticle();
		}
	}
}