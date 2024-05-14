package org.zeith.hammeranims.core.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ParticleRenderTypes
{
	protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
	protected static final RenderStateShard.LightmapStateShard NO_LIGHTMAP = new RenderStateShard.LightmapStateShard(false);
	protected static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);
	protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_SOLID_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getParticleShader);
	protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getParticleShader);
	protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getParticleShader);
	
	protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", () ->
	{
		RenderSystem.disableBlend();
	}, () ->
	{
	});
	
	protected static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () ->
	{
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
		);
	}, () ->
	{
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	
	protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("additive_transparency", () ->
	{
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
	}, () ->
	{
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	
	private static final Function<ResourceLocation, RenderType> PARTICLE_SOLID = Util.memoize((texture) ->
	{
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
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
				.setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER)
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
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
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
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
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
}