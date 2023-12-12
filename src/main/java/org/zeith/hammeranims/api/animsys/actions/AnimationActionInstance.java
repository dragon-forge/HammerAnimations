package org.zeith.hammeranims.api.animsys.actions;

import net.minecraft.nbt.CompoundNBT;
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
	
	private CompoundNBT extra;
	
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
	public static AnimationActionInstance of(CompoundNBT tag)
	{
		AnimationAction action = HammerAnimationsApi.animationActions()
				.getValue(new ResourceLocation(tag.getString("Id")));
		return action == null || action == DefaultsHA.EMPTY_ACTION
			   ? EMPTY
			   : action.deserializeInstance(tag);
	}
	
	public CompoundNBT getExtra()
	{
		CompoundNBT i = InstanceHelpers.newNBTCompound();
		if(extra == null && !isEmpty()) extra = i;
		return i;
	}
	
	@Override
	public CompoundNBT serializeNBT()
	{
		CompoundNBT tag = InstanceHelpers.newNBTCompound();
		if(extra != null) tag.put("Extra", extra);
		tag.putString("Id", action.getRegistryKey().toString());
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt)
	{
		if(nbt.contains("Extra", Constants.NBT.TAG_COMPOUND))
			extra = nbt.getCompound("Extra");
	}
}