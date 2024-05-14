package org.zeith.hammeranims.api.particles;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.core.client.particle.ParticleRenderTypes;

import java.util.function.Function;
import java.util.function.Supplier;

public enum ParticleMaterial
{
	OPAQUE("particles_opaque", () -> ParticleRenderTypes::particleSolid),
	ALPHA("particles_alpha", () -> ParticleRenderTypes::particleCutout),
	BLEND("particles_blend", () -> ParticleRenderTypes::particleTranslucent),
	ADDITIVE("particles_add", () -> ParticleRenderTypes::particleAdditive);
	
	public final String id;
	public final Supplier<Function<ResourceLocation, RenderType>> renderType;
	
	public static ParticleMaterial fromString(String material)
	{
		for(ParticleMaterial mat : values())
		{
			if(mat.id.equals(material))
			{
				return mat;
			}
		}
		
		return OPAQUE;
	}
	
	ParticleMaterial(String id, Supplier<Function<ResourceLocation, RenderType>> renderType)
	{
		this.id = id;
		this.renderType = renderType;
	}
}