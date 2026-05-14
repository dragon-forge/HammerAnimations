package org.zeith.hammeranims.api.animation.data;

import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.api.animation.AnimationLocation;
import org.zeith.hammeranims.api.animation.interp.*;
import shaded.json.JSONObject;

public class BoneAnimation
{
	public final BaseInterpolation rotation;
	public final BaseInterpolation position;
	public final BaseInterpolation scale;
	
	public BoneAnimation(@Nullable BaseInterpolation rotation, @Nullable BaseInterpolation position, @Nullable BaseInterpolation scale)
	{
		this.rotation = rotation != null ? rotation : DoubleInterpolation.ZERO;
		this.position = position != null ? position : DoubleInterpolation.ZERO;
		this.scale = scale != null ? scale : DoubleInterpolation.ONE;
	}
	
	public static BoneAnimation parse(AnimationLocation anim, JSONObject bone)
	{
		if(bone.has("relative_to"))
			anim.warn(
					"Warning: Detected unsupported feature: relative_to (" + bone.opt("relative_to") + ")! Ignoring.");
		
		BaseInterpolation rotation = BaseInterpolation.parse(bone.opt("rotation"));
		BaseInterpolation position = BaseInterpolation.parse(bone.opt("position"));
		BaseInterpolation scale = BaseInterpolation.parse(bone.opt("scale"));
		if(rotation == null && position == null && scale == null) return null;
		return new BoneAnimation(rotation, position, scale);
	}
	
	@Override
	public String toString()
	{
		return "BoneAnimation{" +
				"rotation=" + rotation +
				", position=" + position +
				", scale=" + scale +
				'}';
	}
}