package org.zeith.hammeranims.core.contents.blocks;

import net.minecraft.block.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.core.init.ContainersHA;

public class BlockBilly
		extends ContainerBlock
{
	public BlockBilly()
	{
		super(Properties.copy(Blocks.GLASS));
	}
	
	@Nullable
	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_)
	{
		return ContainersHA.BILLY_TILE.create();
	}
}