package org.zeith.hammeranims.api.animsys;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public abstract class AnimationSource
{
	public abstract CompoundTag writeSource();
	
	public abstract AnimationSourceType getType();
	
	public abstract IAnimatedObject get(Level world);
}