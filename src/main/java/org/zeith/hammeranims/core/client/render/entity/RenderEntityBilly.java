package org.zeith.hammeranims.core.client.render.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.contents.entity.EntityBilly;
import org.zeith.hammeranims.core.init.ContainersHA;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderEntityBilly
		extends BedrockEntityRenderer<EntityBilly>
{
	final ResourceLocation texture = HammerAnimations.id("textures/entity/billy.png");
	final ResourceLocation textureGlow = HammerAnimations.id("textures/entity/billy_glow.png");
	
	public RenderEntityBilly(EntityRendererProvider.Context pContext)
	{
		super(pContext, ContainersHA.BILLY_GEOM, 0.5F);
	}
	
	@Override
	protected List<RenderType> getRenderPasses(EntityBilly entity)
	{
		return List.of(
				RenderType.entitySolid(texture),
				RenderType.entityTranslucentEmissive(textureGlow)
		);
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
