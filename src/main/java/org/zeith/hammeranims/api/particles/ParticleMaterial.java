package org.zeith.hammeranims.api.particles;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public enum ParticleMaterial
{
	OPAQUE("particles_opaque"),
	ALPHA("particles_alpha"),
	BLEND("particles_blend"),
	ADDITIVE("particles_add");
	
	public final String id;
	
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
	
	ParticleMaterial(String id)
	{
		this.id = id;
	}
	
	@SideOnly(Side.CLIENT)
	public void beginGL()
	{
		switch(this)
		{
			case OPAQUE:
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
				break;
			case ALPHA:
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
				break;
			case BLEND:
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				break;
			case ADDITIVE:
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				break;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void endGL()
	{
		switch(this)
		{
			case OPAQUE:
			case ALPHA:
			case BLEND:
			case ADDITIVE:
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				break;
		}
	}
}