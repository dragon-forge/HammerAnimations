package org.zeith.hammeranims.api.animsys.layer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;

public class ActiveAnimation
		implements INBTSerializable<NBTTagCompound>
{
	public double startTime;
	
	// Properties
	public ConfiguredAnimation config;
	
	public ActiveAnimation(NBTTagCompound tag)
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
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = config.serializeNBT();
		tag.setDouble("StartTime", startTime);
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound tag)
	{
		config = new ConfiguredAnimation(tag);
		this.startTime = tag.getDouble("StartTime");
	}
}