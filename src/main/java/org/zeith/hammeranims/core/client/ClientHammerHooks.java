package org.zeith.hammeranims.core.client;

import com.zeitheron.hammercore.utils.base.Cast;
import com.zeitheron.hammercore.utils.java.tuples.*;
import lombok.var;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

import java.util.*;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientHammerHooks
{
	private static final List<Tuple3.Mutable3<IObjectSource<?>, Consumer<AnimationSystem>, Integer>> QUEUED_ACTIONS = new ArrayList<>();
	
	public static boolean LOG_TIMEOUTS = false;
	public static int DEFAULT_TIMEOUT = 100;
	
	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END) return;
		
		var w = HammerAnimations.PROXY.getClientWorld();
		if(w == null)
		{
			QUEUED_ACTIONS.clear();
			return;
		}
		
		QUEUED_ACTIONS.removeIf(src ->
		{
			int ticksAwaiting = src.c();
			src.setC(ticksAwaiting - 1);
			if(ticksAwaiting <= 0)
			{
				if(LOG_TIMEOUTS) HammerAnimations.LOG.warn("Animation action for {} has timed out.", src.a());
				return true;
			}
			return applyAnimationSystem(w, src.a(), src.b());
		});
	}
	
	/**
	 * Apply animation system client-side for a given animation address with a given timeout.
	 */
	public static void applySystem(IObjectSource<?> source, int timeout, NBTTagCompound tag)
	{
		if(source == null || tag == null)
		{
			HammerAnimations.LOG.error("Completely ignored animation sync with {} being null.", tag);
			return;
		}
		
		enqueueAction(source, timeout, sys -> sys.deserializeNBT(tag));
	}
	
	public static void startAnimation(IObjectSource<?> source, int timeout, String layer, ConfiguredAnimation cfgAnim)
	{
		if(layer == null || cfgAnim == null)
		{
			HammerAnimations.LOG.error("Completely ignored animation activation with layer({}) or cfgAnim({}) being null.", layer, cfgAnim);
			return;
		}
		
		enqueueAction(source, timeout, sys ->
				{
					var l = sys.getLayer(layer);
					if(l != null) l.startAnimationSync(cfgAnim, false);
				}
		);
	}
	
	public static void enqueueAction(IObjectSource<?> source, int timeout, Consumer<AnimationSystem> action)
	{
		if(source == null || action == null)
		{
			HammerAnimations.LOG.error("Completely ignored queued action with source({}) or action({}) being null.", source, action);
			return;
		}
		
		if(!applyAnimationSystem(HammerAnimations.PROXY.getClientWorld(), source, action))
			QUEUED_ACTIONS.add(Tuples.mutable(source, action, timeout));
	}
	
	private static boolean applyAnimationSystem(World world, IObjectSource<?> source, Consumer<AnimationSystem> handler)
	{
		var obj = Cast.cast(source.get(world), IAnimatedObject.class);
		if(obj == null) return false;
		AnimationSystem sys = obj.getAnimationSystem();
		if(sys == null) return true;
		handler.accept(sys);
		return true;
	}
}
