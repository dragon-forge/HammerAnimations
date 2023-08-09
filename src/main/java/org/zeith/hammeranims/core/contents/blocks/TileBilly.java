package org.zeith.hammeranims.core.contents.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.tile.IAnimatedTile;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;

public class TileBilly
		extends TileSyncableTickable
		implements IAnimatedTile
{
	@NBTSerializable
	public final AnimationSystem animations = AnimationSystem.create(this);
	
	public TileBilly(BlockEntityType<TileBilly> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void setupSystem(AnimationSystem.Builder builder)
	{
		builder.addLayers(AnimationLayer.builder(CommonLayerNames.LEGS))
				.addLayers(AnimationLayer.builder(CommonLayerNames.AMBIENT));
	}
	
	@Override
	public void update()
	{
		super.update();
		animations.tick();
		
		int power = level.getBestNeighborSignal(worldPosition);
		
		if(power > 0)
			animations.startAnimationAt(CommonLayerNames.LEGS, org.zeith.hammeranims.core.init.ContainersHA.BILLY_WALK.configure()
					.speed(power / 15F));
		else
			animations.startAnimationAt(CommonLayerNames.LEGS, ConfiguredAnimation.noAnimation()
					.transitionTime(1F));
		
		animations.startAnimationAt(CommonLayerNames.AMBIENT, org.zeith.hammeranims.core.init.ContainersHA.BILLY_BREATHE.configure()
				.speed(0.5F));
	}
	
	@Override
	public AnimationSystem getAnimationSystem()
	{
		return animations;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		animations.deserializeNBT(nbt.getCompound("Anims"));
		super.deserializeNBT(nbt);
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		var nbt = super.serializeNBT();
		nbt.put("Anims", animations.serializeNBT());
		return nbt;
	}
}