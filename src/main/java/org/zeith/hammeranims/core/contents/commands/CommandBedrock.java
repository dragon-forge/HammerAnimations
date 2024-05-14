package org.zeith.hammeranims.core.contents.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
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
	
	public static LiteralArgumentBuilder<CommandSourceStack> register()
	{
		return Commands.literal("bedrockmc")
				.then(CommandParticle.register());
	}
}