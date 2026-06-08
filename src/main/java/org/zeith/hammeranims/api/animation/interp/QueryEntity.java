package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.molang.compiler.libs.MoMathLibrary;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;

import static dev.zeith.lzvm.op.ReadonlyLzVarOp.ofBool;
import static net.minecraft.client.renderer.entity.LivingEntityRenderer.isEntityUpsideDown;

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
		super(entity != null ? entity.level() : null);
		setEntity(entity);
	}
	
	public void setEntity(Entity entity)
	{
		if(entity == null) return;
		setWorld(entity.level());
		this.entity = entity;
		registerEntityVariables(this, entity, IVariableRegistrar.of(this));
	}
	
	public static void registerEntityVariables(Query q, Entity entity, IVariableRegistrar reg)
	{
		if(entity instanceof IAnimatedEntity)
			((IAnimatedEntity) entity).registerEntityProperties(q, reg);
		
		reg.registerR("query.cardinal_facing", () -> entity.getDirection().get3DDataValue());
		reg.registerR("query.cardinal_facing_2d", () ->
				{
					int directionId = entity.getDirection().get3DDataValue();
					return directionId < 2 ? 6 : directionId;
				}
		);
		reg.registerR("query.is_spectator", ofBool(entity::isSpectator));
		reg.registerR("query.is_sprinting", ofBool(entity::isSprinting));
		reg.registerR("query.is_crawling", ofBool(entity::isVisuallyCrawling));
		reg.registerR("query.is_fire_immune", ofBool(entity::fireImmune));
		reg.registerR("query.is_in_lava", ofBool(entity::isInLava));
		reg.registerR("query.is_in_water", ofBool(entity::isInWater));
		reg.registerR("query.is_on_ground", ofBool(entity::onGround));
		reg.registerR("query.invulnerable_ticks", () -> entity.invulnerableTime == 0 ? 0 : entity.invulnerableTime - q.partialTicks);
		reg.registerR("query.is_onfire", ofBool(entity::isOnFire));
		reg.registerR("query.has_gravity", ofBool(() -> !entity.isNoGravity()));
		reg.registerR("query.walk_distance", () -> MoMathLibrary.lerp(entity.walkDistO, entity.walkDist, q.partialTicks));
		reg.registerR("query.has_collision", ofBool(() -> !entity.noPhysics));
		reg.registerR("query.has_rider", ofBool(entity::isVehicle));
		reg.registerR("query.is_riding", ofBool(entity::isPassenger));
		reg.registerR("query.is_invisible", ofBool(entity::isInvisible));
		reg.registerR("query.is_silent", ofBool(entity::isSilent));
		reg.registerR("query.is_sneaking", ofBool(entity::isCrouching));
		reg.registerR("query.is_in_contact_with_water", ofBool(entity::isInWaterOrRain));
		reg.registerR("query.is_swimming", ofBool(entity::isSwimming));
		reg.registerR("query.has_player_rider", ofBool(() -> entity.hasPassenger(Player.class::isInstance)));
		reg.registerR("query.has_owner", ofBool(() -> entity instanceof OwnableEntity own && own.getOwnerUUID() != null));
		reg.registerR("query.ground_speed", () -> entity.getDeltaMovement().horizontalDistance());
		reg.registerR("query.vertical_speed", () -> entity.getDeltaMovement().y());
		reg.registerR("query.yaw_speed", () -> entity.getYRot() - entity.yRotO);
		reg.registerR("query.movement_direction", () -> entity.getDeltaMovement().horizontalDistance() > 0.001 ? getNearest(entity.getDeltaMovement()).get3DDataValue() : 6);
		reg.registerR("query.rider_body_x_rotation", () ->
				!entity.isVehicle() || entity.getFirstPassenger() == null
				? 0
				: entity.getFirstPassenger() instanceof LivingEntity
				  ? 0
				  : entity.getFirstPassenger().getViewXRot(q.partialTicks)
		);
		reg.registerR("query.rider_body_y_rotation", () ->
				!entity.isVehicle() || entity.getFirstPassenger() == null
				? 0
				: entity.getFirstPassenger() instanceof LivingEntity living
				  ? MoMathLibrary.lerp(living.yBodyRotO, living.yBodyRot, q.partialTicks)
				  : entity.getFirstPassenger().getViewYRot(q.partialTicks)
		);
		reg.registerR("query.rider_head_x_rotation", () -> entity.getFirstPassenger() instanceof LivingEntity le ? le.getViewXRot(q.partialTicks) : 0);
		reg.registerR("query.rider_head_y_rotation", () -> entity.getFirstPassenger() instanceof LivingEntity le ? le.getViewXRot(q.partialTicks) : 0);
		
		if(entity instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity) entity;
			reg.registerR("query.body_x_rotation", () -> entity.getViewXRot(q.partialTicks));
			reg.registerR("query.body_y_rotation", () -> getBodyYaw(le, q.partialTicks));
			reg.registerR("query.blocking", ofBool(le::isBlocking));
			reg.registerR("query.health", le::getHealth);
			reg.registerR("query.death_ticks", () -> le.isDeadOrDying() ? (le.deathTime + q.partialTicks) : 0);
			reg.registerR("query.hurt_time", () -> le.hurtTime == 0 ? le.hurtTime : (le.hurtTime - q.partialTicks));
			reg.registerR("query.max_health", le::getMaxHealth);
			reg.registerR("query.is_alive", ofBool(le::isAlive));
			reg.registerR("query.is_baby", ofBool(le::isBaby));
			reg.registerR("query.is_using_item", ofBool(le::isUsingItem));
			reg.registerR("query.query.is_wall_climbing", ofBool(le::onClimbable));
			reg.registerR("query.item_in_use_duration", () -> (le.getTicksUsingItem() + q.partialTicks) / 20.0);
			reg.registerR("query.scale", le::getScale);
			reg.registerR("query.item_remaining_use_duration", () -> (le.getUseItemRemainingTicks() + q.partialTicks) / 20.0);
			reg.registerR("query.sleep_rotation", () -> getBedOrientationInDegrees(le));
			reg.registerR("query.head_x_rotation", () -> Mth.lerp(q.partialTicks, le.xRotO, le.getXRot()) * (isEntityUpsideDown(le) ? -1F : 1F));
			reg.registerR("query.head_y_rotation", () -> getHeadYaw(le, q.partialTicks));
		} else
		{
			reg.registerR("query.body_x_rotation", () -> entity.getViewXRot(q.partialTicks));
			reg.registerR("query.body_y_rotation", () -> entity.getViewYRot(q.partialTicks));
		}
		
		if(entity instanceof Mob)
		{
			Mob mob = (Mob) entity;
			reg.registerR("query.is_leashed", ofBool(mob::isLeashed));
			reg.registerR("query.has_target", ofBool(() -> mob.getTarget() != null));
			reg.registerR("query.can_climb", ofBool(() -> !mob.isNoAi() && mob.getNavigation() instanceof WallClimberNavigation));
			reg.registerR("query.can_fly", ofBool(() -> !mob.isNoAi() && mob.getNavigation() instanceof FlyingPathNavigation));
			reg.registerR("query.can_swim", ofBool(() -> !mob.isNoAi() && (mob.getNavigation() instanceof WaterBoundPathNavigation || mob.getNavigation() instanceof AmphibiousPathNavigation)));
			reg.registerR("query.can_walk", ofBool(() -> !mob.isNoAi() && (mob.getNavigation() instanceof GroundPathNavigation || mob.getNavigation() instanceof AmphibiousPathNavigation)));
		}
		
		if(entity instanceof Animal)
		{
			Animal a = (Animal) entity;
			reg.registerR("query.is_in_love", () -> a.isInLove() ? 1D : 0D);
		}
		
		if(entity instanceof NeutralMob)
		{
			NeutralMob n = (NeutralMob) entity;
			reg.registerR("query.is_angry", ofBool(n::isAngry));
		}
	}
	
	public static float getHeadYaw(LivingEntity entity, float partialTicks)
	{
		return interpolateRotation(entity.yHeadRotO, entity.yHeadRot, partialTicks);
	}
	
	public static float getBodyYaw(LivingEntity entity, float partialTicks)
	{
		boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
		float f = interpolateRotation(entity.yBodyRotO, entity.yBodyRot, partialTicks);
		float f1 = interpolateRotation(entity.yHeadRotO, entity.yHeadRot, partialTicks);
		if(shouldSit && entity.getVehicle() instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity) entity.getVehicle();
			f = interpolateRotation(le.yBodyRotO, le.yBodyRot, partialTicks);
			float f3 = Mth.wrapDegrees(f1 - f);
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
	
	public static Direction getNearest(Vec3 v)
	{
		return Direction.getNearest((float) v.x, (float) v.y, (float) v.z);
	}
	
	public static float getBedOrientationInDegrees(LivingEntity entity)
	{
		var face = entity.getBedOrientation();
		if(face != null)
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
		return 0.0F;
	}
}