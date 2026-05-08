package org.zeith.hammeranims.api.tile;

import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;
import org.zeith.hammerlib.util.java.Cast;

import java.util.function.BiConsumer;

public interface IAnimatedEntity
		extends IAnimatedObject
{
	default void registerEntityProperties(BiConsumer<String, ReadonlyLzVarOp> reg)
	{
	}
	
	@Override
	default Query createQuery()
	{
		Entity entity = Cast.cast(this);
		return new QueryEntity(entity);
	}
	
	@Override
	default IObjectSource<?> getAnimationSource()
	{
		Entity entity = Cast.cast(this);
		return IObjectSource.ofEntity(entity).get();
	}
	
	@Override
	default float getAnimatedObjectWidth()
	{
		Entity tile = Cast.cast(this);
		return tile.getBbWidth();
	}
	
	@Override
	default float getAnimatedObjectHeight()
	{
		Entity tile = Cast.cast(this);
		return tile.getBbHeight();
	}
	
	@Override
	default Level getAnimatedObjectWorld()
	{
		Entity tile = Cast.cast(this);
		return tile.level();
	}
	
	@Override
	default Vec3 getAnimatedObjectPosition()
	{
		Entity tile = Cast.cast(this);
		return tile.position();
	}
}