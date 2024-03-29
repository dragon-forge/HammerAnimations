package org.zeith.hammeranims.core.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.contents.entity.EntityBilly;
import org.zeith.hammeranims.core.init.ContainersHA;
import org.zeith.hammerlib.annotations.client.ClientSetup;

public class RenderEntityBilly
		extends BedrockEntityRenderer<EntityBilly>
{
	final ResourceLocation texture = HammerAnimations.id("textures/entity/billy.png");
	
	public RenderEntityBilly(EntityRendererManager pContext)
	{
		super(pContext);
	}
	
	@Override
	public ResourceLocation getTextureLocation(EntityBilly entityBilly)
	{
		return texture;
	}
	
	@ClientSetup
	public static void registerRenderer(FMLClientSetupEvent e)
	{
		RenderingRegistry.registerEntityRenderingHandler(ContainersHA.BILLY_ENTITY, RenderEntityBilly::new);
	}
}
