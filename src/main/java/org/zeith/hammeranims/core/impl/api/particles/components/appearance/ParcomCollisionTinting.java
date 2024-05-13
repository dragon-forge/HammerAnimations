package org.zeith.hammeranims.core.impl.api.particles.components.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomCollisionTinting
		extends ParcomAppearanceTinting
{
	public InterpolatedDouble<ParticleVariables> enabled = InterpolatedDouble.zero();
	
	public ParcomCollisionTinting(JsonElement elem)
	{
		super(elem);
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("enabled")) this.enabled = InterpolatedDouble.parse(element.get("enabled"));
	}
}
