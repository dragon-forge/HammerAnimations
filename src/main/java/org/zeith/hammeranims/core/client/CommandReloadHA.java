package org.zeith.hammeranims.core.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.*;
import net.minecraft.entity.player.PlayerEntity;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.proxy.ClientProxy;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammerlib.api.lighting.ColoredLightManager;
import org.zeith.hammerlib.net.*;

public class CommandReloadHA
{
	public static LiteralArgumentBuilder<CommandSource> command()
	{
		return Commands.literal(HammerAnimations.MOD_ID)
				.then(Commands.literal("reload")
						.executes(cs ->
						{
							Network.sendTo(cs.getSource().getPlayerOrException(), new IPacket()
							{
								@Override
								public void clientExecute(PacketContext ctx)
								{
									PlayerEntity player = ColoredLightManager.getClientPlayer();
									ClientProxy.performReload().thenRun(() ->
									{
										player.sendMessage(InstanceHelpers.componentText("Reload complete."), player.getUUID());
									});
								}
							});
							cs.getSource().sendSuccess(InstanceHelpers.componentText("Reloading models and animations."), true);
							return Command.SINGLE_SUCCESS;
						}));
	}
}