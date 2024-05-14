package org.zeith.hammeranims.core.contents.particles.components.shape;

import com.google.gson.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.joml.*;

import java.lang.Math;

public class ParcomShapeDisc
		extends ParcomShapeSphere
{
	@SuppressWarnings("rawtypes")
	public InterpolatedDouble[] normal = { InterpolatedDouble.zero(), InterpolatedDouble.one(), InterpolatedDouble.zero() };
	
	public ParcomShapeDisc(JsonElement elem)
	{
		super(elem);
		
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		
		if(!element.has("plane_normal")) return;
		
		JsonElement normal = element.get("plane_normal");
		
		if(normal.isJsonPrimitive())
		{
			String axis = normal.getAsString().toLowerCase();
			
			if(axis.equals("x"))
			{
				this.normal[0] = InterpolatedDouble.one();
				this.normal[1] = InterpolatedDouble.zero();
			} else if(axis.equals("z"))
			{
				this.normal[1] = InterpolatedDouble.zero();
				this.normal[2] = InterpolatedDouble.one();
			}
		} else
		{
			JsonArray array = element.getAsJsonArray("plane_normal");
			
			if(array.size() >= 3)
			{
				this.normal[0] = InterpolatedDouble.parse(array.get(0));
				this.normal[1] = InterpolatedDouble.parse(array.get(1));
				this.normal[2] = InterpolatedDouble.parse(array.get(2));
			}
		}
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		float centerX = (float) this.offset[0].get(v);
		float centerY = (float) this.offset[1].get(v);
		float centerZ = (float) this.offset[2].get(v);
		
		Vector3f normal = new Vector3f((float) this.normal[0].get(v), (float) this.normal[1].get(v), (float) this.normal[2].get(v));
		
		normal.normalize();
		
		Quaternionf quaternion = new Quaternionf(normal.x, normal.y, normal.z, 1);
		Matrix4f rotation = new Matrix4f();
		rotation.set(quaternion);
		
		Vector4f position = new Vector4f((float) Math.random() - 0.5F, 0, (float) Math.random() - 0.5F, 0);
		position.normalize();
		rotation.transform(position);
		
		position.mul((float) (this.radius.get(v) * (this.surface ? 1 : Math.random())));
		position.add(new Vector4f(centerX, centerY, centerZ, 0));
		
		particle.position.x += position.x;
		particle.position.y += position.y;
		particle.position.z += position.z;
		
		this.direction.applyDirection(particle, centerX, centerY, centerZ);
	}
}