package org.zeith.hammeranims.api.animation.interp;

import lombok.Getter;
import net.minecraft.world.level.Level;

/**
 * This is an extensible class (this gets passed to animation layers)
 */
public class QueryWorld
		extends Query
{
	protected @Getter Level world;
	
	public QueryWorld()
	{
	}
	
	public QueryWorld(Level world)
	{
		setWorld(world);
	}
	
	@Override
	public void setWorld(Level world)
	{
		if(world == null) return;
		this.world = world;
		registerWorldVariables(this, world, IVariableRegistrar.of(this));
	}
	
	public static void registerWorldVariables(Query q, Level world, IVariableRegistrar reg)
	{
		reg.registerR("query.moon_brightness", () -> world.getMoonBrightness());
		reg.registerR("query.moon_phase", () -> world.getMoonPhase());
		reg.registerR("query.time_of_day", () -> world.getTimeOfDay(q.partialTicks));
		reg.registerR("query.time_stamp", () -> world.getGameTime());
	}
}