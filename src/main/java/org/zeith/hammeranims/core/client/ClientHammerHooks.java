package org.zeith.hammeranims.core.client;

import com.zeitheron.hammercore.utils.java.tuples.*;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animsys.*;

import java.util.*;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientHammerHooks
{
	private static final List<Tuple3.Mutable3<AnimationSource, NBTTagCompound, Integer>> QUEUED_SYSTEMS = new ArrayList<>();
	
	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END) return;
		
		World w = Minecraft.getMinecraft().world;
		if(w == null)
		{
			QUEUED_SYSTEMS.clear();
			return;
		}
		
		QUEUED_SYSTEMS.removeIf(src ->
		{
			int ticksAwaiting = src.c();
			src.setC(ticksAwaiting - 1);
			if(ticksAwaiting <= 0)
			{
				HammerAnimations.LOG.warn("Animation update for {} has timed out.", src.a());
				return true;
			}
			return applyAnimationSystem(w, src.a(), src.b());
		});
	}
	
	/**
	 * Apply animation system client-side for a given animation address with a given timeout.
	 * This has a
	 */
	public static void applySystem(AnimationSource source, NBTTagCompound tag, int timeout)
	{
		if(source == null || tag == null)
		{
			HammerAnimations.LOG.error("Completely ignored animation sync with {} or {} being null.", source, tag);
			return;
		}
		
		if(!applyAnimationSystem(Minecraft.getMinecraft().world, source, tag))
			QUEUED_SYSTEMS.add(Tuples.mutable(source, tag, timeout));
	}
	
	private static boolean applyAnimationSystem(World world, AnimationSource source, NBTTagCompound tag)
	{
		IAnimatedObject obj = source.get(world);
		if(obj == null) return false;
		AnimationSystem sys = obj.getAnimationSystem();
		if(sys == null) return true;
		sys.deserializeNBT(tag);
		return true;
	}
}
