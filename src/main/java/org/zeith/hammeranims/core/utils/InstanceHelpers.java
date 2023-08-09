package org.zeith.hammeranims.core.utils;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;

public class InstanceHelpers
{
	public static Component componentText(String text)
	{
		return Component.literal(text);
	}
	
	public static CompoundTag newNBTCompound()
	{
		return new CompoundTag();
	}
	
	public static ListTag newNBTList()
	{
		return new ListTag();
	}
	
	public static StringTag newNBTString(String s)
	{
		return StringTag.valueOf(s);
	}
}