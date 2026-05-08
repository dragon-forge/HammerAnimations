package org.zeith.hammeranims.core.contents.particles.components.shape;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.joml.Vector3f;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomShapeSphere
		extends ParcomShapeBase
{
	public LzFactory radius = InterpolatedDouble.zero();
	
	public ParcomShapeSphere(JsonElement elem)
	{
		super(elem);
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("radius")) this.radius = InterpolatedDouble.parse(element.get("radius"));
	}
	
	@Override
	public ParcomShapeBaseInstance createInstance(LzVariableStore vars)
	{
		return new ParcomShapeSphereInstance(
				LzFactory.instantiate(vars, offset),
				direction.apply(vars),
				surface,
				radius.instantiate(vars)
		);
	}
	
	public static class ParcomShapeSphereInstance
			extends ParcomShapeBaseInstance
	{
		public final LzExpression radius;
		
		public ParcomShapeSphereInstance(LzExpression[] offset, ShapeDirection direction, boolean surface, LzExpression radius)
		{
			super(offset, direction, surface);
			this.radius = radius;
		}
		
		@Override
		public void apply(ParticleEmitter emitter, BedrockParticle particle)
		{
			float centerX = (float) this.offset[0].get();
			float centerY = (float) this.offset[1].get();
			float centerZ = (float) this.offset[2].get();
			float radius = (float) this.radius.get();
			
			Vector3f direction = new Vector3f((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1);
			direction.normalize();
			
			if(!this.surface) radius *= Math.random();
			
			direction.mul(radius);
			
			particle.position.x = centerX + direction.x;
			particle.position.y = centerY + direction.y;
			particle.position.z = centerZ + direction.z;
			
			this.direction.applyDirection(particle, centerX, centerY, centerZ);
		}
	}
}