package org.zeith.hammeranims.api.particles.emitter;

import net.minecraft.world.entity.Entity;
import org.zeith.hammeranims.core.contents.particles.components.appearance.ParcomCollisionAppearance;
import org.zeith.hammeranims.core.contents.particles.components.appearance.ParcomCollisionTinting;
import org.zeith.hammeranims.core.init.ParticleComponentsHA;
import org.zeith.hammeranims.joml.*;

import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

public class BedrockParticle
{
	public final ParticleEmitter owner;
	
	/* Randoms */
	public float random1 = (float) Math.random();
	public float random2 = (float) Math.random();
	public float random3 = (float) Math.random();
	public float random4 = (float) Math.random();
	
	/* States */
	public int age;
	public int lifetime;
	public boolean dead;
	public boolean relativePosition;
	public boolean relativeRotation;
	public boolean relativeDirection;
	public boolean relativeScale;
	public boolean relativeScaleBillboard;
	public boolean relativeAcceleration;
	public boolean realisticCollisionDrag;
	public float linearVelocity;
	public float angularVelocity;
	public boolean gravity;
	public boolean manual;
	
	/* Age when the particle should expire */
	private int expireAge = -1;
	/* Used to determine lifetime when expirationDelay is on */
	private int expirationDelay = -1;
	
	/**
	 * This is used to estimate whether an object is only bouncing or lying on a surface
	 * <p>
	 * CollisionTime won't work when e.g. the particle bounces of the surface and directly in the next
	 * update cycle hits the same surface side, like from top of the block to bottom of the block...
	 * I think this probably never happens in practice
	 */
	public Vector3f collisionTime = new Vector3f(-2f, -2f, -2f);
	public HashMap<Entity, Vector3f> entityCollisionTime = new HashMap<>();
	public boolean collided;
	public int bounces;
	/**
	 * Is set by collision component when there is a collision an drag should happen.
	 */
	public float rotationCollisionDrag = 0;
	
	/**
	 * For collision Appearance needed for animation
	 */
	public int firstIntersection = -1;
	public boolean intersected;
	
	/* Rotation */
	public float rotation;
	public float initialRotation;
	public float prevRotation;
	
	public float rotationVelocity;
	public float rotationAcceleration;
	public float rotationDrag;
	
	/* for transforming into inertial systems (currently just used for inertia)*/
	public Vector3d offset = new Vector3d();
	
	/* Position */
	public Vector3d position = new Vector3d();
	public Vector3d initialPosition = new Vector3d();
	public Vector3d prevPosition = new Vector3d();
	public Matrix3d matrix = new Matrix3d();
	private boolean matrixSet;
	
	public Vector3f speed = new Vector3f();
	public Vector3f acceleration = new Vector3f();
	public Vector3f accelerationFactor = new Vector3f(1, 1, 1);
	public float drag = 0;
	
	/**
	 * Is set by collision component when there is a collision an drag should happen.
	 */
	public float dragFactor = 0;
	
	/* Color */
	public float r = 1;
	public float g = 1;
	public float b = 1;
	public float a = 1;
	
	private Vector3d global = new Vector3d();
	
	public BedrockParticle(ParticleEmitter owner)
	{
		this.owner = owner;
		this.speed.set((float) Math.random() - 0.5F, (float) Math.random() - 0.5F, (float) Math.random() - 0.5F);
		this.speed.normalize();
		this.matrix.identity();
	}
	
	public boolean isCollisionTexture(ParticleEmitter emitter)
	{
		ParcomCollisionAppearance a = emitter.effect.get(ParcomCollisionAppearance.class, ParticleComponentsHA.PARTICLE_COLLISION_APPEARANCE);
		return a != null && a.enabled.get(owner.vars) >= 1 && this.intersected;
	}
	
	public boolean isCollisionTinting(ParticleEmitter emitter)
	{
		ParcomCollisionTinting a = emitter.effect.get(ParcomCollisionTinting.class, ParticleComponentsHA.PARTICLE_COLLISION_TINTING);
		return a != null && a.enabled.get(owner.vars) >= 1 && this.intersected;
	}
	
	public int getExpireAge()
	{
		return this.expireAge;
	}
	
	public int getExpirationDelay()
	{
		return this.expirationDelay;
	}
	
