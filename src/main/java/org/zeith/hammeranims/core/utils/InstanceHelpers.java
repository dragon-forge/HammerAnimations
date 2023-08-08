package org.zeith.hammeranims.core.utils;

import net.minecraft.nbt.*;

public class InstanceHelpers
{
	public static NBTTagCompound newNBTCompound()
	{
		return new NBTTagCompound();
	}
	
	public static NBTTagList newNBTList()
	{
		return new NBTTagList();
	}
	
	public static NBTTagString newNBTString(String s)
	{
		return new NBTTagString(s);
	}
}