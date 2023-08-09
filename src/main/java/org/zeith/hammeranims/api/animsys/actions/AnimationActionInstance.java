package org.zeith.hammeranims.api.animsys.actions;

import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import javax.annotation.Nonnull;

public class AnimationActionInstance
		implements ICompoundSerializable
{
	public static final AnimationActionInstance EMPTY = new AnimationActionInstance(DefaultsHA.EMPTY_ACTION);
	
	private CompoundTag extra;
	
	public final AnimationAction action;
	
	public AnimationActionInstance(AnimationAction action)
	{
		this.action = action;
	}
	
	public void execute(AnimationLayer layer)
	{
		action.execute(this, layer);
	}
	
	public final boolean isEmpty()
	{
		return action == DefaultsHA.EMPTY_ACTION;
	}
	
	@Nonnull
	public static AnimationActionInstance of(CompoundTag tag)
	{
		AnimationAction action = HammerAnimationsApi.animationActions()
				.getValue(new ResourceLocation(tag.getString("Id")));
		return action == null || action == DefaultsHA.EMPTY_ACTION
			   ? EMPTY
			   : action.deserializeInstance(tag);
	}
	
	public CompoundTag getExtra()
	{
		CompoundTag i = InstanceHelpers.newNBTCompound();
		if(extra == null && !isEmpty()) extra = i;
		return i;
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag tag = InstanceHelpers.newNBTCompound();
		if(extra != null) tag.put("Extra", extra);
		tag.putString("Id", action.getRegistryKey().toString());
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		if(nbt.contains("Extra", Tag.TAG_COMPOUND))
			extra = nbt.getCompound("Extra");
	}
}