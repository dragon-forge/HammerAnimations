package org.zeith.hammeranims.core.contents.particles.components.expiration;

import com.google.gson.*;
import org.joml.Vector3d;
import org.zeith.hammeranims.api.particles.components.INonAnimatedParticleComponent;
import org.zeith.hammeranims.api.particles.components.itf.IParticleUpdate;
import org.zeith.hammeranims.api.particles.emitter.*;

public class ParcomKillPlane
		implements INonAnimatedParticleComponent, IParticleUpdate
{
	public float a;
	public float b;
	public float c;
	public float d;
	
	public ParcomKillPlane(JsonElement element)
	{
		if(!element.isJsonArray()) return;
		
		JsonArray array = element.getAsJsonArray();
		
		if(array.size() >= 4)
		{
			this.a = array.get(0).getAsFloat();
			this.b = array.get(1).getAsFloat();
			this.c = array.get(2).getAsFloat();
			this.d = array.get(3).getAsFloat();
		}
	}
	
	@Override
	public void update(ParticleEmitter emitter, BedrockParticle particle)
	{
		if(particle.dead) return;
		
		Vector3d prevLocal = new Vector3d(particle.prevPosition);
		Vector3d local = new Vector3d(particle.position);
		
		if(!particle.relativePosition)
		{
			local.sub(emitter.lastGlobal);
			prevLocal.sub(emitter.lastGlobal);
		}
		
		double prev = this.a * prevLocal.x + this.b * prevLocal.y + this.c * prevLocal.z + this.d;
		double now = this.a * local.x + this.b * local.y + this.c * local.z + this.d;
		
		if((prev > 0 && now < 0) || (prev < 0 && now > 0))
			particle.dead = true;
	}
	
	@Override
	public int getSortingIndex()
	{
		return 100;
	}
}