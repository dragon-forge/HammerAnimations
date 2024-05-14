package org.zeith.hammeranims.core.client.particle;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class ParticleRenderTypes
		extends RenderState
{
	public static RenderType particleSolid(ResourceLocation texture)
	{
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(texture, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setDiffuseLightingState(DIFFUSE_LIGHTING)
				.setLightmapState(LIGHTMAP)
				.createCompositeState(true);
		
		return RenderType.create("particle_solid", DefaultVertexFormats.PARTICLE, 7, 256, true, false, state);
	}
	
	public static RenderType particleCutout(ResourceLocation texture, boolean outline)
	{
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(texture, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setDiffuseLightingState(DIFFUSE_LIGHTING)
				.setAlphaState(DEFAULT_ALPHA)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.createCompositeState(outline);
		return RenderType.create("particle_cutout_no_cull", DefaultVertexFormats.PARTICLE, 7, 256, true, false, state);
	}
	
	public static RenderType particleCutout(ResourceLocation texture)
	{
		return particleCutout(texture, true);
	}
	
	public static RenderType particleTranslucent(ResourceLocation texture, boolean outline)
	{
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setDiffuseLightingState(DIFFUSE_LIGHTING)
				.setAlphaState(DEFAULT_ALPHA)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.createCompositeState(outline);
		return RenderType.create("particle_translucent", DefaultVertexFormats.PARTICLE, 7, 256, true, true, state);
	}
	
	public static RenderType particleTranslucent(ResourceLocation texture)
	{
		return particleTranslucent(texture, true);
	}
	
	public static RenderType particleAdditive(ResourceLocation texture, boolean outline)
	{
		RenderType.State state = RenderType.State.builder()
				.setTextureState(new RenderState.TextureState(texture, false, false))
				.setTransparencyState(ADDITIVE_TRANSPARENCY)
				.setDiffuseLightingState(DIFFUSE_LIGHTING)
				.setAlphaState(DEFAULT_ALPHA)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.createCompositeState(outline);
		return RenderType.create("particle_translucent_additive", DefaultVertexFormats.PARTICLE, 7, 256, true, true, state);
	}
	
	public static RenderType particleAdditive(ResourceLocation texture)
	{
		return particleAdditive(texture, true);
	}
	
	private ParticleRenderTypes(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_)
	{
		super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
	}
}