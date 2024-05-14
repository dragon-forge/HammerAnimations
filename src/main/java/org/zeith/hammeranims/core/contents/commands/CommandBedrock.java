package org.zeith.hammeranims.core.contents.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandBedrock
		extends CommandTreeBase
{
	public CommandBedrock()
	{
		addSubcommand(new CommandParticle());
	}
	
	@Override
	public String getName()
	{
		return "bedrockmc";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "";
	}
}