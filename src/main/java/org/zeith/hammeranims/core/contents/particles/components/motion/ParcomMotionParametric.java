package org.zeith.hammeranims.core.contents.particles.components.motion;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.components.itf.*;
import org.zeith.hammeranims.api.particles.emitter.*;
import org.zeith.hammeranims.joml.Vector3d;

public class ParcomMotionParametric
		implements IParticleComponent
{
	public LzFactory[] position = {InterpolatedDouble.zero(), InterpolatedDouble.zero(), InterpolatedDouble.zero()};
	public LzFactory rotation = InterpolatedDouble.zero();
	
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
	public int getSortingIndex()
	{
		return 10;
	}
	
	@Override
	public IParticleCompInstance createInstance(LzVariableStore vars)
	{
		return new ParcomMotionParametricInstance(
				LzFactory.instantiate(vars, position),
				rotation.instantiate(vars)
		);
	}
	
	public static class ParcomMotionParametricInstance
			implements IParticleInitialize, IParticleUpdate
	{
		public final LzExpression[] position;
		public final LzExpression rotation;
		
		public ParcomMotionParametricInstance(LzExpression[] position, LzExpression rotation)
		{
			this.position = position;
			this.rotation = rotation;
		}
		
		@Override
		public void apply(ParticleEmitter emitter, BedrockParticle particle)
		{
			Vector3d position = new Vector3d(this.position[0].get(), this.position[1].get(), this.position[2].get());
			
			particle.manual = true;
			particle.initialPosition.set(particle.position);
			
			particle.matrix.transform(position);
			particle.position.x = particle.initialPosition.x + position.x;
			particle.position.y = particle.initialPosition.y + position.y;
			particle.position.z = particle.initialPosition.z + position.z;
			particle.rotation = (float) this.rotation.get();
		}
		
		@Override
		public void update(ParticleEmitter emitter, BedrockParticle particle)
		{
			Vector3d position = new Vector3d(this.position[0].get(), this.position[1].get(), this.position[2].get());
			
			particle.matrix.transform(position);
			particle.position.x = particle.initialPosition.x + position.x;
			particle.position.y = particle.initialPosition.y + position.y;
			particle.position.z = particle.initialPosition.z + position.z;
			particle.rotation = (float) this.rotation.get();
		}
	}
}