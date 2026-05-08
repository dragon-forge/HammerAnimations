package org.zeith.hammeranims.core.contents.particles.components.motion;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.components.itf.IParticleInitialize;
import org.zeith.hammeranims.api.particles.emitter.*;

public class ParcomInitialSpin
		implements IParticleComponent
{
	public LzFactory rotation = InterpolatedDouble.zero();
	public LzFactory rate = InterpolatedDouble.zero();
	
	public ParcomInitialSpin(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("rotation")) this.rotation = InterpolatedDouble.parse(element.get("rotation"));
		if(element.has("rotation_rate")) this.rate = InterpolatedDouble.parse(element.get("rotation_rate"));
	}
	
	@Override
	public IParticleCompInstance createInstance(LzVariableStore vars)
	{
		return new ParcomInitialSpinInstance(
				rotation.instantiate(vars),
				rate.instantiate(vars)
		);
	}
	
	public static class ParcomInitialSpinInstance
			implements IParticleInitialize
	{
		public final LzExpression rotation;
		public final LzExpression rate;
		
		public ParcomInitialSpinInstance(LzExpression rotation, LzExpression rate)
		{
			this.rotation = rotation;
			this.rate = rate;
		}
		
		@Override
		public void apply(ParticleEmitter emitter, BedrockParticle particle)
		{
			particle.initialRotation = (float) this.rotation.get();
			particle.rotationVelocity = (float) this.rate.get() / 20;
		}
	}
}