	/**
	 * Copy this particle to the given particle (it does not copy fields that are initialized by components)
	 *
	 * @param to
	 * 		destiny to copy values to
	 *
	 * @return copied particle
	 */
	public BedrockParticle softCopy(BedrockParticle to)
	{
		to.age = this.age;
		to.expireAge = this.expireAge;
		to.expirationDelay = this.expirationDelay;
		to.realisticCollisionDrag = this.realisticCollisionDrag;
		to.collisionTime.set(this.collisionTime);
		to.entityCollisionTime = new HashMap<>();
		
		for(Map.Entry<Entity, Vector3f> entry : this.entityCollisionTime.entrySet())
			to.entityCollisionTime.put(entry.getKey(), new Vector3f(entry.getValue()));
		
		to.bounces = this.bounces;
		to.firstIntersection = this.firstIntersection;
		to.offset.set(this.offset);
		to.position.set(this.position);
		to.initialPosition.set(this.initialPosition);
		to.prevPosition.set(this.prevPosition);
		to.matrix.set(this.matrix);
		to.matrixSet = this.matrixSet;
		to.speed.set(this.speed);
		to.acceleration.set(this.acceleration);
		to.accelerationFactor.set(this.accelerationFactor);
		to.dragFactor = this.dragFactor;
		to.global.set(this.global);
		
		return to;
	}
	
	public double getDistanceSq(ParticleEmitter emitter)
	{
		Vector3d pos = this.getGlobalPosition(emitter);
		
		double dx = emitter.cX - pos.x;
		double dy = emitter.cY - pos.y;
		double dz = emitter.cZ - pos.z;
		
		return dx * dx + dy * dy + dz * dz;
	}
	
	public double getAge(float partialTick)
	{
		return (this.age + partialTick) / 20.0;
	}
	
	public Vector3d getGlobalPosition(ParticleEmitter emitter)
	{
		return this.getGlobalPosition(emitter, this.position);
	}
	
	public Vector3d getGlobalPosition(ParticleEmitter emitter, Vector3d vector)
	{
		double px = vector.x;
		double py = vector.y;
		double pz = vector.z;
		
		if(this.relativePosition && this.relativeRotation)
		{
			Vector3f v = new Vector3f((float) px, (float) py, (float) pz);
			emitter.rotation.transform(v);
			
			px = v.x;
			py = v.y;
			pz = v.z;
			
			px += emitter.lastGlobal.x;
			py += emitter.lastGlobal.y;
			pz += emitter.lastGlobal.z;
		}
		
		this.global.set(px, py, pz);
		
		return this.global;
	}
	
