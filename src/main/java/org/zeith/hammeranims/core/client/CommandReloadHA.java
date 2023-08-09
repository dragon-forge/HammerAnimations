package org.zeith.hammeranims.core.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.proxy.ClientProxy;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

public class CommandReloadHA
{
	public static LiteralArgumentBuilder<CommandSourceStack> command()
	{
		return Commands.literal(HammerAnimations.MOD_ID)
				.then(Commands.literal("reload")
						.executes(cs ->
						{
							cs.getSource().sendSuccess(InstanceHelpers.componentText("Reloading models and animations."), true);
							ClientProxy.performReload();
							return Command.SINGLE_SUCCESS;
						}));
	}
}