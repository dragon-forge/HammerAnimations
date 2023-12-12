package org.zeith.hammeranims.core.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.contents.blocks.TileBilly;
import org.zeith.hammerlib.client.render.tile.ITESR;

public class RenderTileBilly
		implements ITESR<TileBilly>
{
	IGeometricModel model;
	
	final ResourceLocation texture = HammerAnimations.id("textures/entity/billy.png");
	final RenderData data;
	
	public RenderTileBilly()
	{
		data = new RenderData();
		HammerAnimationsApi.EVENT_BUS.addListener(this::refreshModel);
	}
	
	public void refreshModel(RefreshStaleModelsEvent e)
	{
		model = org.zeith.hammeranims.core.init.ContainersHA.BILLY_GEOM.createModel();
	}
	
	@Override
	public void render(TileBilly entity, float partial, MatrixStack matrix, IRenderTypeBuffer buf, int lighting, int overlay, TileEntityRendererDispatcher dispatcher)
	{
		model.applySystem(partial, entity.getAnimationSystem());
		matrix.translate(0.5, 0, 0.5);
		model.renderModel(data.apply(matrix, buf.getBuffer(RenderType.entitySolid(texture)), lighting, overlay));
	}
}