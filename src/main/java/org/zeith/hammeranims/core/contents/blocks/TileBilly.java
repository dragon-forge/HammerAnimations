package org.zeith.hammeranims.core.contents.blocks;

import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.utils.math.MathHelper;
import lombok.val;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentString;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.annotation.ExposedToAnimAction;
import org.zeith.hammeranims.api.geometry.model.IPositionalModel;
import org.zeith.hammeranims.api.tile.IAnimatedTile;
import org.zeith.hammeranims.core.contents.actions.MethodAnimAction;
import org.zeith.hammeranims.core.init.*;
import org.zeith.hammeranims.joml.*;

import java.util.Objects;

public class TileBilly
		extends TileSyncableTickable
		implements IAnimatedTile
{
	public final AnimationSystem animations = AnimationSystem.create(this);
	
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
		val player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 32, Objects::nonNull);
		if(player != null)
			player.sendMessage(new TextComponentString("[" + (world.isRemote ? "CLIENT" : "SERVER") + "] testCall() called @ " + pos + ", world time is " + (world.getWorldTime() % 24000L)));
		HammerAnimations.LOG.info("testCall() called @ {}", pos);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		animations.tick();
		
		int power = world.getRedstonePowerFromNeighbors(pos);
		
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
		
		mat.identity()
		   .translate(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F)
		   .rotateY((float) (MathHelper.torad * 0));
		IPositionalModel posMod = ContainersHA.BILLY_GEOM.getPositionalModel();
		posMod.applySystem(1F, animations);
		if(posMod.applyLocatorTransforms(mat, "particle"))
		{
			Vector3f pos = new Vector3f(0, 0, 0);
			mat.transformPosition(pos);
			
			Vector3f move = new Vector3f(0, 1F / 16F, 0);
			mat.transformPosition(move);
			move.sub(pos);
			
			if(atTickRate(5))
				world.spawnParticle(EnumParticleTypes.END_ROD, pos.x, pos.y, pos.z, move.x, move.y, move.z);
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