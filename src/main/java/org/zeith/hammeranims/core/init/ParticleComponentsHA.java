package org.zeith.hammeranims.core.init;

import org.zeith.hammeranims.api.particles.components.IParticleComponentType;
import org.zeith.hammeranims.core.impl.api.particles.components.appearance.*;
import org.zeith.hammeranims.core.impl.api.particles.components.expiration.*;
import org.zeith.hammeranims.core.impl.api.particles.components.lifetime.*;
import org.zeith.hammeranims.core.impl.api.particles.components.meta.ParcomInitialization;
import org.zeith.hammeranims.core.impl.api.particles.components.meta.ParcomLocalSpace;
import org.zeith.hammeranims.core.impl.api.particles.components.motion.*;
import org.zeith.hammeranims.core.impl.api.particles.components.rate.ParcomRateInstant;
import org.zeith.hammeranims.core.impl.api.particles.components.rate.ParcomRateSteady;
import org.zeith.hammeranims.core.impl.api.particles.components.shape.*;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

@SimplyRegister
public interface ParticleComponentsHA
{
	//<editor-fold desc="Metadata">
	@RegistryName("emitter_local_space")
	IParticleComponentType EMITTER_LOCAL_SPACE = IParticleComponentType.create(ParcomLocalSpace::new);
	
	@RegistryName("emitter_initialization")
	IParticleComponentType EMITTER_INITIALIZATION = IParticleComponentType.create(ParcomInitialization::new);
	//</editor-fold>
	
	//<editor-fold desc="Rate">
	@RegistryName("emitter_rate_instant")
	IParticleComponentType EMITTER_RATE_INSTANT = IParticleComponentType.create(ParcomRateInstant::new);
	
	@RegistryName("emitter_rate_steady")
	IParticleComponentType EMITTER_RATE_STEADY = IParticleComponentType.create(ParcomRateSteady::new);
	//</editor-fold>
	
	//<editor-fold desc="Lifetime [EMITTER]">
	@RegistryName("emitter_lifetime_looping")
	IParticleComponentType EMITTER_LIFETIME_LOOPING = IParticleComponentType.create(ParcomLifetimeLooping::new);
	
	@RegistryName("emitter_lifetime_once")
	IParticleComponentType EMITTER_LIFETIME_ONCE = IParticleComponentType.create(ParcomLifetimeOnce::new);
	
	@RegistryName("emitter_lifetime_expression")
	IParticleComponentType EMITTER_LIFETIME_EXPRESSION = IParticleComponentType.create(ParcomLifetimeExpression::new);
	//</editor-fold>
	
	//<editor-fold desc="Shapes">
	@RegistryName("emitter_shape_disc")
	IParticleComponentType EMITTER_SHAPE_DISC = IParticleComponentType.create(ParcomShapeDisc::new);
	
	@RegistryName("emitter_shape_box")
	IParticleComponentType EMITTER_SHAPE_BOX = IParticleComponentType.create(ParcomShapeBox::new);
	
	@RegistryName("emitter_shape_entity_aabb")
	IParticleComponentType EMITTER_SHAPE_ENTITY_AABB = IParticleComponentType.create(ParcomShapeEntityAABB::new);
	
	@RegistryName("emitter_shape_point")
	IParticleComponentType EMITTER_SHAPE_POINT = IParticleComponentType.create(ParcomShapePoint::new);
	
	@RegistryName("emitter_shape_sphere")
	IParticleComponentType EMITTER_SHAPE_SPHERE = IParticleComponentType.create(ParcomShapeSphere::new);
	//</editor-fold>
	
	//<editor-fold desc="Lifetime [PARTICLE]">
	@RegistryName("particle_lifetime_expression")
	IParticleComponentType PARTICLE_LIFETIME_EXPRESSION = IParticleComponentType.create(ParcomParticleLifetime::new);
	
	@RegistryName("particle_expire_if_in_blocks")
	IParticleComponentType PARTICLE_EXPIRE_IF_IN_BLOCKS = IParticleComponentType.create(ParcomExpireInBlocks::new);
	
	@RegistryName("particle_expire_if_not_in_blocks")
	IParticleComponentType PARTICLE_EXPIRE_IF_NOT_IN_BLOCKS = IParticleComponentType.create(ParcomExpireNotInBlocks::new);
	
	@RegistryName("particle_kill_plane")
	IParticleComponentType PARTICLE_KILL_PLANE = IParticleComponentType.create(ParcomKillPlane::new);
	//</editor-fold>
	
	//<editor-fold desc="Appearance">
	@RegistryName("particle_appearance_billboard")
	IParticleComponentType PARTICLE_APPEARANCE_BILLBOARD = IParticleComponentType.create(ParcomAppearanceBillboard::new);
	
	@RegistryName("particle_appearance_lighting")
	IParticleComponentType PARTICLE_APPEARANCE_LIGHTING = IParticleComponentType.builder()
			.deserializer(ParcomAppearanceLighting::new)
			.canBeEmpty(true)
			.build();
	
	@RegistryName("particle_appearance_tinting")
	IParticleComponentType PARTICLE_APPEARANCE_TINTING = IParticleComponentType.create(ParcomAppearanceTinting::new);
	
	@RegistryName("particle_collision_appearance")
	IParticleComponentType PARTICLE_COLLISION_APPEARANCE = IParticleComponentType.create(ParcomCollisionAppearance::new);
	
	@RegistryName("particle_collision_tinting")
	IParticleComponentType PARTICLE_COLLISION_TINTING = IParticleComponentType.create(ParcomCollisionTinting::new);
	//</editor-fold>
	
	//<editor-fold desc="Dynamics">
	@RegistryName("particle_initial_speed")
	IParticleComponentType PARTICLE_INITIAL_SPEED = IParticleComponentType.builder()
			.deserializer(ParcomInitialSpeed::new)
			.canBeEmpty(true)
			.build();
	
	@RegistryName("particle_initial_spin")
	IParticleComponentType PARTICLE_INITIAL_SPIN = IParticleComponentType.create(ParcomInitialSpin::new);
	
	@RegistryName("particle_motion_collision")
	IParticleComponentType PARTICLE_MOTION_COLLISION = IParticleComponentType.create(ParcomMotionCollision::new);
	
	@RegistryName("particle_motion_dynamic")
	IParticleComponentType PARTICLE_MOTION_DYNAMIC = IParticleComponentType.create(ParcomMotionDynamic::new);

	@RegistryName("particle_motion_parametric")
	IParticleComponentType PARTICLE_MOTION_PARAMETRIC = IParticleComponentType.create(ParcomMotionParametric::new);
	//</editor-fold>
}