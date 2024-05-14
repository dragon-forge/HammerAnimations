package org.zeith.hammeranims.core.contents.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
	
	public static LiteralArgumentBuilder<CommandSource> register()
	{
		return Commands.literal("bedrockmc")
				.then(CommandParticle.register());
	}
}