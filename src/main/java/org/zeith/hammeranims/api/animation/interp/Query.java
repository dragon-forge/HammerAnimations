package org.zeith.hammeranims.api.animation.interp;

import lombok.var;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;

import java.util.function.BiConsumer;

/**
 * This is an extensible class (this gets passed to animation layers)
 */
public class Query
	implements IVariableAccess
{
	public double anim_time;
	
	public double anim_duration;
	public double anim_length;
	
	public void setTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation anim)
	{
		this.anim_time = anim.config.timeFunction.getTime(system, sysTime, partialTicks, anim);
		var a = anim.config.animation;
		this.anim_duration = this.anim_length = anim.getLengthSeconds();
	}
	
	@Override
	public void putObjects(BiConsumer<String, Object> storage)
	{
		storage.accept("q", this);
		storage.accept("query", this);
	}
}