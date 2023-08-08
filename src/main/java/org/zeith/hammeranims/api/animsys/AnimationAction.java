package org.zeith.hammeranims.api.animsys;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;

public abstract class AnimationAction
		extends IForgeRegistryEntry.Impl<AnimationAction>
{
	public abstract void execute(AnimationLayer layer);
	
	public World getAnimationWorld(AnimationLayer layer)
	{
		return layer.system.owner.getAnimatedObjectWorld();
	}
	
	public final ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.animationActions().getKey(this);
	}
}