package org.zeith.hammeranims.core.contents.particles.components.motion;

import com.google.gson.*;
import org.joml.Vector3f;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IParticleInitialize;
import org.zeith.hammeranims.api.particles.components.itf.IParticleUpdate;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomMotionParametric
		implements IParticleInitialize, IParticleUpdate
{
	@SuppressWarnings("rawtypes")
	public InterpolatedDouble[] position = { InterpolatedDouble.zero(), InterpolatedDouble.zero(), InterpolatedDouble.zero() };
	public InterpolatedDouble<ParticleVariables> rotation = InterpolatedDouble.zero();
	
	public ParcomMotionParametric(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		
		if(element.has("relative_position") && element.get("relative_position").isJsonArray())
		{
			JsonArray array = element.get("relative_position").getAsJsonArray();
			this.position[0] = InterpolatedDouble.parse(array.get(0));
			this.position[1] = InterpolatedDouble.parse(array.get(1));
			this.position[2] = InterpolatedDouble.parse(array.get(2));
		}
		
		if(element.has("rotation"))
			this.rotation = InterpolatedDouble.parse(element.get("rotation"));
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		Vector3f position = new Vector3f((float) this.position[0].get(v), (float) this.position[1].get(v), (float) this.position[2].get(v));
		
		particle.manual = true;
		particle.initialPosition.set(particle.position);
		
		particle.matrix.transform(position);
		particle.position.x = particle.initialPosition.x + position.x;
		particle.position.y = particle.initialPosition.y + position.y;
		particle.position.z = particle.initialPosition.z + position.z;
		particle.rotation = (float) this.rotation.get(v);
	}
	
	@Override
	public void update(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		Vector3f position = new Vector3f((float) this.position[0].get(v), (float) this.position[1].get(v), (float) this.position[2].get(v));
		
		particle.matrix.transform(position);
		particle.position.x = particle.initialPosition.x + position.x;
		particle.position.y = particle.initialPosition.y + position.y;
		particle.position.z = particle.initialPosition.z + position.z;
		particle.rotation = (float) this.rotation.get(v);
	}
	
	@Override
	public int getSortingIndex()
	{
		return 10;
	}
}