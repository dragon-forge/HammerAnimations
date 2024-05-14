package org.zeith.hammeranims.core.contents.particles.components.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IEmitterInitialize;
import org.zeith.hammeranims.api.particles.components.itf.IEmitterUpdate;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomInitialization
		implements IEmitterInitialize, IEmitterUpdate
{
	public InterpolatedDouble<ParticleVariables> creation = InterpolatedDouble.zero();
	public InterpolatedDouble<ParticleVariables> update = InterpolatedDouble.zero();
	
	public ParcomInitialization(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("creation_expression")) this.creation = InterpolatedDouble.parse(element.get("creation_expression"));
		if(element.has("per_update_expression")) this.update = InterpolatedDouble.parse(element.get("per_update_expression"));
	}
	
	@Override
	public void apply(ParticleEmitter emitter)
	{
		emitter.initialValues.clear();
		this.creation.get(emitter.vars);
	}
	
	@Override
	public void update(ParticleEmitter emitter)
	{
		this.update.get(emitter.vars);
		emitter.replaceVariables();
	}
}