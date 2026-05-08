package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class QueryWorld
		extends Query
{
	protected boolean registered = false;
	protected Level world;
	
	public QueryWorld(Level world)
	{
		this.world = world;
		if(world != null)
			registerWorldVariables(this::setVariable);
	}
	
	@Override
	public void setWorld(Level world)
	{
		this.world = world;
		if(!registered) // in case of BlockEntities, world may be provided later.
			registerWorldVariables(this::setVariable);
	}
	
	protected void registerWorldVariables(BiConsumer<String, ReadonlyLzVarOp> reg)
	{
		if(registered || world == null) return;
		registered = true;
		reg.accept("query.moon_brightness", () -> world.getMoonBrightness());
		reg.accept("query.moon_phase", () -> world.getMoonPhase());
		reg.accept("query.time_of_day", () -> world.getTimeOfDay(partialTicks));
		reg.accept("query.time_stamp", () -> world.getGameTime());
	}
}