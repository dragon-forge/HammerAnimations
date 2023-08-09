package org.zeith.hammeranims.api.animsys.actions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;

import javax.annotation.Nonnull;

public abstract class AnimationAction
		extends IForgeRegistryEntry.Impl<AnimationAction>
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
	public AnimationActionInstance deserializeInstance(NBTTagCompound tag)
	{
		AnimationActionInstance inst = createInstance();
		inst.deserializeNBT(tag);
		return inst;
	}
	
	public abstract void execute(AnimationActionInstance instance, AnimationLayer layer);
	
	public World getAnimationWorld(AnimationLayer layer)
	{
		return layer.system.owner.getAnimatedObjectWorld();
	}
	
	public Vec3d getAnimationPos(AnimationLayer layer)
	{
		return layer.system.owner.getAnimatedObjectPosition();
	}
	
	public final ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.animationActions().getKey(this);
	}
}