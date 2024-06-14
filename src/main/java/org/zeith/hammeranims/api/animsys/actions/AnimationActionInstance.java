package org.zeith.hammeranims.api.animsys.actions;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammerlib.util.mcf.Resources;

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
	public static AnimationActionInstance of(HolderLookup.Provider provider, CompoundTag tag)
	{
		AnimationAction action = HammerAnimationsApi.animationActions()
				.get(Resources.location(tag.getString("Id")));
		return action == null || action == DefaultsHA.EMPTY_ACTION
			   ? EMPTY
			   : action.deserializeInstance(provider, tag);
	}
	
	public CompoundTag getExtra()
	{
		CompoundTag i = InstanceHelpers.newNBTCompound();
		if(extra == null && !isEmpty()) extra = i;
		return i;
	}
	
	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider lookup)
	{
		CompoundTag tag = InstanceHelpers.newNBTCompound();
		if(extra != null) tag.put("Extra", extra);
		tag.putString("Id", action.getRegistryKey().toString());
		return tag;
	}
	
	@Override
	public void deserializeNBT(HolderLookup.Provider lookup, CompoundTag nbt)
	{
		if(nbt.contains("Extra", Tag.TAG_COMPOUND))
			extra = nbt.getCompound("Extra");
	}
}