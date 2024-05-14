package org.zeith.hammeranims.core.contents.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
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