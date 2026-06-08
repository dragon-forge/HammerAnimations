package org.zeith.hammeranims.api.animation.interp;

import com.zeitheron.hammercore.lib.zlib.utils.Vec2D;
import dev.zeith.lzvm.molang.compiler.libs.MoMathLibrary;
import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;

import static dev.zeith.lzvm.op.ReadonlyLzVarOp.ofBool;

/**
 * This is an extensible class (this gets passed to animation layers)
 */
public class QueryEntity
		extends QueryWorld
{
	protected @Getter Entity entity;
	
	public QueryEntity()
	{
	}
	
	public QueryEntity(Entity entity)
	{
		super(entity != null ? entity.getEntityWorld() : null);
		setEntity(entity);
	}
	
	public void setEntity(Entity entity)
	{
		if(entity == null) return;
		setWorld(entity.getEntityWorld());
		this.entity = entity;
		registerEntityVariables(this, entity, IVariableRegistrar.of(this));
	}
	
	public static void registerEntityVariables(Query q, Entity entity, IVariableRegistrar reg)
	{
		if(entity instanceof IAnimatedEntity)
			((IAnimatedEntity) entity).registerEntityProperties(q, reg);
		
		reg.registerR("query.cardinal_facing", () -> entity.getHorizontalFacing().getIndex());
		reg.registerR("query.cardinal_facing_2d", () ->
				{
					int directionId = entity.getHorizontalFacing().getIndex();
					return directionId < 2 ? 6 : directionId;
				}
		);
		reg.registerR("query.is_spectator", ofBool(() -> entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()));
		reg.registerR("query.is_sprinting", ofBool(entity::isSprinting));
		reg.registerR("query.is_crawling", ofBool(entity::isSneaking));
		reg.registerR("query.is_fire_immune", ofBool(entity::isImmuneToFire));
		reg.registerR("query.is_in_lava", ofBool(entity::isInLava));
		reg.registerR("query.is_in_water", ofBool(entity::isInWater));
		reg.registerR("query.is_on_ground", ofBool(() -> entity.onGround));
		reg.registerR("query.is_onfire", ofBool(entity::isBurning));
		reg.registerR("query.has_gravity", ofBool(() -> !entity.hasNoGravity()));
		reg.registerR("query.walk_distance", () -> MoMathLibrary.lerp(entity.prevDistanceWalkedModified, entity.distanceWalkedModified, q.partialTicks));
		reg.registerR("query.has_collision", ofBool(() -> !entity.noClip));
		reg.registerR("query.has_rider", ofBool(() -> !entity.getPassengers().isEmpty()));
		reg.registerR("query.is_riding", ofBool(entity::isRiding));
		reg.registerR("query.is_invisible", ofBool(entity::isInvisible));
		reg.registerR("query.is_silent", ofBool(entity::isSilent));
		reg.registerR("query.is_sneaking", ofBool(entity::isSneaking));
		reg.registerR("query.is_in_contact_with_water", ofBool(entity::isInWater));
		reg.registerR("query.is_swimming", ofBool(false));
		reg.registerR("query.has_player_rider", ofBool(() -> entity.getPassengers().stream().anyMatch(EntityPlayer.class::isInstance)));
		reg.registerR("query.has_owner", ofBool(() -> entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null));
		reg.registerR("query.ground_speed", () -> Vec2D.ZERO.distanceTo(entity.motionX, entity.motionZ));
		reg.registerR("query.vertical_speed", () -> entity.motionY);
		reg.registerR("query.yaw_speed", () -> entity.rotationYaw - entity.prevRotationYaw);
		reg.registerR("query.movement_direction", () -> Vec2D.ZERO.distanceTo(entity.motionX, entity.motionZ) > 0.001 ? getNearest(entity.motionX, entity.motionY, entity.motionZ).getIndex() : 6);
//		reg.accept("query.rider_body_x_rotation", () ->
//				!entity.isVehicle() || entity.getFirstPassenger() == null
//				? 0
//				: entity.getFirstPassenger() instanceof LivingEntity
//				  ? 0
//				  : entity.getFirstPassenger().getViewXRot(partialTicks)
//		);
//		reg.accept("query.rider_body_y_rotation", () ->
//				!entity.isVehicle() || entity.getFirstPassenger() == null
//				? 0
//				: entity.getFirstPassenger() instanceof LivingEntity living
//				  ? MoMathLibrary.lerp(living.yBodyRotO, living.yBodyRot, partialTicks)
//				  : entity.getFirstPassenger().getViewYRot(partialTicks)
//		);
//		reg.accept("query.rider_head_x_rotation", () -> entity.getFirstPassenger() instanceof LivingEntity le ? le.getViewXRot(partialTicks) : 0);
//		reg.accept("query.rider_head_y_rotation", () -> entity.getFirstPassenger() instanceof LivingEntity le ? le.getViewXRot(partialTicks) : 0);
		
		reg.registerR("query.head_x_rotation", () -> entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * q.partialTicks);
		reg.registerR("query.body_x_rotation", () -> entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * q.partialTicks);
		
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase le = (EntityLivingBase) entity;
			reg.registerR("query.invulnerable_ticks", () -> le.hurtTime == 0 ? 0 : le.hurtTime - q.partialTicks);
			reg.registerR("query.body_y_rotation", () -> getBodyYaw(le, q.partialTicks));
			reg.registerR("query.blocking", ofBool(le::isActiveItemStackBlocking));
			reg.registerR("query.health", le::getHealth);
			reg.registerR("query.death_ticks", () -> le.getHealth() <= 0.0F ? le.deathTime + q.partialTicks : 0);
			reg.registerR("query.hurt_time", () -> le.hurtTime == 0 ? le.hurtTime : (le.hurtTime - q.partialTicks));
			reg.registerR("query.max_health", le::getMaxHealth);
			reg.registerR("query.is_alive", ofBool(le::isEntityAlive));
			reg.registerR("query.is_baby", ofBool(le::isChild));
			reg.registerR("query.is_using_item", ofBool(le::isHandActive));
			reg.registerR("query.query.is_wall_climbing", ofBool(le::isOnLadder));
			reg.registerR("query.item_in_use_duration", () -> (le.getItemInUseCount() + q.partialTicks) / 20.0);
//			reg.accept("query.scale", le::getScale);
			reg.registerR("query.item_remaining_use_duration", () -> Math.max(0, (le.getItemInUseMaxCount() - le.getItemInUseCount()) - q.partialTicks) / 20.0);
			reg.registerR("query.head_y_rotation", () -> getHeadYaw(le, q.partialTicks));
		} else
		{
//			reg.accept("query.body_x_rotation", () -> entity.getViewXRot(partialTicks));
//			reg.accept("query.body_y_rotation", () -> entity.getViewYRot(partialTicks));
		}
		
		if(entity instanceof EntityLiving)
		{
			EntityLiving mob = (EntityLiving) entity;
			reg.registerR("query.is_leashed", ofBool(mob::getLeashed));
			reg.registerR("query.has_target", ofBool(() -> mob.getAttackTarget() != null));
//			reg.accept("query.can_climb", ofBool(() -> !mob.isNoAi() && mob.getNavigation() instanceof WallClimberNavigation));
//			reg.accept("query.can_fly", ofBool(() -> !mob.isNoAi() && mob.getNavigation() instanceof FlyingPathNavigation));
//			reg.accept("query.can_swim", ofBool(() -> !mob.isNoAi() && (mob.getNavigation() instanceof WaterBoundPathNavigation || mob.getNavigation() instanceof AmphibiousPathNavigation)));
//			reg.accept("query.can_walk", ofBool(() -> !mob.isNoAi() && (mob.getNavigation() instanceof GroundPathNavigation || mob.getNavigation() instanceof AmphibiousPathNavigation)));
		}
		
		if(entity instanceof EntityAnimal)
		{
			EntityAnimal a = (EntityAnimal) entity;
			reg.registerR("query.is_in_love", () -> a.isInLove() ? 1D : 0D);
		}
		
		if(entity instanceof EntityWolf)
		{
			EntityWolf n = (EntityWolf) entity;
			reg.registerR("query.is_angry", ofBool(n::isAngry));
		}
		
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) entity;
			reg.registerR("query.sleep_rotation", () -> getBedOrientationInDegrees(p));
		}
	}
	
	public static float getHeadYaw(EntityLivingBase entity, float partialTicks)
	{
		return interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
	}
	
	public static float getBodyYaw(EntityLivingBase entity, float partialTicks)
	{
		boolean shouldSit = entity.isRiding() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		float f = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
		float f1 = interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
		if(shouldSit && entity.getRidingEntity() instanceof EntityLivingBase)
		{
			EntityLivingBase le = (EntityLivingBase) entity.getRidingEntity();
			f = interpolateRotation(le.prevRenderYawOffset, le.renderYawOffset, partialTicks);
			float f3 = MathHelper.wrapDegrees(f1 - f);
			if(f3 < -85.0F) f3 = -85.0F;
			if(f3 >= 85.0F) f3 = 85.0F;
			f = f1 - f3;
			if(f3 * f3 > 2500.0F) f += f3 * 0.2F;
		}
		return f;
	}
	
	public static float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks)
	{
		float f;
		for(f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) ;
		while(f >= 180.0F) f -= 360.0F;
		return prevYawOffset + partialTicks * f;
	}
	
	public static EnumFacing getNearest(double x, double y, double z)
	{
		return EnumFacing.getFacingFromVector((float) x, (float) y, (float) z);
	}
	
	public static float getBedOrientationInDegrees(EntityPlayer player)
	{
		IBlockState state = player.bedLocation == null ? null : player.world.getBlockState(player.bedLocation);
		if(state != null && state.getBlock().isBed(state, player.world, player.bedLocation, player))
		{
			EnumFacing face = state.getBlock().getBedDirection(state, player.world, player.bedLocation);
			switch(face)
			{
				case SOUTH:
					return 90.0F;
				case WEST:
					return 0.0F;
				case NORTH:
					return 270.0F;
				case EAST:
					return 180.0F;
			}
		}
		return 0.0F;
	}
}