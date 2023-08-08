package org.zeith.hammeranims.core.contents.sources;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

public class TileAnimationSourceType
		extends AnimationSourceType
{
	@Override
	public TileSourceType readSource(NBTTagCompound tag)
	{
		return new TileSourceType(tag);
	}
	
	public AnimationSource of(TileEntity tile)
	{
		return new TileSourceType(tile.getPos());
	}
	
	public static class TileSourceType
			extends AnimationSource
	{
		public final BlockPos pos;
		
		public TileSourceType(BlockPos pos)
		{
			this.pos = pos;
		}
		
		public TileSourceType(NBTTagCompound tag)
		{
			this.pos = new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
		}
		
		@Override
		public NBTTagCompound writeSource()
		{
			NBTTagCompound tag = InstanceHelpers.newNBTCompound();
			tag.setInteger("x", pos.getX());
			tag.setInteger("y", pos.getY());
			tag.setInteger("z", pos.getZ());
			return tag;
		}
		
		@Override
		public AnimationSourceType getType()
		{
			return DefaultsHA.TILE_TYPE;
		}
		
		@Override
		public IAnimatedObject get(World world)
		{
			return Cast.cast(world.getTileEntity(pos), IAnimatedObject.class);
		}
	}
}