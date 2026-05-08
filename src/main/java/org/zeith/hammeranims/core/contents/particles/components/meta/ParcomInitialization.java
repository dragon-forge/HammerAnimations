package org.zeith.hammeranims.core.contents.particles.components.meta;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.components.itf.*;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomInitialization
		implements IParticleComponent
{
	public LzFactory creation = InterpolatedDouble.zero();
	public LzFactory update = InterpolatedDouble.zero();
	
	public ParcomInitialization(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("creation_expression")) this.creation = InterpolatedDouble.parse(element.get("creation_expression"));
		if(element.has("per_update_expression")) this.update = InterpolatedDouble.parse(element.get("per_update_expression"));
	}
	
	@Override
	public IParticleCompInstance createInstance(LzVariableStore vars)
	{
		return new ParcomInitializationInstance(
				creation.instantiate(vars),
				update.instantiate(vars)
		);
	}
	
	public static class ParcomInitializationInstance
			implements IEmitterInitialize, IEmitterUpdate
	{
		public final LzExpression creation;
		public final LzExpression update;
		
		public ParcomInitializationInstance(LzExpression creation, LzExpression update)
		{
			this.creation = creation;
			this.update = update;
		}
		
		@Override
		public void apply(ParticleEmitter emitter)
		{
			emitter.initialValues.clear();
			this.creation.get();
		}
		
		@Override
		public void update(ParticleEmitter emitter)
		{
			this.update.get();
			emitter.replaceVariables();
		}
	}
}