package org.zeith.hammeranims.api.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;
import org.zeith.hammerlib.util.java.Cast;

public interface IAnimatedTile
		extends IAnimatedObject
{
	@Override
	default IObjectSource<?> getAnimationSource()
	{
		TileEntity tile = Cast.cast(this);
		return IObjectSource.ofTile(tile).get();
	}
	
	@Override
	default World getAnimatedObjectWorld()
	{
		TileEntity tile = Cast.cast(this);
		return tile.getLevel();
	}
	
	@Override
	default Vector3d getAnimatedObjectPosition()
	{
		TileEntity tile = Cast.cast(this);
		return Vector3d.atCenterOf(tile.getBlockPos());
	}
}