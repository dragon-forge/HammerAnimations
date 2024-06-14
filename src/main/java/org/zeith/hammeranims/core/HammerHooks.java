package org.zeith.hammeranims.core;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.net.Network;

@EventBusSubscriber
public class HammerHooks
{
	@SubscribeEvent
	public static void playerStartTracking(PlayerEvent.StartTracking e)
	{
		if(e.getTarget() instanceof IAnimatedObject o && e.getEntity() instanceof ServerPlayer player)
			Network.sendTo(o.getAnimationSystem().createSyncPacket(), player);
	}
}