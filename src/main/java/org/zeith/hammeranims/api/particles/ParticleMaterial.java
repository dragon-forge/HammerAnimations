package org.zeith.hammeranims.api.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public enum ParticleMaterial
{
	OPAQUE("particles_opaque", RenderType::entitySolid),
	ALPHA("particles_alpha", RenderType::entityCutoutNoCull),
	BLEND("particles_blend", RenderType::entityTranslucent),
	ADDITIVE("particles_add", RenderType::eyes);
	
	public final String id;
	public final Function<ResourceLocation, RenderType> renderType;
	
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
	
	ParticleMaterial(String id, Function<ResourceLocation, RenderType> renderType)
	{
		this.id = id;
		this.renderType = renderType;
	}
	
	public void beginGL()
	{
		switch(this)
		{
			case OPAQUE:
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.alphaFunc(GL11.GL_GREATER, 0F);
				RenderSystem.disableBlend();
				RenderSystem.enableAlphaTest();
				break;
			case ALPHA:
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
				RenderSystem.disableBlend();
				RenderSystem.enableAlphaTest();
				break;
			case BLEND:
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
				RenderSystem.enableBlend();
				RenderSystem.enableAlphaTest();
				break;
			case ADDITIVE:
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
				RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
				RenderSystem.enableBlend();
				RenderSystem.enableAlphaTest();
				break;
		}
	}
	
	public void endGL()
	{
		switch(this)
		{
			case OPAQUE:
			case ALPHA:
			case BLEND:
			case ADDITIVE:
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.disableBlend();
				RenderSystem.enableAlphaTest();
				RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
				break;
		}
	}
}