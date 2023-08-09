package org.zeith.hammeranims.api.tile;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.contents.sources.TileAnimationSourceType;
import org.zeith.hammerlib.util.java.Cast;

public interface IAnimatedTile
		extends IAnimatedObject
{
	@Override
	default AnimationSource getAnimationSource()
	{
		BlockEntity tile = Cast.cast(this);
		return new TileAnimationSourceType.TileSourceType(tile.getBlockPos());
	}
	
	@Override
	default Level getAnimatedObjectWorld()
	{
		BlockEntity tile = Cast.cast(this);
		return tile.getLevel();
	}
	
	@Override
	default Vec3 getAnimatedObjectPosition()
	{
		BlockEntity tile = Cast.cast(this);
		return Vec3.atCenterOf(tile.getBlockPos());
	}
}