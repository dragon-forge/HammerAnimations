package org.zeith.hammeranims.core.contents.sources;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammerlib.util.java.Cast;

public class TileAnimationSourceType
		extends AnimationSourceType
{
	@Override
	public TileSourceType readSource(CompoundTag tag)
	{
		return new TileSourceType(tag);
	}
	
	public AnimationSource of(BlockEntity tile)
	{
		return new TileSourceType(tile.getBlockPos());
	}
	
	public static class TileSourceType
			extends AnimationSource
	{
		public final BlockPos pos;
		
		public TileSourceType(BlockPos pos)
		{
			this.pos = pos;
		}
		
		public TileSourceType(CompoundTag tag)
		{
			this.pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
		}
		
		@Override
		public CompoundTag writeSource()
		{
			var tag = InstanceHelpers.newNBTCompound();
			tag.putInt("x", pos.getX());
			tag.putInt("y", pos.getY());
			tag.putInt("z", pos.getZ());
			return tag;
		}
		
		@Override
		public AnimationSourceType getType()
		{
			return DefaultsHA.TILE_TYPE;
		}
		
		@Override
		public IAnimatedObject get(Level world)
		{
			return Cast.cast(world.getBlockEntity(pos), IAnimatedObject.class);
		}
	}
}