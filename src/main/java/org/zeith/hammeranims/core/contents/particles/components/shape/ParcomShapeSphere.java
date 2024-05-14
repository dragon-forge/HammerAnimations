package org.zeith.hammeranims.core.contents.particles.components.shape;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.joml.Vector3f;

public class ParcomShapeSphere
		extends ParcomShapeBase
{
	public InterpolatedDouble<ParticleVariables> radius = InterpolatedDouble.zero();
	
	public ParcomShapeSphere(JsonElement elem)
	{
		super(elem);
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("radius")) this.radius = InterpolatedDouble.parse(element.get("radius"));
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		float centerX = (float) this.offset[0].get(v);
		float centerY = (float) this.offset[1].get(v);
		float centerZ = (float) this.offset[2].get(v);
		float radius = (float) this.radius.get(v);
		
		Vector3f direction = new Vector3f((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1);
		direction.normalize();
		
		if(!this.surface)
		{
			radius *= Math.random();
		}
		
		direction.mul(radius);
		
		particle.position.x = centerX + direction.x;
		particle.position.y = centerY + direction.y;
		particle.position.z = centerZ + direction.z;
		
		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}