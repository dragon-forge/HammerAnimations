package org.zeith.hammeranims.core.contents.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@EventBusSubscriber
public class CommandBedrock
{
	@SubscribeEvent
	public static void registerCmd(RegisterCommandsEvent e)
	{
		e.getDispatcher().register(register());
	}
	
	@SubscribeEvent
	public static void serverShutdown(ServerStoppingEvent e)
	{
		CommandParticle.PLAYER_CUSTOM_MAP.clear();
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> register()
	{
		return Commands.literal("bedrockmc")
				.then(CommandParticle.register());
	}
}