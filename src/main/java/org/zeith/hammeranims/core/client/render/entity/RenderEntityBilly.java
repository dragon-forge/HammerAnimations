package org.zeith.hammeranims.core.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.contents.entity.EntityBilly;
import org.zeith.hammeranims.core.init.ContainersHA;

import javax.annotation.Nullable;

public class RenderEntityBilly
		extends BedrockEntityRenderer<EntityBilly>
{
	final ResourceLocation texture = HammerAnimations.id("textures/entity/billy.png");
	
	public RenderEntityBilly(RenderManager rendermanagerIn)
	{
		super(rendermanagerIn, ContainersHA.BILLY_GEOM, 0.5F);
	}
	
	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityBilly entity)
	{
		return texture;
	}
}
