package org.zeith.hammeranims.core.contents.particles.components.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomLifetimeLooping
		extends ParcomLifetime
{
	public InterpolatedDouble<ParticleVariables> sleepTime = InterpolatedDouble.zero();
	
	public ParcomLifetimeLooping(JsonElement elem)
	{
		super(elem);
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("sleep_time")) this.sleepTime = InterpolatedDouble.parse(element.get("sleep_time"));
	}
	
	@Override
	public void update(ParticleEmitter emitter)
	{
		double active = this.activeTime.get(emitter.vars);
		double sleep = this.sleepTime.get(emitter.vars);
		double age = emitter.getAge();
		emitter.lifetime = (int) (active * 20);
		if(age >= active && emitter.playing) emitter.stop();
		if(age >= sleep && !emitter.playing) emitter.start();
	}
}