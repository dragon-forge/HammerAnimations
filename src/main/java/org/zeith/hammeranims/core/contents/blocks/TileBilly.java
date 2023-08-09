package org.zeith.hammeranims.core.contents.blocks;

import com.zeitheron.hammercore.tile.TileSyncableTickable;
import net.minecraft.nbt.NBTTagCompound;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.tile.IAnimatedTile;
import org.zeith.hammeranims.core.init.*;

public class TileBilly
		extends TileSyncableTickable
		implements IAnimatedTile
{
	public final AnimationSystem animations = AnimationSystem.create(this);
	
	@Override
	public void setupSystem(AnimationSystem.Builder builder)
	{
		builder.addLayers(AnimationLayer.builder(CommonLayerNames.LEGS))
				.addLayers(AnimationLayer.builder(CommonLayerNames.AMBIENT));
	}
	
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

		animations.startAnimationAt(CommonLayerNames.AMBIENT, org.zeith.hammeranims.core.init.ContainersHA.BILLY_BREATHE.configure().speed(0.5F));
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