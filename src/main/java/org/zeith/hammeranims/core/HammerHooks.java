package org.zeith.hammeranims.core;

import com.zeitheron.hammercore.net.HCNet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;

@Mod.EventBusSubscriber
public class HammerHooks
{
	@SubscribeEvent
	public static void playerStartTracking(PlayerEvent.StartTracking e)
	{
		if(e.getTarget() instanceof IAnimatedObject && e.getEntityPlayer() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP) e.getEntityPlayer();
			IAnimatedObject o = (IAnimatedObject) e.getTarget();
			HCNet.INSTANCE.sendTo(o.getAnimationSystem().createSyncPacket(), player);
		}
	}
}