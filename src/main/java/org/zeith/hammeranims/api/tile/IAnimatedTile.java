package org.zeith.hammeranims.api.tile;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.abstractions.sources.*;

public interface IAnimatedTile
		extends IAnimatedObject
{
	@Override
	default IObjectSource<?> getAnimationSource()
	{
		TileEntity tile = Cast.cast(this);
		return new TileSourceType.TileSource(tile.getPos());
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