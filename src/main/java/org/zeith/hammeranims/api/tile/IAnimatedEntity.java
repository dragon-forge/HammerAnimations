package org.zeith.hammeranims.api.tile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.contents.sources.EntityAnimationSourceType;
import org.zeith.hammerlib.util.java.Cast;

public interface IAnimatedEntity
		extends IAnimatedObject
{
	@Override
	default AnimationSource getAnimationSource()
	{
		Entity tile = Cast.cast(this);
		return new EntityAnimationSourceType.EntitySourceType(tile.getId());
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