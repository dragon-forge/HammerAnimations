package org.zeith.hammeranims.core.client.render.tile;

import com.zeitheron.hammercore.client.render.tesr.TESR;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.contents.blocks.TileBilly;
import org.zeith.hammeranims.core.init.ContainersHA;

import javax.annotation.*;

public class RenderTileBilly
		extends TESR<TileBilly>
{
	IGeometricModel model;
	
	final RenderData data;
	
	public RenderTileBilly()
	{
		data = new RenderData();
		data.texture = HammerAnimations.id("textures/entity/billy.png");
		HammerAnimationsApi.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void refreshModel(RefreshStaleModelsEvent e)
	{
		model = org.zeith.hammeranims.core.init.ContainersHA.BILLY_GEOM.createModel();
	}
	
	@Override
	public void renderTileEntityAt(@Nonnull TileBilly te, double x, double y, double z, float partialTicks, @Nullable ResourceLocation destroyStage, float alpha)
	{
		model.applySystem(partialTicks, te.getAnimationSystem());
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);
		model.renderModel(data);
		GlStateManager.popMatrix();
	}
}