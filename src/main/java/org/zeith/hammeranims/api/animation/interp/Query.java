package org.zeith.hammeranims.api.animation.interp;

import org.zeith.hammeranims.api.animation.scope.Variables;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;
import org.zeith.hammeranims.core.molang.MolangExpressionParser;

/**
 * This is an extensible class (this gets passed to animation layers)
 */
public class Query
		implements IVariableAccess
{
	public final Variables variables = new Variables();
	
	public double anim_time;
	
	public double anim_duration;
	public double anim_length;
	
	public Query()
	{
		MolangExpressionParser.initializeQuery(this);
	}
	
	public void setTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation anim)
	{
		this.anim_time = anim.config.timeFunction.getTime(system, sysTime, partialTicks, anim);
		this.anim_duration = this.anim_length = anim.getLengthSeconds();
		addCustomVariables(
				MolangExpressionParser.getMolangStorage(this),
				system,
				sysTime,
				partialTicks,
				anim
		);
	}
	
	protected void addCustomVariables(IVariableStorage env, AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation anim)
	{
//		env.store(new String[] {"query", "test_value"}, 1);
	}
	
	@Override
	public Variables getVariables()
	{
		return variables;
	}
}