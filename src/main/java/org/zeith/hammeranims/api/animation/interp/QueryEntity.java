package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.molang.compiler.libs.MoMathLibrary;
import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;

import java.util.function.BiConsumer;

import static dev.zeith.lzvm.op.ReadonlyLzVarOp.ofBool;

/**
 * This is an extensible class (this gets passed to animation layers)
 */
public class QueryEntity
		extends QueryWorld
{
	protected final Entity entity;
	
	public QueryEntity(Entity entity)
	{
		super(entity.level());
		this.entity = entity;
		registerEntityVariables(this::setVariable);
	}
	
	protected void registerEntityVariables(BiConsumer<String, ReadonlyLzVarOp> reg)
	{
		if(entity instanceof IAnimatedEntity)
			((IAnimatedEntity) entity).registerEntityProperties(reg);
		
		reg.accept("query.cardinal_facing", () -> entity.getDirection().get3DDataValue());
		reg.accept("query.cardinal_facing_2d", () ->
				{
					int directionId = entity.getDirection().get3DDataValue();
					return directionId < 2 ? 6 : directionId;
				}
		);
		reg.accept("query.is_spectator", ofBool(entity::isSpectator));
		reg.accept("query.is_sprinting", ofBool(entity::isSprinting));
		reg.accept("query.is_crawling", ofBool(entity::isVisuallyCrawling));
		reg.accept("query.is_fire_immune", ofBool(entity::fireImmune));
		reg.accept("query.is_in_lava", ofBool(entity::isInLava));
		reg.accept("query.is_in_water", ofBool(entity::isInWater));
		reg.accept("query.is_on_ground", ofBool(entity::onGround));
		reg.accept("query.invulnerable_ticks", () -> entity.invulnerableTime == 0 ? 0 : entity.invulnerableTime - partialTicks);
		reg.accept("query.is_onfire", ofBool(entity::isOnFire));
		reg.accept("query.has_gravity", ofBool(() -> !entity.isNoGravity()));
		reg.accept("query.walk_distance", () -> MoMathLibrary.lerp(entity.walkDistO, entity.walkDist, partialTicks));
		reg.accept("query.has_collision", ofBool(() -> !entity.noPhysics));
		reg.accept("query.has_rider", ofBool(entity::isVehicle));
		reg.accept("query.is_riding", ofBool(entity::isPassenger));
		reg.accept("query.is_invisible", ofBool(entity::isInvisible));
		reg.accept("query.is_silent", ofBool(entity::isSilent));
		reg.accept("query.is_sneaking", ofBool(entity::isCrouching));
		reg.accept("query.is_in_contact_with_water", ofBool(entity::isInWaterOrRain));
		reg.accept("query.is_swimming", ofBool(entity::isSwimming));
		reg.accept("query.has_player_rider", ofBool(() -> entity.hasPassenger(Player.class::isInstance)));
		reg.accept("query.has_owner", ofBool(() -> entity instanceof OwnableEntity own && own.getOwnerUUID() != null));
		reg.accept("query.ground_speed", () -> entity.getDeltaMovement().horizontalDistance());
		reg.accept("query.vertical_speed", () -> entity.getDeltaMovement().y());
		reg.accept("query.yaw_speed", () -> entity.getYRot() - entity.yRotO);
		reg.accept("query.movement_direction", () -> entity.getDeltaMovement().horizontalDistance() > 0.001 ? getNearest(entity.getDeltaMovement()).get3DDataValue() : 6);
		reg.accept("query.rider_body_x_rotation", () ->
				!entity.isVehicle() || entity.getFirstPassenger() == null
				? 0
				: entity.getFirstPassenger() instanceof LivingEntity
				  ? 0
				  : entity.getFirstPassenger().getViewXRot(partialTicks)
		);
		reg.accept("query.rider_body_y_rotation", () ->
				!entity.isVehicle() || entity.getFirstPassenger() == null
				? 0
				: entity.getFirstPassenger() instanceof LivingEntity living
				  ? MoMathLibrary.lerp(living.yBodyRotO, living.yBodyRot, partialTicks)
				  : entity.getFirstPassenger().getViewYRot(partialTicks)
		);
		reg.accept("query.rider_head_x_rotation", () -> entity.getFirstPassenger() instanceof LivingEntity le ? le.getViewXRot(partialTicks) : 0);
		reg.accept("query.rider_head_y_rotation", () -> entity.getFirstPassenger() instanceof LivingEntity le ? le.getViewXRot(partialTicks) : 0);
		
		if(entity instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity) entity;
			reg.accept("query.body_x_rotation", () -> 0);
			reg.accept("query.body_y_rotation", () -> MoMathLibrary.lerp(le.yBodyRotO, le.yBodyRot, partialTicks));
			reg.accept("query.blocking", ofBool(le::isBlocking));
			reg.accept("query.health", le::getHealth);
			reg.accept("query.death_ticks", () -> le.isDeadOrDying() ? (le.deathTime + partialTicks) : 0);
			reg.accept("query.hurt_time", () -> le.hurtTime == 0 ? le.hurtTime : (le.hurtTime - partialTicks));
			reg.accept("query.max_health", le::getMaxHealth);
			reg.accept("query.is_alive", ofBool(le::isAlive));
			reg.accept("query.is_baby", ofBool(le::isBaby));
			reg.accept("query.is_using_item", ofBool(le::isUsingItem));
			reg.accept("query.query.is_wall_climbing", ofBool(le::onClimbable));
			reg.accept("query.item_in_use_duration", () -> (le.getTicksUsingItem() + partialTicks) / 20.0);
			reg.accept("query.scale", le::getScale);
			reg.accept("query.item_remaining_use_duration", () -> (le.getUseItemRemainingTicks() + partialTicks) / 20.0);
			reg.accept("query.sleep_rotation", () -> le.getBedOrientation() != null ? (double) le.getBedOrientation().toYRot() : 0.0);
			reg.accept("query.head_x_rotation", () -> le.getViewXRot(partialTicks));
			reg.accept("query.head_y_rotation", () -> le.getViewYRot(partialTicks));
		} else
		{
			reg.accept("query.body_x_rotation", () -> entity.getViewXRot(partialTicks));
			reg.accept("query.body_y_rotation", () -> entity.getViewYRot(partialTicks));
		}
		
		if(entity instanceof Mob)
		{
			Mob mob = (Mob) entity;
			reg.accept("query.is_leashed", ofBool(mob::isLeashed));
			reg.accept("query.has_target", ofBool(() -> mob.getTarget() != null));
			reg.accept("query.can_climb", ofBool(() -> !mob.isNoAi() && mob.getNavigation() instanceof WallClimberNavigation));
			reg.accept("query.can_fly", ofBool(() -> !mob.isNoAi() && mob.getNavigation() instanceof FlyingPathNavigation));
			reg.accept("query.can_swim", ofBool(() -> !mob.isNoAi() && (mob.getNavigation() instanceof WaterBoundPathNavigation || mob.getNavigation() instanceof AmphibiousPathNavigation)));
			reg.accept("query.can_walk", ofBool(() -> !mob.isNoAi() && (mob.getNavigation() instanceof GroundPathNavigation || mob.getNavigation() instanceof AmphibiousPathNavigation)));
		}
		
		if(entity instanceof Animal)
		{
			Animal a = (Animal) entity;
			reg.accept("query.is_in_love", () -> a.isInLove() ? 1D : 0D);
		}
		
		if(entity instanceof NeutralMob)
		{
			NeutralMob n = (NeutralMob) entity;
			reg.accept("query.is_angry", ofBool(n::isAngry));
		}
	}
	
	private static Direction getNearest(Vec3 v)
	{
		return Direction.getNearest(v.x, v.y, v.z);
	}
}