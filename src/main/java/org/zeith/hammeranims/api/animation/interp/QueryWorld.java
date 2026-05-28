package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class QueryWorld
		extends Query
{
	protected boolean registered = false;
	protected Level world;
	
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
		registerWorldVariables(this, world, this::setVariable);
	}
	
	public static void registerWorldVariables(Query q, Level world, BiConsumer<String, ReadonlyLzVarOp> reg)
	{
		reg.accept("query.moon_brightness", () -> world.getMoonBrightness());
		reg.accept("query.moon_phase", () -> world.getMoonPhase());
		reg.accept("query.time_of_day", () -> world.getTimeOfDay(q.partialTicks));
		reg.accept("query.time_stamp", () -> world.getGameTime());
	}
}