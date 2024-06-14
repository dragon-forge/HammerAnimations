package org.zeith.hammeranims.core.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.contents.entity.EntityBilly;
import org.zeith.hammeranims.core.init.ContainersHA;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class RenderEntityBilly
		extends BedrockEntityRenderer<EntityBilly>
{
	final ResourceLocation texture = HammerAnimations.id("textures/entity/billy.png");
	
	public RenderEntityBilly(EntityRendererProvider.Context pContext)
	{
		super(pContext, ContainersHA.BILLY_GEOM, 0.5F);
	}
	
	@SubscribeEvent
	public static void registerRenderer(EntityRenderersEvent.RegisterRenderers e)
	{
		e.registerEntityRenderer(ContainersHA.BILLY_ENTITY, RenderEntityBilly::new);
	}
	
	@Override
	public ResourceLocation getTextureLocation(EntityBilly entityBilly)
	{
		return texture;
	}
}
