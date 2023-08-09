package org.zeith.hammeranims.api.animsys;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.api.HammerAnimationsApi;

public abstract class AnimationSourceType
{
	public abstract AnimationSource readSource(CompoundTag tag);
	
	public final ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.animationSources().getKey(this);
	}
}