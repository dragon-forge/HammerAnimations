package org.zeith.hammeranims.api.animsys.actions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;

import javax.annotation.Nonnull;

public abstract class AnimationAction
{
	@Nonnull
	protected AnimationActionInstance createInstance()
	{
		return new AnimationActionInstance(this);
	}
	
	public AnimationActionInstance defaultInstance()
	{
		return createInstance();
	}
	
	@Nonnull
	public AnimationActionInstance deserializeInstance(CompoundTag tag)
	{
		AnimationActionInstance inst = createInstance();
		inst.deserializeNBT(tag);
		return inst;
	}
	
	public abstract void execute(AnimationActionInstance instance, AnimationLayer layer);
	
	public Level getAnimationWorld(AnimationLayer layer)
	{
		return layer.system.owner.getAnimatedObjectWorld();
	}
	
	public Vec3 getAnimationPos(AnimationLayer layer)
	{
		return layer.system.owner.getAnimatedObjectPosition();
	}
	
	public final ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.animationActions().getKey(this);
	}
}