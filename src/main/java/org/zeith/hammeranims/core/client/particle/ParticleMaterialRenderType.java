package org.zeith.hammeranims.core.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import org.zeith.hammeranims.api.particles.ParticleMaterial;

public class ParticleMaterialRenderType
		implements IParticleRenderType
{
	public static final ParticleMaterialRenderType[] BUILTIN = {
			new ParticleMaterialRenderType(ParticleMaterial.OPAQUE),
			new ParticleMaterialRenderType(ParticleMaterial.ALPHA),
			new ParticleMaterialRenderType(ParticleMaterial.BLEND),
			new ParticleMaterialRenderType(ParticleMaterial.ADDITIVE)
	};
	
	protected final ParticleMaterial material;
	
	public ParticleMaterialRenderType(ParticleMaterial material)
	{
		this.material = material;
	}
	
	@Override
	public void begin(BufferBuilder pBuilder, TextureManager pTextureManager)
	{
		material.beginGL();
		RenderSystem.depthMask(true);
	}
	
	@Override
	public void end(Tessellator pTesselator)
	{
		material.endGL();
	}
	
	@Override
	public String toString()
	{
		return "HammerAnimations_ParticleMaterialRenderType_" + material.name();
	}
}