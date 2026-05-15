package org.zeith.hammeranims.core.contents.particles.components.lifetime;

import com.google.gson.JsonElement;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.LzExpression;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomLifetimeOnce
		extends ParcomLifetime
{
	public ParcomLifetimeOnce(JsonElement elem)
	{
		super(elem);
	}
	
	@Override
	public ParcomLifetimeInstance createInstance(LzVariableStore vars)
	{
		return new ParcomLifetimeOnceInstance(activeTime.instantiate(vars));
	}
	
	public static class ParcomLifetimeOnceInstance
			extends ParcomLifetimeInstance
	{
		public ParcomLifetimeOnceInstance(LzExpression activeTime)
		{
			super(activeTime);
		}
		
		@Override
		public void update(ParticleEmitter emitter)
		{
			double time = this.activeTime.get();
			
			emitter.lifetime = (int) (time * 20);
			
			if(emitter.getAge() >= time)
			{
				emitter.stop();
			}
		}
	}
}