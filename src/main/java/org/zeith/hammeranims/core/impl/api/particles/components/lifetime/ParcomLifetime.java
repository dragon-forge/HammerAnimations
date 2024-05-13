package org.zeith.hammeranims.core.impl.api.particles.components.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IEmitterUpdate;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public abstract class ParcomLifetime
		implements IEmitterUpdate
{
	public static final InterpolatedDouble<ParticleVariables> DEFAULT_ACTIVE = InterpolatedDouble.constant(10);
	
	public InterpolatedDouble<ParticleVariables> activeTime = DEFAULT_ACTIVE;
	
	public ParcomLifetime(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has(this.getPropertyName())) this.activeTime = InterpolatedDouble.parse(element.get(this.getPropertyName()));
	}
	
	protected String getPropertyName()
	{
		return "active_time";
	}
	
	@Override
	public int getSortingIndex()
	{
		return -10;
	}
}