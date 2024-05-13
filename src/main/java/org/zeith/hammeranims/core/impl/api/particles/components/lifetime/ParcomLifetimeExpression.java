package org.zeith.hammeranims.core.impl.api.particles.components.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomLifetimeExpression
		extends ParcomLifetime
{
	public InterpolatedDouble<ParticleVariables> expiration = InterpolatedDouble.zero();
	
	public ParcomLifetimeExpression(JsonElement elem)
	{
		super(elem);
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("expiration_expression")) this.expiration = InterpolatedDouble.parse(element.get("expiration_expression"));
	}
	
	@Override
	protected String getPropertyName()
	{
		return "activation_expression";
	}
	
	@Override
	public void update(ParticleEmitter emitter)
	{
		if(this.activeTime.get(emitter.vars) > 0)
		{
			emitter.start();
		}
		
		if(this.expiration.get(emitter.vars) > 0)
		{
			emitter.stop();
		}
	}
}
