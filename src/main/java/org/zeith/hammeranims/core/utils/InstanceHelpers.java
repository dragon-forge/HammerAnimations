package org.zeith.hammeranims.core.utils;

import net.minecraft.nbt.*;
import net.minecraft.util.text.*;

public class InstanceHelpers
{
	public static ITextComponent componentText(String text)
	{
		return new StringTextComponent(text);
	}
	
	public static CompoundNBT newNBTCompound()
	{
		return new CompoundNBT();
	}
	
	public static ListNBT newNBTList()
	{
		return new ListNBT();
	}
	
	public static StringNBT newNBTString(String s)
	{
		return StringNBT.valueOf(s);
	}
}