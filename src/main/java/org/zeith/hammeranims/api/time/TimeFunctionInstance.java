package org.zeith.hammeranims.api.time;

import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.*;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TimeFunctionInstance
		implements ICompoundSerializable
{
	public static final TimeFunctionInstance EMPTY = new TimeFunctionInstance(DefaultsHA.LINEAR_TIME);
	
	private CompoundTag extra;
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
	public static TimeFunctionInstance of(CompoundTag tag)
	{
		TimeFunction action = HammerAnimationsApi.timeFunctions()
				.getValue(new ResourceLocation(tag.getString("Id")));
		return action == null || action == DefaultsHA.LINEAR_TIME
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
		var tag = InstanceHelpers.newNBTCompound();
		if(extra != null) tag.put("Extra", extra);
		tag.putString("Id", function.getRegistryKey().toString());
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		if(nbt.contains("Extra", Tag.TAG_COMPOUND))
			extra = nbt.getCompound("Extra");
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