package org.zeith.hammeranims.core.contents;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.init.AnimationSourceTypesHM;

public class TileAnimationSourceType
		extends AnimationSourceType
{
	@Override
	public TileSourceType readSource(NBTTagCompound tag)
	{
		return new TileSourceType(tag);
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
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("x", pos.getX());
			tag.setInteger("y", pos.getY());
			tag.setInteger("z", pos.getZ());
			return tag;
		}
		
		@Override
		public AnimationSourceType getType()
		{
			return AnimationSourceTypesHM.TILE_TYPE;
		}
		
		@Override
		public IAnimatedObject get(World world)
		{
			return Cast.cast(world.getTileEntity(pos), IAnimatedObject.class);
		}
	}
}