package org.zeith.hammeranims.core.client.particle;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ParticleRenderTypes
		extends RenderStateShard
{
	protected static final ShaderStateShard RENDERTYPE_PARTICLE_SHADER = new ShaderStateShard(GameRenderer::getParticleShader);
	
	private static final Function<ResourceLocation, RenderType> PARTICLE_SOLID = Util.memoize((texture) ->
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_PARTICLE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.createCompositeState(true);
		
		return RenderType.create("particle_solid",
				DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, 256,
				true, false,
				state
		);
	});
	
	private static final BiFunction<ResourceLocation, Boolean, RenderType> PARTICLE_CUTOUT_NO_CULL = Util.memoize((texture, outline) ->
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_PARTICLE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.createCompositeState(outline);
		
		return RenderType.create("particle_cutout_no_cull",
				DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, 256,
				true, false,
				state
		);
	});
	
	private static final BiFunction<ResourceLocation, Boolean, RenderType> PARTICLE_TRANSLUCENT = Util.memoize((texture, outline) ->
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_PARTICLE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.createCompositeState(outline);
		
		return RenderType.create("particle_translucent",
				DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, 256,
				true, true,
				state
		);
	});
	
	private static final BiFunction<ResourceLocation, Boolean, RenderType> PARTICLE_ADDITIVE = Util.memoize((texture, outline) ->
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_PARTICLE_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(ADDITIVE_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(NO_LIGHTMAP)
				.createCompositeState(outline);
		
		return RenderType.create("particle_translucent_additive",
				DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, 256,
				true, true,
				state
		);
	});
	
	public static RenderType particleSolid(ResourceLocation texture)
	{
		return PARTICLE_SOLID.apply(texture);
	}
	
	public static RenderType particleCutout(ResourceLocation texture, boolean outline)
	{
		return PARTICLE_CUTOUT_NO_CULL.apply(texture, outline);
	}
	
	public static RenderType particleCutout(ResourceLocation texture)
	{
		return particleCutout(texture, true);
	}
	
	public static RenderType particleTranslucent(ResourceLocation texture, boolean outline)
	{
		return PARTICLE_TRANSLUCENT.apply(texture, outline);
	}
	
	public static RenderType particleTranslucent(ResourceLocation texture)
	{
		return particleTranslucent(texture, true);
	}
	
	public static RenderType particleAdditive(ResourceLocation texture, boolean outline)
	{
		return PARTICLE_ADDITIVE.apply(texture, outline);
	}
	
	public static RenderType particleAdditive(ResourceLocation texture)
	{
		return particleAdditive(texture, true);
	}
	
	private ParticleRenderTypes(String pName, Runnable pSetupState, Runnable pClearState)
	{
		super(pName, pSetupState, pClearState);
	}
}