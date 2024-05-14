package org.zeith.hammeranims.core.contents.particles.components.motion;

import com.google.gson.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IParticleUpdate;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.core.utils.EntityTransformationUtils;
import org.zeith.hammeranims.core.utils.EnumFacing;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ParcomMotionCollision
		implements IParticleUpdate
{
	public InterpolatedDouble<ParticleVariables> enabled = InterpolatedDouble.one();
	public boolean preserveEnergy = false;
	public boolean entityCollision;
	public boolean momentum;
	public float collisionDrag = 0;
	public float bounciness = 1;
	public float randomBounciness = 0;
	public float randomDamp = 0;
	public float damp = 0; // should be like in Blender
	public int splitParticleCount;
	public float splitParticleSpeedThreshold; // threshold to activate the split
	public float radius = 0.01F;
	public boolean expireOnImpact;
	public InterpolatedDouble<ParticleVariables> expirationDelay = InterpolatedDouble.zero();
	public boolean realisticCollision;
	public boolean realisticCollisionDrag;
	public float rotationCollisionDrag;
	
	/* Runtime options */
	private Vector3d previous = new Vector3d();
	private Vector3d current = new Vector3d();
	private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
	
	public ParcomMotionCollision(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		
		if(element.has("enabled")) this.enabled = InterpolatedDouble.parse(element.get("enabled"));
		if(element.has("entityCollision")) this.entityCollision = element.get("entityCollision").getAsBoolean();
		if(element.has("momentum")) this.momentum = element.get("momentum").getAsBoolean();
		if(element.has("realistic_collision_drag")) this.realisticCollisionDrag = element.get("realistic_collision_drag").getAsBoolean();
		if(element.has("collision_drag")) this.collisionDrag = element.get("collision_drag").getAsFloat();
		if(element.has("coefficient_of_restitution")) this.bounciness = element.get("coefficient_of_restitution").getAsFloat();
		if(element.has("bounciness_randomness")) this.randomBounciness = element.get("bounciness_randomness").getAsFloat();
		if(element.has("collision_rotation_drag")) this.rotationCollisionDrag = element.get("collision_rotation_drag").getAsFloat();
		if(element.has("preserveEnergy") && element.get("preserveEnergy").isJsonPrimitive())
		{
			JsonPrimitive energy = element.get("preserveEnergy").getAsJsonPrimitive();
			if(energy.isBoolean()) this.preserveEnergy = energy.getAsBoolean();
			else this.preserveEnergy = energy.getAsNumber().longValue() > 0;
		}
		if(element.has("damp")) this.damp = element.get("damp").getAsFloat();
		if(element.has("random_damp")) this.randomDamp = element.get("random_damp").getAsFloat();
		if(element.has("split_particle_count")) this.splitParticleCount = element.get("split_particle_count").getAsInt();
		if(element.has("split_particle_speedThreshold")) this.splitParticleSpeedThreshold = element.get("split_particle_speedThreshold").getAsFloat();
		if(element.has("collision_radius")) this.radius = element.get("collision_radius").getAsFloat();
		if(element.has("expire_on_contact")) this.expireOnImpact = element.get("expire_on_contact").getAsBoolean();
		if(element.has("expirationDelay")) this.expirationDelay = InterpolatedDouble.parse(element.get("expirationDelay"));
		if(element.has("realisticCollision")) this.realisticCollision = element.get("realisticCollision").getAsBoolean();
	}
	
	@Override
	public void update(ParticleEmitter emitter, BedrockParticle particle)
	{
		particle.realisticCollisionDrag = this.realisticCollisionDrag;
		
		if(emitter.world == null)
		{
			return;
		}
		
		float r = this.radius;
		
		this.previous.set(particle.getGlobalPosition(emitter, particle.prevPosition));
		this.current.set(particle.getGlobalPosition(emitter));
		
		Vector3d prev = this.previous;
		Vector3d now = this.current;
		
		double x = now.x - prev.x;
		double y = now.y - prev.y;
		double z = now.z - prev.z;
		boolean veryBig = Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10;
		
		this.pos.set(now.x, now.y, now.z);
		
		if(veryBig || !emitter.world.isLoaded(this.pos))
		{
			return;
		}
		
		var aabb = new AABB(
				prev.x - r, prev.y - r, prev.z - r,
				prev.x + r, prev.y + r, prev.z + r
		);
		
		double d0 = y;
		double origX = x;
		double origZ = z;
		
		List<Entity> entities = emitter.world.getEntitiesOfClass(Entity.class, aabb.inflate(x, y, z));
		HashMap<Entity, AABB> entityAABBs = new HashMap<>();
		HashMap<Entity, CollisionOffset> staticEntityAABBs = new HashMap<>(); //for newtons first law
		
		var list = BlockPos.betweenClosedStream(aabb.inflate(x, y, z))
				.flatMap(pos ->
						emitter.world.getBlockState(pos)
								.getCollisionShape(emitter.world, pos)
								.move(pos.getX(), pos.getY(), pos.getZ())
								.toAabbs()
								.stream()
				)
				.collect(Collectors.toList());
		
		if((!list.isEmpty() || (!entities.isEmpty() && this.entityCollision)) && !particle.intersected)
		{
			particle.firstIntersection = particle.age;
			particle.intersected = true;
		}
		
		if(!particle.manual && this.enabled.get(emitter.vars) > 0)
		{
			if(this.entityCollision)
			{
				for(Entity entity : entities)
				{
					AABB aabb2 = new AABB(prev.x - r, prev.y - r, prev.z - r, prev.x + r, prev.y + r, prev.z + r);
					AABB entityAABB = entity.getBoundingBox();
					
					double y2 = y, x2 = x, z2 = z;
					
					y2 = calculateYOffset(entityAABB, aabb2, y2);
					aabb2 = aabb2.move(0.0D, y2, 0.0D);
					
					x2 = calculateXOffset(entityAABB, aabb2, x2);
					aabb2 = aabb2.move(x2, 0.0D, 0.0D);
					
					z2 = calculateZOffset(entityAABB, aabb2, z2);
					aabb2 = aabb2.move(0.0D, 0.0D, z2);
					
					if(d0 == y2 && origX == x2 && origZ == z2)
					{
						entityAABBs.put(entity, entityAABB); //Note to myself: maybe start already here with collision response?
					} else
					{
						list.add(entityAABB);
						staticEntityAABBs.put(entity, new CollisionOffset(entityAABB, x2, y2, z2));
						
						if(this.momentum && d0 == y2)
						{
							momentum(particle, entity);
						}
					}
				}
			}
			
			CollisionOffset offsetData = calculateOffsets(aabb, list, x, y, z);
			aabb = offsetData.aabb;
			x = offsetData.x;
			y = offsetData.y;
			z = offsetData.z;
			
			if(d0 != y || origX != x || origZ != z)
			{
				this.collision(particle, emitter, prev);
				
				now.set(aabb.minX + r, aabb.minY + r, aabb.minZ + r);
				
				if(d0 != y)
				{
					if(d0 < y) now.y = aabb.minY;
					else now.y = aabb.maxY;
					
					now.y += d0 < y ? r : -r;
					
					this.collisionHandler(particle, emitter, EnumFacing.Axis.Y, now, prev);
					
					/* here comes inertia */
					/* remove unecessary elements from collisionTime*/
					particle.entityCollisionTime.keySet().retainAll(staticEntityAABBs.keySet());
					
					for(HashMap.Entry<Entity, CollisionOffset> entry : staticEntityAABBs.entrySet())
					{
						CollisionOffset offsetData2 = entry.getValue();
						AABB entityAABB = offsetData2.aabb;
						Entity collidingEntity = entry.getKey();
						
						if(d0 != offsetData2.y && origX == offsetData2.x && origZ == offsetData2.z)
						{
							inertia(particle, collidingEntity, now);
						}
						
						if(particle.entityCollisionTime.containsKey(collidingEntity))
						{
							particle.entityCollisionTime.get(collidingEntity).y = particle.age;
						} else
						{
							particle.entityCollisionTime.put(entry.getKey(), new Vector3f(-1F, particle.age, -1F));
						}
					}
				}
				
				if(origX != x)
				{
					if(origX < x) now.x = aabb.minX;
					else now.x = aabb.maxX;
					
					now.x += origX < x ? r : -r;
					
					collisionHandler(particle, emitter, EnumFacing.Axis.X, now, prev);
				}
				
				if(origZ != z)
				{
					if(origZ < z) now.z = aabb.minZ;
					else now.z = aabb.maxZ;
					
					now.z += origZ < z ? r : -r;
					
					collisionHandler(particle, emitter, EnumFacing.Axis.Z, now, prev);
				}
				
				particle.position.set(now);
				
				drag(particle);
			} else if(entityAABBs.isEmpty() && this.realisticCollisionDrag) //no collision - reset collision drag
			{
				particle.dragFactor = 0;
			} else
			{
				particle.rotationCollisionDrag = 0;
			}
			
			
			for(var entry : entityAABBs.entrySet())
			{
				AABB entityAABB = entry.getValue();
				Entity entity = entry.getKey();
				
				var dMove = entity.position().subtract(entity.xo, entity.yo, entity.zo);
				
				Vector3f speedEntity = new Vector3f((float) dMove.x, (float) dMove.y, (float) dMove.z);
				Vector3f ray;
				
				if(speedEntity.x != 0 || speedEntity.y != 0 || speedEntity.z != 0)
				{
					ray = speedEntity;
				} else
				{
					/* fixes the issue of particles falling through the entity
					 * when they lie on the surface while the hitbox changes
					 * downside: the position is not always accurate depending on the movement*/

                    /*Vector3f particleMotion = new Vector3f();
                    particleMotion.x = (float) (particle.prevPosition.x - particle.position.x);
                    particleMotion.y = (float) (particle.prevPosition.y - particle.position.y);
                    particleMotion.z = (float) (particle.prevPosition.z - particle.position.z);
                    ray = particleMotion;*/
					continue;
				}
				
				Vector3d frac = intersect(ray, particle.getGlobalPosition(emitter), entityAABB);
				
				if(frac != null)
				{
					particle.position.add(frac);
					
					AABB aabb2 = new AABB(particle.position.x - r, particle.position.y - r, particle.position.z - r, particle.position.x + r, particle.position.y + r, particle.position.z + r);
					
					collision(particle, emitter, prev);
					
					if((aabb2.minX < entityAABB.maxX && aabb2.maxX > entityAABB.maxX) || (aabb2.maxX > entityAABB.minX && aabb2.minX < entityAABB.minX))
					{
						entityCollision(particle, emitter, entity, EnumFacing.Axis.X, prev);
					}
					
					if((aabb2.minY < entityAABB.maxY && aabb2.maxY > entityAABB.maxY) || (aabb2.maxY > entityAABB.minY && aabb2.minY < entityAABB.minY))
					{
						entityCollision(particle, emitter, entity, EnumFacing.Axis.Y, prev);
					}
					
					if((aabb2.minZ < entityAABB.maxZ && aabb2.maxZ > entityAABB.maxZ) || (aabb2.maxZ > entityAABB.minZ && aabb2.minZ < entityAABB.minZ))
					{
						entityCollision(particle, emitter, entity, EnumFacing.Axis.Z, prev);
					}
				}
			}
			
			if(!entityAABBs.isEmpty())
			{
				this.drag(particle);
			}
		}
	}
	
	public void collision(BedrockParticle particle, ParticleEmitter emitter, Vector3d prev)
	{
		if(this.expireOnImpact)
		{
			double expirationDelay = this.expirationDelay.get(emitter.vars);
			
			if(expirationDelay != 0 && !particle.collided)
			{
				particle.setExpirationDelay(expirationDelay);
			} else if(expirationDelay == 0 && !particle.collided)
			{
				particle.dead = true;
				
				return;
			}
		}
		
		if(particle.relativePosition)
		{
			particle.relativePosition = false;
			particle.prevPosition.set(prev);
		}
		
		particle.rotationCollisionDrag = this.rotationCollisionDrag;
		particle.collided = true;
	}
	
	public void entityCollision(BedrockParticle particle, ParticleEmitter emitter, Entity entity, EnumFacing.Axis component, Vector3d prev)
	{
		var ePos = entity.position();
		var dMove = ePos.subtract(entity.xo, entity.yo, entity.zo);
		Vector3f entitySpeed = new Vector3f((float) dMove.x, (float) dMove.y, (float) dMove.z);
		Vector3d entityPosition = new Vector3d(ePos.x, ePos.y, ePos.z);
		
		if(this.momentum)
		{
			momentum(particle, entity);
		}
		
		/* collisionTime should be not changed - otherwise the particles will stop when moving against moving entites */
		float tmpTime = getComponent(particle.collisionTime, component);
		double delta = getComponent(particle.position, component) - getComponent(entityPosition, component);
		
		setComponent(particle.position, component, getComponent(particle.position, component) + (delta > 0 ? this.radius : -this.radius));
		
		collisionHandler(particle, emitter, component, particle.position, prev);
		
		/* collisionTime should not change or otherwise particles will lose their speed although they should be reflected */
		setComponent(particle.collisionTime, component, tmpTime);
		
		if(delta > 0 && component == EnumFacing.Axis.Y) //particle is above
		{
			inertia(particle, entity, null);
		}
		
		/* particle speed is always switched (realistcCollision==true), as it always collides with the entity, but it should only have one correct direction */
		if(getComponent(particle.speed, component) > 0)
		{
			if(getComponent(entitySpeed, component) < 0) negateComponent(particle.speed, component);
		} else if(getComponent(particle.speed, component) < 0)
		{
			if(getComponent(entitySpeed, component) > 0) negateComponent(particle.speed, component);
		}
		
		/* otherwise particles would stick on the body and get reflected when entity stops */
		/* note to myself: when particle lies on top and you fly up it floats weirdly - need to redo this system a little bit*/
		setComponent(particle.position, component, getComponent(particle.position, component) + getComponent(particle.speed, component) / 20F);
	}
	
	public void collisionHandler(BedrockParticle particle, ParticleEmitter emitter, EnumFacing.Axis component, Vector3d now, Vector3d prev)
	{
		float collisionTime = getComponent(particle.collisionTime, component);
		float speed = getComponent(particle.speed, component);
		float accelerationFactor = getComponent(particle.accelerationFactor, component);
		
		/* realistic collision */
		if(this.realisticCollision)
		{
			if(collisionTime != (particle.age - 1))
			{
				if(this.bounciness != 0)
				{
					setComponent(particle.speed, component, -speed * this.bounciness);
				}
			} else if(collisionTime == (particle.age - 1))
			{
				setComponent(particle.speed, component, 0); //particle laid on that surface since last tick
			}
		} else
		{
			setComponent(particle.accelerationFactor, component, accelerationFactor * -this.bounciness);
		}
		
		if(collisionTime != (particle.age - 1))
		{
			/* random bounciness */
			if(this.randomBounciness != 0 /* && Math.round(particle.speed.x) != 0 */)
			{
				particle.speed = this.randomBounciness(particle.speed, component, this.randomBounciness);
			}
			
			/* split particles */
			if(this.splitParticleCount != 0)
			{
				this.splitParticle(particle, emitter, component, now, prev);
			}
			
			/* damping */
			if(damp != 0)
			{
				particle.speed = this.damping(particle.speed);
			}
		}
		
		if(collisionTime != particle.age - 1)
		{
			particle.bounces++;
		}
		
		setComponent(particle.collisionTime, component, particle.age);
	}
	
	public void inertia(BedrockParticle particle, Entity entity, @Nullable Vector3d now)
	{
		if(this.collisionDrag == 0)
		{
			return;
		}
		
		var ePos = entity.position();
		var dMove = ePos.subtract(entity.xo, entity.yo, entity.zo);
		Vector3f entitySpeed = new Vector3f((float) dMove.x, (float) dMove.y, (float) dMove.z);
		
		double prevPrevPosX = EntityTransformationUtils.getPrevPrevPosX(entity);
		double prevPrevPosY = EntityTransformationUtils.getPrevPrevPosY(entity);
		double prevPrevPosZ = EntityTransformationUtils.getPrevPrevPosZ(entity);
		
		Vector3d prevEntitySpeed = new Vector3d(entity.xo - prevPrevPosX, entity.yo - prevPrevPosY, entity.zo - prevPrevPosZ);

        /*if (Math.round((prevEntitySpeed.x-entitySpeed.x)*1000D) != 0 || Math.round((prevEntitySpeed.y-entitySpeed.y)*1000D) != 0 || Math.round((prevEntitySpeed.z-entitySpeed.z)*1000D) != 0)
        {
            particle.dragFactor = 0;
        }*/
		
		/* for first collision from the inertial system of the particle it is acceleration from zero to current velocity */
		if(!particle.entityCollisionTime.containsKey(entity))
		{
			prevEntitySpeed.mul(0);
		} else
		{
			/* stick the particle on top of the entity */
			particle.offset.x = entitySpeed.x;
			particle.offset.z = entitySpeed.z;
			
			if(now == null)
			{
				particle.position.x += entitySpeed.x;
				particle.position.z += entitySpeed.z;
			} else
			{
				now.x += entitySpeed.x;
				now.z += entitySpeed.z;
			}
		}
		
		particle.speed.x += Math.round((prevEntitySpeed.x - entitySpeed.x) * 1000D) / 250D; //scale it up so it gets more noticable
		particle.speed.y += Math.round((prevEntitySpeed.y - entitySpeed.y) * 1000D) / 250D;
		particle.speed.z += Math.round((prevEntitySpeed.z - entitySpeed.z) * 1000D) / 250D;
	}
	
	public void momentum(BedrockParticle particle, Entity entity)
	{
		var ePos = entity.position();
		var dMove = ePos.subtract(entity.xo, entity.yo, entity.zo);
		Vector3f entitySpeed = new Vector3f((float) dMove.x, (float) dMove.y, (float) dMove.z);
		
		particle.speed.x += 2 * entitySpeed.x;
		particle.speed.y += 2 * entitySpeed.y;
		particle.speed.z += 2 * entitySpeed.z;
	}
	
	public void drag(BedrockParticle particle)
	{
		/* only apply drag when speed is almost not zero and randombounciness and realisticCollision are off
		 * prevent particles from accelerating away when randomBounciness is active */
		if(!((this.randomBounciness != 0 || this.realisticCollision) && Math.round(particle.speed.x * 10000) == 0 && Math.round(particle.speed.y * 10000) == 0 && Math.round(particle.speed.z * 10000) == 0))
		{
			particle.dragFactor = this.collisionDrag;
            /*if (this.realisticCollisionDrag)
            {
                //TODO WTF IS THIS
                particle.dragFactor = 3*this.collisionDrag;
            }
            else
            {
                //why is it adding it on top of the old drag?
                particle.dragFactor += this.collisionDrag;
            }*/
		}
	}
	
	public Vector3f damping(Vector3f vector)
	{
		float random = (float) (this.randomDamp * (Math.random() * 2 - 1));
		return vector.mul(org.zeith.hammeranims.joml.Math.clamp(0, 1, (1 - this.damp) + random));
	}
	
	public void splitParticle(BedrockParticle particle, ParticleEmitter emitter, EnumFacing.Axis component, Vector3d now, Vector3d prev)
	{
		float speed = getComponent(particle.speed, component);
		
		if(!(Math.abs(speed) > Math.abs(this.splitParticleSpeedThreshold)))
		{
			return;
		}
		
		for(int i = 0; i < this.splitParticleCount; i++)
		{
			BedrockParticle splitParticle = emitter.createParticle(false);
			
			particle.softCopy(splitParticle);
			
			splitParticle.position.set(now);
			splitParticle.prevPosition.set(prev);
			
			splitParticle.bounces = 1;
			
			double splitPosition = getComponent(splitParticle.position, component);
			
			setComponent(splitParticle.collisionTime, component, particle.age);
			setComponent(splitParticle.position, component, splitPosition/* + ((orig < offset) ? this.radius : -this.radius)*/);
			
			Vector3f randomSpeed = this.randomBounciness(particle.speed, component, (this.randomBounciness != 0) ? this.randomBounciness : 10);
			
			randomSpeed.mul(1.0f / this.splitParticleCount);
			splitParticle.speed.set(randomSpeed);
			
			if(this.damp != 0)
			{
				splitParticle.speed = this.damping(splitParticle.speed);
			}
			
			emitter.splitParticles.add(splitParticle);
		}
		
		particle.dead = true;
	}
	
	public Vector3f randomBounciness(Vector3f vector0, EnumFacing.Axis component, float randomness)
	{
		if(randomness != 0)
		{
			/* don't change the vector0 - pointer behaviour not wanted here */
			Vector3f vector = new Vector3f(vector0);
			/* scale down the vector components not involved in the collision reflection */
			float randomfactor = 0.25F;
			float prevLength = vector.length();
			randomness *= 0.1F;
			float random1 = (float) Math.random() * randomness;
			float random2 = (float) (randomness * randomfactor * (Math.random() * 2 - 1));
			float random3 = (float) (randomness * randomfactor * (Math.random() * 2 - 1));
			
			float vectorValue = getComponent(vector, component);
			
			if(component == EnumFacing.Axis.X)
			{
				vector.y += random2;
				vector.z += random3;
			} else if(component == EnumFacing.Axis.Y)
			{
				vector.x += random2;
				vector.z += random3;
			} else
			{
				vector.y += random2;
				vector.x += random3;
			}
			
			if(this.bounciness != 0)
			{
				setComponent(vector, component, vectorValue + ((vectorValue < 0) ? -random1 : random1));
				vector.mul(prevLength / vector.length()); //scale back to original length
			} else if(vector.x != 0 || vector.y != 0 || vector.z != 0)
			{
				/* if bounciness=0 then the speed of a specific component wont't affect the particles movement
				 * so the particles speed needs to be scaled back without taking that component into account
				 * when bounciness=0 the energy of that component gets absorbed by the collision block and therefore is lost for the particle
				 */
				if(this.preserveEnergy)
				{
					setComponent(vector, component, 0);
				}
				
				/* if the vector is now zero... don't execute 1/vector.length() -> 1/0 not possible */
				if(vector.x != 0 || vector.y != 0 || vector.z != 0)
				{
					vector.mul(prevLength / vector.length());
				}
				
				setComponent(vector, component, vectorValue);
			} else /* bounciness == 0 and vector is zero (rare case, but not impossible) */
			{
				/* if you don't want particles to stop, while others randomly slide away,
				 * when bounciness==0, then return vector0 */
				return vector0;
			}
			
			return vector;
		}
		
		return vector0;
	}
	
	public Vector3d intersect(Vector3f ray, Vector3d orig, AABB aabb)
	{
		double tmin = (aabb.minX - orig.x) / ray.x;
		double tmax = (aabb.maxX - orig.x) / ray.x;
		
		if(tmin > tmax)
		{
			double tminTmp = tmin;
			tmin = tmax;
			tmax = tminTmp;
		}
		
		double tymin = (aabb.minY - orig.y) / ray.y;
		double tymax = (aabb.maxY - orig.y) / ray.y;
		
		if(tymin > tymax)
		{
			double tyminTmp = tymin;
			tymin = tymax;
			tymax = tyminTmp;
		}
		
		if(tmin > tymax || tymin > tmax)
			return null;
		
		if(tymin > tmin)
			tmin = tymin;
		
		if(tymax < tmax)
			tmax = tymax;
		
		double tzmin = (aabb.minZ - orig.z) / ray.z;
		double tzmax = (aabb.maxZ - orig.z) / ray.z;
		
		if(tzmin > tzmax)
		{
			double tzminTmp = tzmin;
			tzmin = tzmax;
			tzmax = tzminTmp;
		}
		
		if(tmin > tzmax || tzmin > tmax)
			return null;
		
		if(tzmax < tmax)
			tmax = tzmax;
		
		Vector3d ray1 = new Vector3d(ray);
		
		ray1.mul(tmax);
		
		return ray1;
	}
	
	/**
	 * @param aabb
	 * 		AxisAlignedBoundingBox of the main aabb
	 * @param list
	 * 		List of AxisAlignedBoundingBoxs of the targets
	 * @param x
	 * 		origin
	 * @param y
	 * 		origin
	 * @param z
	 * 		origin
	 *
	 * @return CollisionOffset which includes aabb, x, y, z
	 */
	public CollisionOffset calculateOffsets(AABB aabb, List<AABB> list, double x, double y, double z)
	{
		for(AABB AABB : list)
		{
			y = calculateYOffset(AABB, aabb, y);
		}
		
		aabb = aabb.move(0.0D, y, 0.0D);
		
		for(AABB AABB1 : list)
		{
			x = calculateXOffset(AABB1, aabb, x);
		}
		
		aabb = aabb.move(x, 0.0D, 0.0D);
		
		for(AABB AABB2 : list)
		{
			z = calculateZOffset(AABB2, aabb, z);
		}
		
		aabb = aabb.move(0.0D, 0.0D, z);
		
		return new CollisionOffset(aabb, x, y, z);
	}
	
	@Override
	public int getSortingIndex()
	{
		return 50;
	}
	
	public static class CollisionOffset
	{
		public AABB aabb;
		public double x;
		public double y;
		public double z;
		
		public CollisionOffset(AABB aabb, double x, double y, double z)
		{
			this.aabb = aabb;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	public static float getComponent(Vector3f vector, EnumFacing.Axis component)
	{
		if(component == EnumFacing.Axis.X)
		{
			return vector.x;
		} else if(component == EnumFacing.Axis.Y)
		{
			return vector.y;
		}
		
		return vector.z;
	}
	
	public static void setComponent(Vector3f vector, EnumFacing.Axis component, float value)
	{
		if(component == EnumFacing.Axis.X)
		{
			vector.x = value;
		} else if(component == EnumFacing.Axis.Y)
		{
			vector.y = value;
		} else
		{
			vector.z = value;
		}
	}
	
	public static void negateComponent(Vector3f vector, EnumFacing.Axis component)
	{
		setComponent(vector, component, -getComponent(vector, component));
	}
	
	public static double getComponent(Vector3d vector, EnumFacing.Axis component)
	{
		if(component == EnumFacing.Axis.X)
		{
			return vector.x;
		} else if(component == EnumFacing.Axis.Y)
		{
			return vector.y;
		}
		
		return vector.z;
	}
	
	public static void setComponent(Vector3d vector, EnumFacing.Axis component, double value)
	{
		if(component == EnumFacing.Axis.X)
		{
			vector.x = value;
		} else if(component == EnumFacing.Axis.Y)
		{
			vector.y = value;
		} else
		{
			vector.z = value;
		}
	}
	
	public static void negateComponent(Vector3d vector, EnumFacing.Axis component)
	{
		setComponent(vector, component, -getComponent(vector, component));
	}
	
	
	public static double calculateXOffset(AABB thiz, AABB other, double offsetX)
	{
		if(other.maxY > thiz.minY && other.minY < thiz.maxY && other.maxZ > thiz.minZ && other.minZ < thiz.maxZ)
		{
			if(offsetX > 0.0D && other.maxX <= thiz.minX)
			{
				double d1 = thiz.minX - other.maxX;
				
				if(d1 < offsetX)
				{
					offsetX = d1;
				}
			} else if(offsetX < 0.0D && other.minX >= thiz.maxX)
			{
				double d0 = thiz.maxX - other.minX;
				
				if(d0 > offsetX)
				{
					offsetX = d0;
				}
			}
			
			return offsetX;
		} else
		{
			return offsetX;
		}
	}
	
	public static double calculateYOffset(AABB thiz, AABB other, double offsetY)
	{
		if(other.maxX > thiz.minX && other.minX < thiz.maxX && other.maxZ > thiz.minZ && other.minZ < thiz.maxZ)
		{
			if(offsetY > 0.0D && other.maxY <= thiz.minY)
			{
				double d1 = thiz.minY - other.maxY;
				
				if(d1 < offsetY)
				{
					offsetY = d1;
				}
			} else if(offsetY < 0.0D && other.minY >= thiz.maxY)
			{
				double d0 = thiz.maxY - other.minY;
				
				if(d0 > offsetY)
				{
					offsetY = d0;
				}
			}
			
			return offsetY;
		} else
		{
			return offsetY;
		}
	}
	
	public static double calculateZOffset(AABB thiz, AABB other, double offsetZ)
	{
		if(other.maxX > thiz.minX && other.minX < thiz.maxX && other.maxY > thiz.minY && other.minY < thiz.maxY)
		{
			if(offsetZ > 0.0D && other.maxZ <= thiz.minZ)
			{
				double d1 = thiz.minZ - other.maxZ;
				
				if(d1 < offsetZ)
				{
					offsetZ = d1;
				}
			} else if(offsetZ < 0.0D && other.minZ >= thiz.maxZ)
			{
				double d0 = thiz.maxZ - other.minZ;
				
				if(d0 > offsetZ)
				{
					offsetZ = d0;
				}
			}
			
			return offsetZ;
		} else
		{
			return offsetZ;
		}
	}
}