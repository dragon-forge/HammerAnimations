package org.zeith.hammeranims.core.client.render.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.contents.entity.EntityBilly;
import org.zeith.hammeranims.core.init.ContainersHA;
import org.zeith.hammerlib.annotations.client.ClientSetup;

import java.util.Arrays;
import java.util.List;

public class RenderEntityBilly
		extends BedrockEntityRenderer<EntityBilly>
{
	final ResourceLocation texture = HammerAnimations.id("textures/entity/billy.png");
	final ResourceLocation textureGlow = HammerAnimations.id("textures/entity/billy_glow.png");
	
	public RenderEntityBilly(EntityRendererManager pContext)
	{
		super(pContext, ContainersHA.BILLY_GEOM, 0.5F);
	}
	
	@Override
	public ResourceLocation getTextureLocation(EntityBilly entityBilly)
	{
		return texture;
	}
	
	@Override
	protected List<RenderType> getRenderPasses(EntityBilly entity)
	{
		return Arrays.asList(
				RenderType.entitySolid(texture),
				RenderType.eyes(textureGlow)
		);
	}
	
	@ClientSetup
	public static void registerRenderer(FMLClientSetupEvent e)
	{
		RenderingRegistry.registerEntityRenderingHandler(ContainersHA.BILLY_ENTITY, RenderEntityBilly::new);
	}
}
