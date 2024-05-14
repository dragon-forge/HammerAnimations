package org.zeith.hammeranims.core.contents.particles.components.motion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IParticleInitialize;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomInitialSpin
		implements IParticleInitialize
{
	public InterpolatedDouble<ParticleVariables> rotation = InterpolatedDouble.zero();
	public InterpolatedDouble<ParticleVariables> rate = InterpolatedDouble.zero();
	
	public ParcomInitialSpin(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("rotation")) this.rotation = InterpolatedDouble.parse(element.get("rotation"));
		if(element.has("rotation_rate")) this.rate = InterpolatedDouble.parse(element.get("rotation_rate"));
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		particle.initialRotation = (float) this.rotation.get(v);
		particle.rotationVelocity = (float) this.rate.get(v) / 20;
	}
}
