package org.zeith.hammeranims.api.tile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
	default Level getAnimatedObjectWorld()
	{
		Entity tile = Cast.cast(this);
		return tile.getLevel();
	}
	
	@Override
	default Vec3 getAnimatedObjectPosition()
	{
		Entity tile = Cast.cast(this);
		return tile.position();
	}
}