package org.zeith.hammeranims.core.contents.particles.components.motion;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.itf.IParticleUpdate;
import org.zeith.hammeranims.api.particles.emitter.*;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomMotionDynamic
		implements IParticleComponent
{
	public LzFactory[] motionAcceleration = {InterpolatedDouble.zero(), InterpolatedDouble.zero(), InterpolatedDouble.zero()};
	public LzFactory motionDrag = InterpolatedDouble.zero();
	public LzFactory rotationAcceleration = InterpolatedDouble.zero();
	public LzFactory rotationDrag = InterpolatedDouble.zero();
	
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
	public ParcomMotionDynamicInstance createInstance(LzVariableStore vars)
	{
		return new ParcomMotionDynamicInstance(this, vars);
	}
	
	public static class ParcomMotionDynamicInstance
			implements IParticleUpdate
	{
		public LzExpression[] motionAcceleration;
		public LzExpression motionDrag;
		public LzExpression rotationAcceleration;
		public LzExpression rotationDrag;
		
		public ParcomMotionDynamicInstance(ParcomMotionDynamic o, LzVariableStore vars)
		{
			this.motionAcceleration = LzFactory.instantiate(vars, o.motionAcceleration);
			this.motionDrag = o.motionDrag.instantiate(vars);
			this.rotationAcceleration = o.rotationAcceleration.instantiate(vars);
			this.rotationDrag = o.rotationDrag.instantiate(vars);
		}
		
		@Override
		public void update(ParticleEmitter emitter, BedrockParticle particle)
		{
			ParticleVariables v = emitter.vars;
			
			particle.acceleration.x += (float) this.motionAcceleration[0].get();
			particle.acceleration.y += (float) this.motionAcceleration[1].get();
			particle.acceleration.z += (float) this.motionAcceleration[2].get();
			particle.drag = (float) this.motionDrag.get();
			
			particle.rotationAcceleration += (float) this.rotationAcceleration.get() / 20F;
			particle.rotationDrag = (float) this.rotationDrag.get();
		}
	}
}