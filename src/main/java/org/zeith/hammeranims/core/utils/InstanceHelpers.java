package org.zeith.hammeranims.core.utils;

import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.zeith.hammerlib.util.mcf.Resources;

public class InstanceHelpers
{
	public static ITextComponent componentText(String text)
	{
		return new TextComponentString(text);
	}
	
	public static ITextComponent translatedText(String text)
	{
		return new TextComponentTranslation(text);
	}
	
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
	
	public static ResourceLocation tryParseLocation(String input)
	{
		return Resources.locationOrNull(input);
	}
}