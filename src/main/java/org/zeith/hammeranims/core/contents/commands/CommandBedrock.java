package org.zeith.hammeranims.core.contents.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.zeith.hammeranims.core.client.CommandReloadHA;

@Mod.EventBusSubscriber
public class CommandBedrock
{
	@SubscribeEvent
	public static void registerCmd(RegisterCommandsEvent e)
	{
		e.getDispatcher().register(register());
		e.getDispatcher().register(CommandReloadHA.command());
	}
	
	@SubscribeEvent
	public static void serverShutdown(FMLServerStoppingEvent e)
	{
		CommandParticle.PLAYER_CUSTOM_MAP.clear();
	}
	
	public static LiteralArgumentBuilder<CommandSource> register()
	{
		return Commands.literal("bedrockmc")
				.then(CommandParticle.register());
	}
}