package org.zeith.hammeranims.api.tile;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.contents.sources.TileAnimationSourceType;

public interface IAnimatedTile
		extends IAnimatedObject
{
	@Override
	default AnimationSource getAnimationSource()
	{
		TileEntity tile = Cast.cast(this);
		return new TileAnimationSourceType.TileSourceType(tile.getPos());
	}
	
	@Override
	default World getAnimatedObjectWorld()
	{
		TileEntity tile = Cast.cast(this);
		return tile.getWorld();
	}
	
	@Override
	default Vec3d getAnimatedObjectPosition()
	{
		TileEntity tile = Cast.cast(this);
		return new Vec3d(tile.getPos());
	}
}