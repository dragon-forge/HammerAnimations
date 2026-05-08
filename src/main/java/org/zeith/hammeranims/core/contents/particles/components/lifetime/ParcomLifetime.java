package org.zeith.hammeranims.core.contents.particles.components.lifetime;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.components.itf.IEmitterUpdate;

public abstract class ParcomLifetime
		implements IParticleComponent
{
	public static final LzFactory DEFAULT_ACTIVE = InterpolatedDouble.constant(10);
	
	public LzFactory activeTime = DEFAULT_ACTIVE;
	
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
	
	@Override
	public abstract ParcomLifetimeInstance createInstance(LzVariableStore vars);
	
	public static abstract class ParcomLifetimeInstance
			implements IEmitterUpdate
	{
		public final LzExpression activeTime;
		
		public ParcomLifetimeInstance(LzExpression activeTime)
		{
			this.activeTime = activeTime;
		}
	}
}