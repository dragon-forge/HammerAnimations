package org.zeith.hammeranims.core.contents.blocks;

import com.zeitheron.hammercore.api.blocks.INoBlockstate;
import com.zeitheron.hammercore.internal.blocks.base.BlockTileHC;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockBilly
		extends BlockTileHC<TileBilly>
		implements INoBlockstate
{
	public BlockBilly()
	{
		super(Material.GLASS, TileBilly.class, "billy");
		lightOpacity = 0;
	}
	
	@Override
	public boolean isFullBlock(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
}