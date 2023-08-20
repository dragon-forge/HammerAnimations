package org.zeith.hammeranims.core.contents.blocks;

import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.math.MathHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.geometry.model.IPositionalModel;
import org.zeith.hammeranims.api.tile.IAnimatedTile;
import org.zeith.hammeranims.core.init.*;
import org.zeith.hammeranims.joml.*;

public class TileBilly
		extends TileSyncableTickable
		implements IAnimatedTile
{
	public final AnimationSystem animations = AnimationSystem.create(this);
	
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
	public void tick()
	{
		super.tick();
		animations.tick();
		
		int power = world.getRedstonePowerFromNeighbors(pos);
		
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
				.translate(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F)
				.rotateY((float) (MathHelper.torad * 0));
		IPositionalModel posMod = ContainersHA.BILLY_GEOM.getPositionalModel();
		posMod.applySystem(1F, animations);
		if(posMod.applyBoneTransforms(mat, "bob"))
		{
			Vector3f relativePos = new Vector3f(-2 / 16F, 1 / 16F, 1 / 16F);
			mat.transformPosition(relativePos);
			
			if(atTickRate(5))
				world.spawnParticle(EnumParticleTypes.END_ROD, relativePos.x, relativePos.y, relativePos.z, 0, 0.1, 0);
		}
		
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setTag("Anims", animations.serializeNBT());
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		animations.deserializeNBT(nbt.getCompoundTag("Anims"));
	}
	
	@Override
	public AnimationSystem getAnimationSystem()
	{
		return animations;
	}
}