	public void update(ParticleEmitter emitter)
	{
		this.prevRotation = this.rotation;
		this.prevPosition.set(this.position);
		
		this.setupMatrix(emitter);
		
		if(!this.manual)
		{
			//this.position.add(this.offset);

            /*if (this.realisticCollisionDrag && Math.round(this.speed.x*10000) == 0 && Math.round(this.speed.y*10000) == 0 && Math.round(this.speed.z*10000) == 0)
            {
                this.dragFactor = 0;
                this.speed.scale(0);
            }*/
			
			/* lazy fix for transforming from moving intertial system back to global space */
			if(this.entityCollisionTime.isEmpty())
			{
				transformOffsetToGlobal();
			} else
			{
				for(HashMap.Entry<Entity, Vector3f> entry : this.entityCollisionTime.entrySet())
				{
					if(entry.getValue().y != this.age)
					{
						transformOffsetToGlobal();
					}
				}
			}
			
			//TODO drag force usually takes velocity^2 -> this cuts off higher velocities much quicker but takes longer with slower velocities. maybe two separate options?
			float rotationDrag = (this.rotationDrag + this.rotationCollisionDrag) * this.rotationVelocity;
			float rotationAcceleration = this.rotationAcceleration / 20F;
			/* clamp drag so it doesn't make rotation velocity explode*/
			this.rotationVelocity = org.zeith.hammeranims.joml.Math.clamp(
					Math.min(this.rotationVelocity, 0), Math.max(this.rotationVelocity, 0),
					this.rotationVelocity - rotationDrag / 20F
			);
			this.rotationVelocity += rotationAcceleration / 20F;
			this.rotation += this.rotationVelocity;
			
			/* Position */
			if(this.age == 0)
			{
				if(this.relativeDirection)
				{
					emitter.rotation.transform(this.speed);
				}
				
				if(this.linearVelocity != 0)
				{
					Vector3d v = new Vector3d(emitter.lastGlobal);
					v.x -= emitter.prevGlobal.x;
					v.y -= emitter.prevGlobal.y;
					v.z -= emitter.prevGlobal.z;
					
					this.speed.x += (float) (v.x * this.linearVelocity);
					this.speed.y += (float) (v.y * this.linearVelocity);
					this.speed.z += (float) (v.z * this.linearVelocity);
				}
				
				if(this.angularVelocity != 0)
				{
					Matrix3f rotation1 = new Matrix3f(emitter.rotation);
					Matrix3f identity = new Matrix3f();
					
					identity.identity();
					
					Matrix3f rotation0 = new Matrix3f(emitter.prevRotation);
					
					rotation0.invert();
					rotation1.mul(rotation0);
					
					Vector3d angularV = getAngularVelocity(rotation1);
					
					Vector3d radius = new Vector3d(emitter.translation);
					radius.x += this.position.x - emitter.lastGlobal.x;
					radius.y += this.position.y - emitter.lastGlobal.y;
					radius.z += this.position.z - emitter.lastGlobal.z;
					
					Vector3d v = new Vector3d();
					angularV.cross(radius, v);
					
					this.speed.x += (float) (v.x * this.angularVelocity);
					this.speed.y += (float) (v.y * this.angularVelocity);
					this.speed.z += (float) (v.z * this.angularVelocity);
				}
			}
			
			if(this.relativeAcceleration)
			{
				emitter.rotation.transform(this.acceleration);
			}
			
			if(this.gravity)
			{
				this.acceleration.y -= 9.81;
			}
			
			Vector3f drag = new Vector3f(this.speed);
			drag.mul(-(this.drag + this.dragFactor));
			/* apply drag separately so we can clamp it - high drag values shouldn't accelerate particles again */
			drag.mul(1 / 20F);
			if(this.speed.length() - drag.length() <= 0)
			{
				this.speed.mul(0);
			} else
			{
				this.speed.add(drag);
			}
			
			this.acceleration.mul(1 / 20F);
			this.speed.add(this.acceleration);
			
			Vector3f speed0 = new Vector3f(this.speed);
			speed0.x *= this.accelerationFactor.x;
			speed0.y *= this.accelerationFactor.y;
			speed0.z *= this.accelerationFactor.z;
			
			if(this.relativePosition || this.relativeRotation)
			{
				this.matrix.transform(speed0);
			}
			
			this.position.x += speed0.x / 20F;
			this.position.y += speed0.y / 20F;
			this.position.z += speed0.z / 20F;
		}
		
		this.age++;
		this.acceleration.set(0, 0, 0);
		
		if(this.lifetime >= 0 && (this.age >= this.lifetime || (this.age >= this.expireAge && this.expireAge != -1)))
		{
			this.dead = true;
		}
	}
	
	/**
	 * Sets the expirationDelay and expireAge - the smallest expire age wins. Negative expiration delays always overwrite/win.
	 */
	public void setExpirationDelay(double delay)
	{
		int expirationDelay = (int) delay;
		
		if(this.age + expirationDelay < this.expireAge || this.expireAge == -1)
		{
			this.expirationDelay = Math.abs(expirationDelay);
			this.expireAge = this.age + this.expirationDelay;
		}
	}
	
	public void setupMatrix(ParticleEmitter emitter)
	{
		if(this.relativePosition)
		{
			if(this.relativeRotation)
			{
				this.matrix.identity();
			} else if(!this.matrixSet)
			{
				this.matrix.set(emitter.rotation);
				this.matrixSet = true;
			}
		} else if(this.relativeRotation)
		{
			this.matrix.set(emitter.rotation);
		}
	}
	
	/**
	 * This method adds the offset to the speed to transform from a moving inertial system to the global space
	 * (especially for inertia)
	 */
	public void transformOffsetToGlobal()
	{
		this.offset.mul(6); //scale it up so it gets more noticeable (artistic choice)
		
		this.speed.x += this.offset.x;
		this.speed.y += this.offset.y;
		this.speed.z += this.offset.z;
		
		this.offset.mul(0);
	}
	
	public static Vector3d getAngularVelocity(Matrix3f rotation)
	{
		Matrix3f step = new Matrix3f(rotation);
		Matrix3f angularVelocity = new Matrix3f();
		Matrix3f i = new Matrix3f();
		
		i.identity();
		angularVelocity.identity();
		angularVelocity.scale(2);
		
		step.add(i);
		step.invert();
		step.scale(4);
		
		angularVelocity.sub(step);
		
		return new Vector3d(angularVelocity.m21,
				-angularVelocity.m20,
				angularVelocity.m10
		);
	}
}