package org.zeith.hammeranims.api.tile;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.contents.sources.EntityAnimationSourceType;

public interface IAnimatedEntity
		extends IAnimatedObject
{
	@Override
	default AnimationSource getAnimationSource()
	{
		Entity tile = Cast.cast(this);
		return new EntityAnimationSourceType.EntitySourceType(tile.getEntityId());
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