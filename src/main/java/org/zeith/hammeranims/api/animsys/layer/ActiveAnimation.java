package org.zeith.hammeranims.api.animsys.layer;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;

public class ActiveAnimation
		implements INBTSerializable<CompoundTag>
{
	public double startTime;
	
	// Properties
	public ConfiguredAnimation config;
	
	public ActiveAnimation(CompoundTag tag)
	{
		deserializeNBT(tag);
	}
	
	public ActiveAnimation(ConfiguredAnimation config)
	{
		this.config = config;
	}
	
	public boolean isDone(double sysTime)
	{
		IAnimationData data;
		return config.animation == null
				|| (config.loopMode == LoopMode.ONCE && (
				(data = config.animation.getData()) == null
						|| (sysTime - startTime) >= data.getLength().toMillis() / 50D
		));
	}
	
	public AnimationLocation getLocation()
	{
		return config.animation != null ? config.animation.getLocation() : null;
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		var tag = config.serializeNBT();
		tag.putDouble("StartTime", startTime);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag)
	{
		config = new ConfiguredAnimation(tag);
		this.startTime = tag.getDouble("StartTime");
	}
}