package org.zeith.hammeranims.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.net.Network;

@Mod.EventBusSubscriber
public class HammerHooks
{
	@SubscribeEvent
	public static void playerStartTracking(PlayerEvent.StartTracking e)
	{
		if(e.getTarget() instanceof IAnimatedObject o && e.getEntity() instanceof ServerPlayer player)
			Network.sendTo(o.getAnimationSystem().createSyncPacket(), player);
	}
}