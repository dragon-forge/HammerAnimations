package org.zeith.hammeranims.api.tile;

import com.zeitheron.hammercore.utils.base.Cast;
import lombok.var;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

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
		return tile.getWorld();
	}
	
	@Override
	default Vec3d getAnimatedObjectPosition()
	{
		TileEntity tile = Cast.cast(this);
		var bp = tile.getPos();
		return new Vec3d(bp.getX() + 0.5, bp.getY(), bp.getZ() + 0.5);
	}
}