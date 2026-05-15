package org.zeith.hammeranims.core.contents.particles.components.lifetime;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

public class ParcomLifetimeLooping
		extends ParcomLifetime
{
	public LzFactory sleepTime = InterpolatedDouble.zero();
	
	public ParcomLifetimeLooping(JsonElement elem)
	{
		super(elem);
		if(!elem.isJsonObject()) return;
		JsonObject element = elem.getAsJsonObject();
		if(element.has("sleep_time")) this.sleepTime = InterpolatedDouble.parse(element.get("sleep_time"));
	}
	
	@Override
	public ParcomLifetimeInstance createInstance(LzVariableStore vars)
	{
		return new ParcomLifetimeLoopingInstance(
				activeTime.instantiate(vars),
				sleepTime.instantiate(vars)
		);
	}
	
	public static class ParcomLifetimeLoopingInstance
			extends ParcomLifetimeInstance
	{
		protected final LzExpression sleepTime;
		
		public ParcomLifetimeLoopingInstance(LzExpression activeTime, LzExpression sleepTime)
		{
			super(activeTime);
			this.sleepTime = sleepTime;
		}
		
		@Override
		public void update(ParticleEmitter emitter)
		{
			double active = this.activeTime.get();
			double sleep = this.sleepTime.get();
			double age = emitter.getAge();
			emitter.lifetime = (int) (active * 20);
			if(age >= active && emitter.playing) emitter.stop();
			if(age >= sleep && !emitter.playing) emitter.start();
		}
	}
}