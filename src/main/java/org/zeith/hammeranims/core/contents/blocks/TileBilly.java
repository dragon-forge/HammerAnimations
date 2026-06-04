package org.zeith.hammeranims.core.contents.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.annotation.ExposedToAnimAction;
import org.zeith.hammeranims.api.tile.IAnimatedTile;
import org.zeith.hammeranims.core.contents.actions.MethodAnimAction;
import org.zeith.hammeranims.core.init.*;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;

import java.util.Objects;

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
		builder.geometry(ContainersHA.BILLY_GEOM).addLayers(AnimationLayer
				.builder(CommonLayerNames.LEGS)
				.mask(ContainersHA.BILLY_GEOM
						.getPositionalModel()
						.maskAnyOfOrChildren("body")
				)
		).addLayers(AnimationLayer.builder(CommonLayerNames.AMBIENT));
	}
	
	protected final Matrix4f mat = new Matrix4f();
	
	@ExposedToAnimAction
	public void testCall()
	{
		Player player = level.getNearestPlayer(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 32, Objects::nonNull);
		if(player != null)
			player.sendSystemMessage(Component.literal("[" + (level.isClientSide ? "CLIENT" : "SERVER") + "] testCall() called @ " + worldPosition + ", world time is " + level.getDayTime()));
		HammerAnimations.LOG.info("testCall() called @ {}", worldPosition);
	}
	
	@Override
	public void update()
	{
		super.update();
		animations.tick();
		setChanged();
		
		int power = level.getBestNeighborSignal(worldPosition);
		
		if(power > 0)
			animations.startAnimationAt(CommonLayerNames.LEGS, ContainersHA.BILLY_WALK
					.configure()
					.speed(power / 15F)
					.loopMode(LoopMode.ONCE)
					.next(ContainersHA.BILLY_WALK
							.configure()
							.speed(2F)
							.loopMode(LoopMode.ONCE)
							.next(DefaultsHA.NULL_ANIM.configure())
							.onFinish(MethodAnimAction.create(getClass(), "testCall", this))
					)
			);
//		else
//			animations.startAnimationAt(CommonLayerNames.LEGS, ConfiguredAnimation.noAnimation()
//					.transitionTime(1F));
		
		animations.startAnimationAt(CommonLayerNames.AMBIENT, ContainersHA.BILLY_BREATHE.configure().speed(0.5F));
		
		var mat = getAnimatedBoneMatrix("bob", 1F);
		if(mat != null)
		{
			Vector3d pos = new Vector3d(-2 / 16F, 2 / 16F, 1 / 16F);
			mat.transformPosition(pos);
			
			Vector3d move = new Vector3d(-2 / 16F, 2 / 16F, 2 / 16F);
			mat.transformPosition(move);
			
			move.sub(pos).normalize(0.1f);
			
			if(atTickRate(5))
				level.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, move.x, move.y, move.z);
		}
	}
	
	@Override
	public AnimationSystem getAnimationSystem()
	{
		return animations;
	}
}