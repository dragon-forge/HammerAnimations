package org.zeith.hammeranims.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.tuples.Tuple3;
import org.zeith.hammerlib.util.java.tuples.Tuples;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class ClientHammerHooks
{
	private static final List<Tuple3.Mutable3<IObjectSource<?>, CompoundTag, Integer>> QUEUED_SYSTEMS = new ArrayList<>();
	
	@SubscribeEvent
	public static void clientTick(ClientTickEvent.Pre e)
	{
		var w = Minecraft.getInstance().level;
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
	public static void applySystem(IObjectSource<?> source, CompoundTag tag, int timeout)
	{
		if(source == null || tag == null)
		{
			HammerAnimations.LOG.error("Completely ignored animation sync with {} or {} being null.", source, tag);
			return;
		}
		
		if(!applyAnimationSystem(Minecraft.getInstance().level, source, tag))
			QUEUED_SYSTEMS.add(Tuples.mutable(source, tag, timeout));
	}
	
	private static boolean applyAnimationSystem(Level world, IObjectSource<?> source, CompoundTag tag)
	{
		var obj = Cast.cast(source.get(world), IAnimatedObject.class);
		if(obj == null) return false;
		AnimationSystem sys = obj.getAnimationSystem();
		if(sys == null) return true;
		sys.deserializeNBT(world.registryAccess(), tag);
		return true;
	}
}
