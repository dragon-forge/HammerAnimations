package org.zeith.hammeranims.core.contents.particles.components.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomCollisionTinting
		extends ParcomAppearanceTinting
{
	public LzFactory enabled = InterpolatedDouble.zero();
	
	public ParcomCollisionTinting(JsonElement elem)
	{
		super(elem);
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("enabled")) this.enabled = InterpolatedDouble.parse(element.get("enabled"));
	}
	
	@Override
	public ParcomCollisionTintingInstance createInstance(LzVariableStore vars)
	{
		return new ParcomCollisionTintingInstance(
				color.newInstance(vars),
				enabled.instantiate(vars)
		);
	}
	
	public static class ParcomCollisionTintingInstance
			extends ParcomAppearanceTintingInstance
	{
		public final LzExpression enabled;
		
		public ParcomCollisionTintingInstance(TintInstance color, LzExpression enabled)
		{
			super(color);
			this.enabled = enabled;
		}
	}
}