package org.zeith.hammeranims.api.animation.interp;

import shaded.json.JSONObject;
import org.teavm.jso.JSObject;
import org.teavm.jso.json.JSON;
import org.zeith.hammeranims.api.animation.Animation;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;

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
		Animation a = anim.config.animation;
		this.anim_duration = this.anim_length = a != null && a.getData() != null ? a.getData().getLengthSeconds() : 0;
	}
	
	@Override
	public JSObject toJS()
	{
		return JSON.parse(new JSONObject()
				.put("anim_time", anim_time)
				.put("anim_duration", anim_duration)
				.put("anim_length", anim_length)
				.toString());
	}
}