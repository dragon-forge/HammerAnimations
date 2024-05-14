package org.zeith.hammeranims.core.contents.particles.components.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.particles.components.itf.IParticleInitialize;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomLocalSpace
		implements IParticleInitialize
{
	public boolean position;
	public boolean rotation;
	public boolean scale;
	public boolean scaleBillboard;
	public boolean direction;
	public boolean acceleration;
	public boolean gravity;
	public float linearVelocity;
	public float angularVelocity;
	
	public ParcomLocalSpace(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("position")) this.position = element.get("position").getAsBoolean();
		if(element.has("rotation")) this.rotation = element.get("rotation").getAsBoolean();
		if(element.has("scale")) this.scale = element.get("scale").getAsBoolean();
		if(element.has("scale_billboard")) this.scaleBillboard = element.get("scale_billboard").getAsBoolean();
		if(element.has("direction")) this.direction = element.get("direction").getAsBoolean();
		if(element.has("acceleration")) this.acceleration = element.get("acceleration").getAsBoolean();
		if(element.has("gravity")) this.gravity = element.get("gravity").getAsBoolean();
		if(element.has("linear_velocity")) this.linearVelocity = element.get("linear_velocity").getAsFloat();
		if(element.has("angular_velocity")) this.angularVelocity = element.get("angular_velocity").getAsFloat();
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		particle.relativePosition = this.position;
		particle.relativeRotation = this.rotation;
		particle.relativeScale = this.scale;
		particle.relativeScaleBillboard = this.scaleBillboard;
		particle.relativeDirection = this.direction;
		particle.relativeAcceleration = this.acceleration;
		particle.gravity = this.gravity;
		particle.linearVelocity = this.linearVelocity;
		particle.angularVelocity = this.angularVelocity;
		
		particle.setupMatrix(emitter);
	}
	
	@Override
	public int getSortingIndex()
	{
		return 6;
	}
}