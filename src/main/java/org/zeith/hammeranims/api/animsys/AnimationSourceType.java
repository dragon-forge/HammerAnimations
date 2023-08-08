package org.zeith.hammeranims.api.animsys;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.api.HammerAnimationsApi;

public abstract class AnimationSourceType
		extends IForgeRegistryEntry.Impl<AnimationSourceType>
{
	public abstract AnimationSource readSource(NBTTagCompound tag);
	
	public final ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.animationSources().getKey(this);
	}
}