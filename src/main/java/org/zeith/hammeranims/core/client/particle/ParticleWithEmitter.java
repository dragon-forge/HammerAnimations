package org.zeith.hammeranims.core.client.particle;

import com.mojang.blaze3d.vertex.*;
import lombok.Getter;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.phys.AABB;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

@Getter
public class ParticleWithEmitter
		extends Particle
{
	public static int MAX_EMITTER_GENERATIONS = 6;
	
	protected final ParticleEmitter emitter;
	
	public ParticleWithEmitter(ClientLevel worldIn, double posXIn, double posYIn, double posZIn, IParticleContainer container)
	{
		super(worldIn, posXIn, posYIn, posZIn);
		this.emitter = createEmitter(container);
		
		emitter.lastGlobal.set(x, y, z);
		emitter.prevGlobal.set(emitter.lastGlobal);
	}
	
	public ParticleWithEmitter(ClientLevel world, IAnimatedObject object, IParticleContainer container)
	{
		super(world,
				object.getAnimatedObjectPosition().x,
				object.getAnimatedObjectPosition().y,
				object.getAnimatedObjectPosition().z
		);
		this.emitter = createEmitter(container);
		emitter.setTarget(object);
		
		emitter.lastGlobal.set(x, y, z);
		emitter.prevGlobal.set(emitter.lastGlobal);
	}
	
	public ParticleEmitter createEmitter(IParticleContainer container)
	{
		ParticleEmitter e = new ParticleEmitter();
		e.setEffect(container.getParticleEffect());
		e.setWorld(level);
		return e;
	}
	
	@Override
	public void tick()
	{
		if(emitter.generation >= MAX_EMITTER_GENERATIONS)
		{
			removed = true;
			return;
		}
		
		emitter.prevGlobal.set(emitter.lastGlobal);
		emitter.lastGlobal.set(x, y, z);
		
		emitter.update();
		if(emitter.lifetime >= 0 && emitter.age >= emitter.lifetime)
		{
			emitter.running = false;
			emitter.stop();
		}
		if(emitter.isFinished())
			removed = true;
		if(emitter.target != null)
		{
//			Vec3d p = emitter.target.getAnimatedObjectPosition();
//			setPosition(p.x, p.y, p.z);
			if(emitter.target.getAnimationSource().get(level) != emitter.target)
			{
				removed = true;
			}
		}
	}
	
	@Override
	public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks)
	{
		var viewEntity = pRenderInfo.getEntity();
		if(emitter.isFinished() || viewEntity == null) return;
		
		var vector3d = pRenderInfo.getPosition();
		
		var camPos = viewEntity.getPosition(pPartialTicks);
		
		// This is usually zero, unless player is in 3P.
		var thirdPersonDelta = camPos.subtract(vector3d);
		
		var buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		var pose = new PoseStack();
		pose.translate(thirdPersonDelta.x, thirdPersonDelta.y, thirdPersonDelta.z); // Accounts for 3P
		emitter.render(pRenderInfo, buffers, pose, pPartialTicks);
		buffers.endBatch();
	}
	
	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleMaterialRenderType.BUILTIN[emitter.effect.material.ordinal()];
	}
	
	@Override
	public AABB getRenderBoundingBox(float partialTicks)
	{
		return AABB.INFINITE;
	}
	
	public void spawn()
	{
		if(emitter.generation >= MAX_EMITTER_GENERATIONS) return;
		Minecraft mc = Minecraft.getInstance();
		mc.execute(() ->
		{
			try
			{
				mc.particleEngine.add(this);
			} catch(Exception e)
			{
				HammerAnimations.LOG.error("Failed to spawn particle", e);
			}
		});
	}
}