package org.zeith.hammeranims.api.animation.interp;

import com.zeitheron.hammercore.lib.zlib.utils.Vec2D;
import dev.zeith.lzvm.molang.compiler.libs.MoMathLibrary;
import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import lombok.Getter;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;

import java.util.function.BiConsumer;

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
		registerEntityVariables(this, entity, this::setVariable);
	}
	
	public static void registerEntityVariables(Query q, Entity entity, BiConsumer<String, ReadonlyLzVarOp> reg)
	{
		if(entity instanceof IAnimatedEntity)
			((IAnimatedEntity) entity).registerEntityProperties(q, reg);
		
		reg.accept("query.cardinal_facing", () -> entity.getHorizontalFacing().getIndex());
		reg.accept("query.cardinal_facing_2d", () ->
				{
					int directionId = entity.getHorizontalFacing().getIndex();
					return directionId < 2 ? 6 : directionId;
				}
		);
		reg.accept("query.is_spectator", ofBool(() -> entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()));
		reg.accept("query.is_sprinting", ofBool(entity::isSprinting));
		reg.accept("query.is_crawling", ofBool(entity::isSneaking));
		reg.accept("query.is_fire_immune", ofBool(entity::isImmuneToFire));
		reg.accept("query.is_in_lava", ofBool(entity::isInLava));
		reg.accept("query.is_in_water", ofBool(entity::isInWater));
		reg.accept("query.is_on_ground", ofBool(() -> entity.onGround));
		reg.accept("query.is_onfire", ofBool(entity::isBurning));
		reg.accept("query.has_gravity", ofBool(() -> !entity.hasNoGravity()));
		reg.accept("query.walk_distance", () -> MoMathLibrary.lerp(entity.prevDistanceWalkedModified, entity.distanceWalkedModified, q.partialTicks));
		reg.accept("query.has_collision", ofBool(() -> !entity.noClip));
		reg.accept("query.has_rider", ofBool(() -> !entity.getPassengers().isEmpty()));
		reg.accept("query.is_riding", ofBool(entity::isRiding));
		reg.accept("query.is_invisible", ofBool(entity::isInvisible));
		reg.accept("query.is_silent", ofBool(entity::isSilent));
		reg.accept("query.is_sneaking", ofBool(entity::isSneaking));
		reg.accept("query.is_in_contact_with_water", ofBool(entity::isInWater));
//		reg.accept("query.is_swimming", ofBool(entity::isSwimming));
//		reg.accept("query.has_player_rider", ofBool(() -> entity.hasPassenger(Player.class::isInstance)));
		reg.accept("query.has_owner", ofBool(() -> entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null));
		reg.accept("query.ground_speed", () -> Vec2D.ZERO.distanceTo(entity.motionX, entity.motionZ));
		reg.accept("query.vertical_speed", () -> entity.motionY);
		reg.accept("query.yaw_speed", () -> entity.rotationYaw - entity.prevRotationYaw);
		reg.accept("query.movement_direction", () -> Vec2D.ZERO.distanceTo(entity.motionX, entity.motionZ) > 0.001 ? getNearest(entity.motionX, entity.motionY, entity.motionZ).getIndex() : 6);
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
		
		reg.accept("query.head_x_rotation", () -> entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * q.partialTicks);
		reg.accept("query.body_x_rotation", () -> entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * q.partialTicks);
		
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase le = (EntityLivingBase) entity;
			reg.accept("query.invulnerable_ticks", () -> le.hurtTime == 0 ? 0 : le.hurtTime - q.partialTicks);
			reg.accept("query.body_y_rotation", () -> getBodyYaw(le, q.partialTicks));
//			reg.accept("query.blocking", ofBool(le::isBlocking));
			reg.accept("query.health", le::getHealth);
			reg.accept("query.death_ticks", () -> le.getHealth() <= 0.0F ? le.deathTime + q.partialTicks : 0);
			reg.accept("query.hurt_time", () -> le.hurtTime == 0 ? le.hurtTime : (le.hurtTime - q.partialTicks));
			reg.accept("query.max_health", le::getMaxHealth);
			reg.accept("query.is_alive", ofBool(le::isEntityAlive));
			reg.accept("query.is_baby", ofBool(le::isChild));
			reg.accept("query.is_using_item", ofBool(le::isHandActive));
			reg.accept("query.query.is_wall_climbing", ofBool(le::isOnLadder));
			reg.accept("query.item_in_use_duration", () -> (le.getItemInUseCount() + q.partialTicks) / 20.0);
//			reg.accept("query.scale", le::getScale);
			reg.accept("query.item_remaining_use_duration", () -> Math.max(0, (le.getItemInUseMaxCount() - le.getItemInUseCount()) - q.partialTicks) / 20.0);
//			reg.accept("query.sleep_rotation", () -> le.getBedOrientation() != null ? (double) le.getBedOrientation().toYRot() : 0.0);
			reg.accept("query.head_y_rotation", () -> getHeadYaw(le, q.partialTicks));
		} else
		{
//			reg.accept("query.body_x_rotation", () -> entity.getViewXRot(partialTicks));
//			reg.accept("query.body_y_rotation", () -> entity.getViewYRot(partialTicks));
		}
		
		if(entity instanceof EntityLiving)
		{
			EntityLiving mob = (EntityLiving) entity;
			reg.accept("query.is_leashed", ofBool(mob::getLeashed));
			reg.accept("query.has_target", ofBool(() -> mob.getAttackTarget() != null));
//			reg.accept("query.can_climb", ofBool(() -> !mob.isNoAi() && mob.getNavigation() instanceof WallClimberNavigation));
//			reg.accept("query.can_fly", ofBool(() -> !mob.isNoAi() && mob.getNavigation() instanceof FlyingPathNavigation));
//			reg.accept("query.can_swim", ofBool(() -> !mob.isNoAi() && (mob.getNavigation() instanceof WaterBoundPathNavigation || mob.getNavigation() instanceof AmphibiousPathNavigation)));
//			reg.accept("query.can_walk", ofBool(() -> !mob.isNoAi() && (mob.getNavigation() instanceof GroundPathNavigation || mob.getNavigation() instanceof AmphibiousPathNavigation)));
		}
		
		if(entity instanceof EntityAnimal)
		{
			EntityAnimal a = (EntityAnimal) entity;
			reg.accept("query.is_in_love", () -> a.isInLove() ? 1D : 0D);
		}
		
		if(entity instanceof EntityWolf)
		{
			EntityWolf n = (EntityWolf) entity;
			reg.accept("query.is_angry", ofBool(n::isAngry));
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
			EntityLivingBase entitylivingbase = (EntityLivingBase) entity.getRidingEntity();
			f = interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
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
	
	private static EnumFacing getNearest(double x, double y, double z)
	{
		return EnumFacing.getFacingFromVector((float) x, (float) y, (float) z);
	}
}