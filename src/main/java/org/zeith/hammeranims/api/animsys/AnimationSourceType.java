package org.zeith.hammeranims.api.animsys;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class AnimationSourceType
		extends IForgeRegistryEntry.Impl<AnimationSourceType>
{
	public abstract AnimationSource readSource(NBTTagCompound tag);
}