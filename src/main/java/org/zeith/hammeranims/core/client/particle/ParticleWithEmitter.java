package org.zeith.hammeranims.core.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lombok.Getter;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.ParticleMaterial;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

import java.util.Locale;
import java.util.Objects;

@Getter
public class ParticleWithEmitter
		extends Particle
{
	public static int MAX_EMITTER_GENERATIONS = 6;
	
	protected final ParticleEmitter emitter;
	protected final String typeId;
	
	public ParticleWithEmitter(ClientWorld worldIn, double posXIn, double posYIn, double posZIn, IParticleContainer container)
	{
		super(worldIn, posXIn, posYIn, posZIn);
		this.emitter = createEmitter(container);
		this.typeId = "BEDROCK_PARTICLE_" + emitter.effect.container.getRegistryKey().toString().replace(':', '_').toUpperCase(Locale.ROOT);
		
		emitter.lastGlobal.set(x, y, z);
		emitter.prevGlobal.set(emitter.lastGlobal);
	}
	
	public ParticleWithEmitter(ClientWorld world, IAnimatedObject object, IParticleContainer container)
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
		if(emitter.generation >= MAX_EMITTER_GENERATIONS)
		{
			removed = true;
			return;
		}
		
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
	public void render(IVertexBuilder pBuffer, ActiveRenderInfo pRenderInfo, float pPartialTicks)
	{
		Entity viewEntity = pRenderInfo.getEntity();
		if(emitter.isFinished() || viewEntity == null) return;
		
		Vector3d vector3d = pRenderInfo.getPosition();
		
		Vector3d camPos = viewEntity.getPosition(pPartialTicks);
		
		// This is usually zero, unless player is in 3P.
		var thirdPersonDelta = camPos.subtract(vector3d);
		
		IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		MatrixStack mat = new MatrixStack();
		mat.translate(thirdPersonDelta.x, thirdPersonDelta.y, thirdPersonDelta.z); // Accounts for 3P
		emitter.render(pRenderInfo, buffers, mat, pPartialTicks);
		buffers.endBatch();
	}
	
	IParticleRenderType RENDER_TYPE = new DynamicParticleRenderType();
	
	@Override
	public IParticleRenderType getRenderType()
	{
		return RENDER_TYPE;
	}
	
	@Override
	public boolean shouldCull()
	{
		return false;
	}
	
	public void spawn()
	{
		if(emitter.generation >= MAX_EMITTER_GENERATIONS) return;
		Minecraft mc = Minecraft.getInstance();
		mc.execute(() -> mc.particleEngine.add(this));
	}
	
	private class DynamicParticleRenderType
			implements IParticleRenderType
	{
		@Override
		public void begin(BufferBuilder pBuilder, TextureManager pTextureManager)
		{
			if(emitter.effect.material == ParticleMaterial.OPAQUE)
				RenderSystem.disableBlend();
			else
			{
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
			}
			RenderSystem.depthMask(true);
		}
		
		@Override
		public void end(Tessellator pTesselator)
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
		
		public String getTypeId()
		{
			return typeId;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof DynamicParticleRenderType
				   && Objects.equals(((DynamicParticleRenderType) obj).getTypeId(), getTypeId());
		}
	}
}