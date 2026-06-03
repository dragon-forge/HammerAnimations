package org.zeith.hammeranims.core.contents.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;
import org.zeith.hammeranims.core.init.ContainersHA;
import org.zeith.hammeranims.joml.*;

import javax.annotation.Nullable;
import java.lang.Math;

public class EntityBilly
		extends EntityAnimal
		implements IAnimatedEntity
{
	protected final AnimationSystem animations = AnimationSystem.create(this);
	
	public EntityBilly(World worldIn)
	{
		super(worldIn);
		setSize(0.5F, 1F);
	}
	
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}
	
	@Nullable
	@Override
	public EntityAgeable createChild(EntityAgeable ageable)
	{
		return null;
	}
	
	@Override
	protected void initEntityAI()
	{
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
		this.tasks.addTask(4, new EntityAITempt(this, 1.2D, Items.REDSTONE, false));
		this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}
	
	@Override
	public void onEntityUpdate()
	{
		animations.tick();
		double prevPosX = this.prevPosX;
		double prevPosY = this.prevPosY;
		double prevPosZ = this.prevPosZ;
		super.onEntityUpdate();
		
		if(world.isRemote) return;
		
		setAlwaysRenderNameTag(false);
		setCustomNameTag(ContainersHA.BILLY_BLOCK.getLocalizedName());
		
		animations.startAnimationAt(CommonLayerNames.AMBIENT, ContainersHA.BILLY_BREATHE);
		
		Vec3d pos = getPositionVector();
		double moved = Math.sqrt(pos.squareDistanceTo(prevPosX, prevPosY, prevPosZ));
		boolean posChanged = Math.abs(pos.x - prevPosX) >= 0.00390625F || Math.abs(pos.z - prevPosZ) >= 0.00390625F;
		if(!posChanged) moved = 0;
		
		if(moved > 0)
		{
			animations.startAnimationAt(CommonLayerNames.LEGS, ContainersHA.BILLY_WALK);
		} else
		{
			animations.stopAnimation(CommonLayerNames.LEGS, 0.4F);
		}
	}
	
	@Override
	public void setupSystem(AnimationSystem.Builder builder)
	{
		builder.autoSync().geometry(ContainersHA.BILLY_GEOM).addLayers(
				AnimationLayer.builder(CommonLayerNames.HEAD_LOOK),
				AnimationLayer.builder(CommonLayerNames.AMBIENT),
				AnimationLayer.builder(CommonLayerNames.LEGS)
		);
	}
	
	@Override
	public AnimationSystem getAnimationSystem()
	{
		return animations;
	}
}