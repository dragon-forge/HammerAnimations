package org.zeith.hammeranims.core.client;

import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.proxy.ClientProxy;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import javax.annotation.Nullable;
import java.util.*;

public class CommandReloadHA
		extends CommandBase
{
	@Override
	public String getName()
	{
		return HammerAnimations.MOD_ID;
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "Client-side commands for hammer animations";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException
	{
		if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("reload"))
			{
				sender.sendMessage(InstanceHelpers.componentText("Reloading models and animations."));
				ClientProxy.performReload();
			}
		}
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if(args.length == 1)
			return getListOfStringsMatchingLastWord(args, "reload");
		
		return Collections.emptyList();
	}
}