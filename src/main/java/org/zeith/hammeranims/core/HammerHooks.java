package org.zeith.hammeranims.core;

import net.minecraft.entity.player.ServerPlayerEntity;
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
		if(e.getTarget() instanceof IAnimatedObject && e.getEntity() instanceof ServerPlayerEntity)
			Network.sendTo(((IAnimatedObject) e.getTarget()).getAnimationSystem().createSyncPacket(), (ServerPlayerEntity) e.getEntity());
	}
}