package org.zeith.hammeranims.core.contents.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.core.init.ContainersHA;
import org.zeith.hammerlib.api.forge.BlockAPI;

public class BlockBilly
		extends BaseEntityBlock
{
	public BlockBilly()
	{
		super(Properties.copy(Blocks.GLASS));
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
	{
		return ContainersHA.BILLY_TILE.create(pPos, pState);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType)
	{
		return BlockAPI.ticker(pLevel);
	}
}