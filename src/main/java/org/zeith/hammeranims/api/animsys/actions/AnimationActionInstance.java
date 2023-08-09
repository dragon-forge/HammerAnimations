package org.zeith.hammeranims.api.animsys.actions;

import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
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
	
	private NBTTagCompound extra;
	
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
	public static AnimationActionInstance of(NBTTagCompound tag)
	{
		AnimationAction action = HammerAnimationsApi.animationActions()
				.getValue(new ResourceLocation(tag.getString("Id")));
		return action == null || action == DefaultsHA.EMPTY_ACTION
			   ? EMPTY
			   : action.deserializeInstance(tag);
	}
	
	public NBTTagCompound getExtra()
	{
		NBTTagCompound i = InstanceHelpers.newNBTCompound();
		if(extra == null && !isEmpty()) extra = i;
		return i;
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = InstanceHelpers.newNBTCompound();
		if(extra != null) tag.setTag("Extra", extra);
		tag.setString("Id", action.getRegistryKey().toString());
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey("Extra", Constants.NBT.TAG_COMPOUND))
			extra = nbt.getCompoundTag("Extra");
	}
}