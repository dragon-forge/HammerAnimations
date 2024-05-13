package org.zeith.hammeranims.core.utils;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
	
	public static ResourceLocation tryParseLocation(String input)
	{
		return ResourceLocation.tryParse(input);
	}
}