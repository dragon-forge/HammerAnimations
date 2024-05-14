package org.zeith.hammeranims.core.contents.particles.components.motion;

import com.google.gson.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IParticleUpdate;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomMotionDynamic
		implements IParticleUpdate
{
	@SuppressWarnings("rawtypes")
	public InterpolatedDouble[] motionAcceleration = { InterpolatedDouble.zero(), InterpolatedDouble.zero(), InterpolatedDouble.zero() };
	public InterpolatedDouble<ParticleVariables> motionDrag = InterpolatedDouble.zero();
	public InterpolatedDouble<ParticleVariables> rotationAcceleration = InterpolatedDouble.zero();
	public InterpolatedDouble<ParticleVariables> rotationDrag = InterpolatedDouble.zero();
	
	public ParcomMotionDynamic(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		
		if(element.has("linear_acceleration"))
		{
			JsonArray array = element.getAsJsonArray("linear_acceleration");
			
			if(array.size() >= 3)
			{
				this.motionAcceleration[0] = InterpolatedDouble.parse(array.get(0));
				this.motionAcceleration[1] = InterpolatedDouble.parse(array.get(1));
				this.motionAcceleration[2] = InterpolatedDouble.parse(array.get(2));
			}
		}
		
		if(element.has("linear_drag_coefficient")) this.motionDrag = InterpolatedDouble.parse(element.get("linear_drag_coefficient"));
		if(element.has("rotation_acceleration")) this.rotationAcceleration = InterpolatedDouble.parse(element.get("rotation_acceleration"));
		if(element.has("rotation_drag_coefficient")) this.rotationDrag = InterpolatedDouble.parse(element.get("rotation_drag_coefficient"));
	}
	
	@Override
	public void update(ParticleEmitter emitter, BedrockParticle particle)
	{
		ParticleVariables v = emitter.vars;
		
		particle.acceleration.x += (float) this.motionAcceleration[0].get(v);
		particle.acceleration.y += (float) this.motionAcceleration[1].get(v);
		particle.acceleration.z += (float) this.motionAcceleration[2].get(v);
		particle.drag = (float) this.motionDrag.get(v);
		
		particle.rotationAcceleration += (float) this.rotationAcceleration.get(v) / 20F;
		particle.rotationDrag = (float) this.rotationDrag.get(v);
	}
}