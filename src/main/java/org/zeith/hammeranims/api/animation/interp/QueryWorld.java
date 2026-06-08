package org.zeith.hammeranims.api.animation.interp;

import lombok.Getter;
import net.minecraft.world.World;

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
		registerWorldVariables(this, world, IVariableRegistrar.of(this));
	}
	
	public static void registerWorldVariables(Query q, World world, IVariableRegistrar reg)
	{
		reg.registerR("query.moon_brightness", () -> getMoonBrightness(world));
		reg.registerR("query.moon_phase", () -> getMoonPhase(world));
		reg.registerR("query.time_of_day", () -> timeOfDay(world.getWorldTime(), q.partialTicks));
		reg.registerR("query.time_stamp", world::getTotalWorldTime);
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