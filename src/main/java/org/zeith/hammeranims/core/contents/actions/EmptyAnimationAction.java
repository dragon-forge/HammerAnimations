package org.zeith.hammeranims.core.contents.actions;

import net.minecraft.nbt.NBTTagCompound;
import org.zeith.hammeranims.api.animsys.actions.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;

import javax.annotation.Nonnull;

public class EmptyAnimationAction
		extends AnimationAction
{
	@Nonnull
	@Override
	public AnimationActionInstance deserializeInstance(NBTTagCompound tag)
	{
		return createInstance();
	}
	
	@Override
	public void execute(AnimationActionInstance instance, AnimationLayer layer)
	{
		// NO-OP
	}
}