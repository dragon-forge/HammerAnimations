package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;

/**
 * This is an extensible class (this gets passed to animation layers)
 */
public class Query
		extends BaseQuery
{
	public double anim_time;
	public double anim_length;
	public float partialTicks;
	
	public Query()
	{
	}
	
	public void setWorld(World world) {}
	
	public void setTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation anim)
	{
		this.anim_time = anim.config.timeFunction.getTime(system, sysTime, partialTicks, anim);
		this.anim_length = anim.getLengthSeconds();
		this.partialTicks = partialTicks;
	}
	
	@Override
	protected void registerVariables(IVariableRegistrar reg)
	{
		super.registerVariables(reg);
		
		ReadonlyLzVarOp animLength = () -> anim_length;
		reg.registerR("query.anim_duration", animLength);
		reg.registerR("query.anim_length", animLength);
		reg.registerR("query.anim_time", () -> anim_time);
		reg.registerR("query.frame_alpha", () -> partialTicks);
	}
}