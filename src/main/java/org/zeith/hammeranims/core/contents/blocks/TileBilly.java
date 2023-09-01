package org.zeith.hammeranims.core.contents.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.geometry.model.IPositionalModel;
import org.zeith.hammeranims.api.tile.IAnimatedTile;
import org.zeith.hammeranims.core.init.*;
import org.zeith.hammeranims.joml.*;
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
		builder.addLayers(AnimationLayer.builder(CommonLayerNames.LEGS)
						.mask(
								ContainersHA.BILLY_GEOM.getPositionalModel()
										.maskAnyOfOrChildren("body")
						)
				)
				.addLayers(AnimationLayer.builder(CommonLayerNames.AMBIENT));
	}
	
	protected final Matrix4f mat = new Matrix4f();
	
	@Override
	public void update()
	{
		super.update();
		animations.tick();
		
		int power = level.getBestNeighborSignal(worldPosition);
		
		if(power > 0)
			animations.startAnimationAt(CommonLayerNames.LEGS, ContainersHA.BILLY_WALK.configure()
					.speed(power / 15F)
					.loopMode(LoopMode.ONCE)
					.next(ContainersHA.BILLY_WALK.configure()
							.speed(2F)
							.loopMode(LoopMode.ONCE)
							.next(DefaultsHA.NULL_ANIM.configure())
							.onFinish(ContainersHA.HELLO_WORLD_ACTION.defaultInstance()
									.withMessage("YOLO")
							)
					)
			);
//		else
//			animations.startAnimationAt(CommonLayerNames.LEGS, ConfiguredAnimation.noAnimation()
//					.transitionTime(1F));
		
		animations.startAnimationAt(CommonLayerNames.AMBIENT, ContainersHA.BILLY_BREATHE.configure()
				.speed(0.5F));
		
		mat.identity()
				.translate(worldPosition.getX() + 0.5F, worldPosition.getY(), worldPosition.getZ() + 0.5F)
				.rotateY((float) (Mth.DEG_TO_RAD * 0));
		IPositionalModel posMod = ContainersHA.BILLY_GEOM.getPositionalModel();
		posMod.applySystem(1F, animations);
		if(posMod.applyBoneTransforms(mat, "bob"))
		{
			Vector3f relativePos = new Vector3f(-2 / 16F, 1 / 16F, 1 / 16F);
			mat.transformPosition(relativePos);
			
			if(atTickRate(5))
				level.addParticle(ParticleTypes.END_ROD, relativePos.x, relativePos.y, relativePos.z, 0, 0.1, 0);
		}
	}
	
	@Override
	public AnimationSystem getAnimationSystem()
	{
		return animations;
	}
}