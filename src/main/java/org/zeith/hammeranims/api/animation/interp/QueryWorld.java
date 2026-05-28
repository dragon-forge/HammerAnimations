package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import lombok.Getter;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

/**
 * This is an extensible class (this gets passed to animation layers)
 */
public class QueryWorld
		extends Query
{
	private static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[] {1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	
	protected @Getter World world;
	
	public QueryWorld()
	{
	}
	
	public QueryWorld(World world)
	{
		setWorld(world);
	}
	
	@Override
	public void setWorld(World world)
	{
		if(world == null) return;
		this.world = world;
		registerWorldVariables(this, world, this::setVariable);
	}
	
	public static void registerWorldVariables(Query q, World world, BiConsumer<String, ReadonlyLzVarOp> reg)
	{
		reg.accept("query.moon_brightness", () -> getMoonBrightness(world));
		reg.accept("query.moon_phase", () -> getMoonPhase(world));
		reg.accept("query.time_of_day", () -> timeOfDay(world.getWorldTime(), q.partialTicks));
		reg.accept("query.time_stamp", world::getTotalWorldTime);
	}
	
	public static double timeOfDay(long pDayTime, float partialTicks)
	{
		return (pDayTime % 24000L + partialTicks) / 20D;
	}
	
	public static float getMoonBrightness(World world)
	{
		return MOON_BRIGHTNESS_PER_PHASE[getMoonPhase(world)];
	}
	
	public static int getMoonPhase(World world)
	{
		return world.provider.getMoonPhase(world.getWorldTime());
	}
}