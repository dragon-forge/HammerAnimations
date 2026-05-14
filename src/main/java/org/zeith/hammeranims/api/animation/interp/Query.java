package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import org.zeith.hammeranims.api.animation.Animation;
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
	public double anim_length;
	
	protected float partialTicks;
	
	public void setTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation anim)
	{
		this.anim_time = anim.config.timeFunction.getTime(system, sysTime, partialTicks, anim);
		Animation a = anim.config.animation;
		this.anim_length = a != null && a.getData() != null ? a.getData().getLengthSeconds() : 0;
		this.partialTicks = partialTicks;
	}
	
	@Override
	protected void registerVariables(BiConsumer<String, ReadonlyLzVarOp> reg)
	{
		super.registerVariables(reg);
		
		ReadonlyLzVarOp animLength = () -> anim_length;
		reg.accept("query.anim_duration", animLength);
		reg.accept("query.anim_length", animLength);
		reg.accept("query.anim_time", () -> anim_time);
		reg.accept("query.frame_alpha", () -> partialTicks);
	}
}