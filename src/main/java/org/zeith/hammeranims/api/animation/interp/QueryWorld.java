package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class QueryWorld
		extends Query
{
	protected boolean registered = false;
	protected World world;
	
	public QueryWorld(World world)
	{
		this.world = world;
		if(world != null)
			registerWorldVariables(this::setVariable);
	}
	
	@Override
	public void setWorld(World world)
	{
		this.world = world;
		if(!registered) // in case of BlockEntities, world may be provided later.
			registerWorldVariables(this::setVariable);
	}
	
	protected void registerWorldVariables(BiConsumer<String, ReadonlyLzVarOp> reg)
	{
		if(registered || world == null) return;
		registered = true;
		reg.accept("query.moon_brightness", this::getMoonBrightness);
		reg.accept("query.moon_phase", this::getMoonPhase);
		reg.accept("query.time_of_day", () -> timeOfDay(world.getWorldTime()));
		reg.accept("query.time_stamp", () -> world.getTotalWorldTime());
	}
	
	public double timeOfDay(long pDayTime)
	{
		return (pDayTime % 24000L) / 20D;
	}
	
	public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[] {1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	
	public float getMoonBrightness()
	{
		return MOON_BRIGHTNESS_PER_PHASE[getMoonPhase()];
	}
	
	public int getMoonPhase()
	{
		return world.provider.getMoonPhase(world.getWorldTime());
	}
}