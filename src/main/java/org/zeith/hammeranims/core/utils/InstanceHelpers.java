package org.zeith.hammeranims.core.utils;

import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

public class InstanceHelpers
{
	public static ITextComponent componentText(String text)
	{
		return new StringTextComponent(text);
	}
	
	public static ITextComponent componentTranslate(String text, Object... args)
	{
		return new TranslationTextComponent(text, args);
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
	
	public static ResourceLocation tryParseLocation(String input)
	{
		return new ResourceLocation(input);
	}
}