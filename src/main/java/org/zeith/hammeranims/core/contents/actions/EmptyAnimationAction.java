package org.zeith.hammeranims.core.contents.actions;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animsys.actions.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;

public class EmptyAnimationAction
		extends AnimationAction
{
	@NotNull
	@Override
	public AnimationActionInstance deserializeInstance(HolderLookup.Provider lookup, CompoundTag tag)
	{
		return createInstance();
	}
	
	@Override
	public void execute(AnimationActionInstance instance, AnimationLayer layer)
	{
		// NO-OP
	}
}