package org.zeith.hammeranims.core.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import org.zeith.hammeranims.api.particles.ParticleMaterial;

public class ParticleMaterialRenderType
		implements ParticleRenderType
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
	}
	
	@Override
	public void end(Tesselator pTesselator)
	{
	}
	
	@Override
	public String toString()
	{
		return "HammerAnimations_ParticleMaterialRenderType_" + material.name();
	}
}