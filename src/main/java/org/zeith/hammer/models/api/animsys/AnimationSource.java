package org.zeith.hammer.models.api.animsys;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class AnimationSource
{
	public abstract NBTTagCompound writeSource();
	
	public abstract AnimationSourceType getType();
	
	public abstract IAnimatedObject get(World world);
}