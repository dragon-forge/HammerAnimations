package org.zeith.hammeranims.core.contents.particles.components.lifetime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomLifetimeExpression
		extends ParcomLifetime
{
	public LzFactory expiration = InterpolatedDouble.zero();
	
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
	public ParcomLifetimeInstance createInstance(LzVariableStore vars)
	{
		return new ParcomLifetimeExpressionInstance(
				activeTime.instantiate(vars),
				expiration.instantiate(vars)
		);
	}
	
	public static class ParcomLifetimeExpressionInstance
			extends ParcomLifetimeInstance
	{
		protected final LzExpression expiration;
		
		public ParcomLifetimeExpressionInstance(LzExpression activeTime, LzExpression expiration)
		{
			super(activeTime);
			this.expiration = expiration;
		}
		
		@Override
		public void update(ParticleEmitter emitter)
		{
			if(this.activeTime.get() > 0)
			{
				emitter.start();
			}
			
			if(this.expiration.get() > 0)
			{
				emitter.stop();
			}
		}
	}
}
