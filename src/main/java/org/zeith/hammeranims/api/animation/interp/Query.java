package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;

import java.util.function.BiConsumer;

/**
 * This is an extensible class (this gets passed to animation layers)
 */
public class Query
		extends BaseQuery
{
	public double anim_time;
	
	public double anim_duration;
	public double anim_length;
	
	public Query()
	{
	}
	
	public void setTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation anim)
	{
		this.anim_time = anim.config.timeFunction.getTime(system, sysTime, partialTicks, anim);
		this.anim_duration = this.anim_length = anim.getLengthSeconds();
	}
	
	@Override
	protected void registerVariables(BiConsumer<String, ReadonlyLzVarOp> reg)
	{
		super.registerVariables(reg);
		reg.accept("query.anim_duration", () -> anim_duration);
		reg.accept("query.anim_time", () -> anim_time);
		reg.accept("query.anim_length", () -> anim_length);
	}
}