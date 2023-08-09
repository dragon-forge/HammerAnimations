package org.zeith.hammeranims.api.animsys;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;

public abstract class AnimationAction
{
	public abstract void execute(AnimationLayer layer);
	
	public Level getAnimationWorld(AnimationLayer layer)
	{
		return layer.system.owner.getAnimatedObjectWorld();
	}
	
	public final ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.animationActions().getKey(this);
	}
}