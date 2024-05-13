package org.zeith.hammeranims.core.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

import java.util.Locale;

public class ParticleWithEmitter
		extends Particle
{
	protected final ParticleEmitter emitter;
	protected final String typeId;
	
	public ParticleWithEmitter(ClientLevel worldIn, double posXIn, double posYIn, double posZIn, IParticleContainer container)
	{
		super(worldIn, posXIn, posYIn, posZIn);
		this.emitter = createEmitter(container);
		this.typeId = "BEDROCK_PARTICLE_" + emitter.effect.container.getRegistryKey().toString().replace(':', '_').toUpperCase(Locale.ROOT);
		
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
		this.typeId = "BEDROCK_PARTICLE_" + emitter.effect.container.getRegistryKey().toString().replace(':', '_').toUpperCase(Locale.ROOT);
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
		emitter.prevGlobal.set(emitter.lastGlobal);
		emitter.lastGlobal.set(x, y, z);
		
		emitter.update();
		if(emitter.lifetime >= 0 && emitter.age >= emitter.lifetime)
			emitter.running = false;
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
		if(emitter.isFinished()) return;
		
		var buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		var pose = new PoseStack();
		
		var vec = pRenderInfo.getPosition();
		pose.translate(vec.x, vec.y, vec.z);
		
		emitter.render(buffers, pose, pPartialTicks);
		buffers.endBatch();
	}
	
	ParticleRenderType RENDER_TYPE = new ParticleRenderType()
	{
		@Override
		public void begin(BufferBuilder pBuilder, TextureManager pTextureManager)
		{
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.depthMask(true);
		}
		
		@Override
		public void end(Tesselator tesselator)
		{
		}
		
		@Override
		public String toString()
		{
			return typeId;
		}
		
		@Override
		public int hashCode()
		{
			return typeId.hashCode();
		}
	};
	
	@Override
	public ParticleRenderType getRenderType()
	{
		return RENDER_TYPE;
	}
}