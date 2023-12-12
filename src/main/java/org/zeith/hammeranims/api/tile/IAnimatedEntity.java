package org.zeith.hammeranims.api.tile;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;
import org.zeith.hammerlib.util.java.Cast;

public interface IAnimatedEntity
		extends IAnimatedObject
{
	@Override
	default IObjectSource<?> getAnimationSource()
	{
		Entity entity = Cast.cast(this);
		return IObjectSource.ofEntity(entity).get();
	}
	
	@Override
	default World getAnimatedObjectWorld()
	{
		Entity tile = Cast.cast(this);
		return tile.level;
	}
	
	@Override
	default Vector3d getAnimatedObjectPosition()
	{
		Entity tile = Cast.cast(this);
		return tile.position();
	}
}