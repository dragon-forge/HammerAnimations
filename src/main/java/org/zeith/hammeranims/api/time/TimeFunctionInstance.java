package org.zeith.hammeranims.api.time;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TimeFunctionInstance
		implements ICompoundSerializable
{
	public static final TimeFunctionInstance EMPTY = new TimeFunctionInstance(DefaultsHA.LINEAR_TIME);
	
	private NBTTagCompound extra;
	protected final TimeFunction function;
	
	public TimeFunctionInstance(TimeFunction function)
	{
		this.function = function;
	}
	
	public final boolean isEmpty()
	{
		return function == DefaultsHA.LINEAR_TIME;
	}
	
	public double getTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation animation)
	{
		return function.getTime(system, sysTime, partialTicks, animation, this);
	}
	
	public double getLengthSeconds(ActiveAnimation animation)
	{
		return function.getLengthSeconds(animation, this);
	}
	
	@Nonnull
	public static TimeFunctionInstance of(NBTTagCompound tag)
	{
		TimeFunction action = HammerAnimationsApi.timeFunctions()
				.getValue(new ResourceLocation(tag.getString("Id")));
		return action == null || action == DefaultsHA.LINEAR_TIME
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
		tag.setString("Id", function.getRegistryKey().toString());
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey("Extra", Constants.NBT.TAG_COMPOUND))
			extra = nbt.getCompoundTag("Extra");
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		TimeFunctionInstance that = (TimeFunctionInstance) o;
		return Objects.equals(extra, that.extra) && Objects.equals(function, that.function);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(extra, function);
	}
}