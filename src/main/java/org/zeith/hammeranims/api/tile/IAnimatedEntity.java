package org.zeith.hammeranims.api.tile;

import com.zeitheron.hammercore.utils.base.Cast;
import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.abstractions.sources.*;

import java.util.function.BiConsumer;

public interface IAnimatedEntity
		extends IAnimatedObject
{
	default void registerEntityProperties(Query q, BiConsumer<String, ReadonlyLzVarOp> reg)
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
		Entity tile = Cast.cast(this);
		return new EntitySourceType.EntitySource(tile.getEntityId());
	}
	
	@Override
	default float getAnimatedObjectWidth()
	{
		Entity tile = Cast.cast(this);
		return tile.width;
	}
	
	@Override
	default float getAnimatedObjectHeight()
	{
		Entity tile = Cast.cast(this);
		return tile.height;
	}
	
	@Override
	default World getAnimatedObjectWorld()
	{
		Entity tile = Cast.cast(this);
		return tile.world;
	}
	
	@Override
	default Vec3d getAnimatedObjectPosition()
	{
		Entity tile = Cast.cast(this);
		return new Vec3d(tile.posX, tile.posY, tile.posZ);
	}
}