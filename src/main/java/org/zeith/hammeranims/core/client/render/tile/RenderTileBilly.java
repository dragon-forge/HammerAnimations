package org.zeith.hammeranims.core.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.contents.blocks.TileBilly;
import org.zeith.hammerlib.client.render.tile.IBESR;

public class RenderTileBilly
		implements IBESR<TileBilly>
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
	public void render(TileBilly entity, float partial, PoseStack matrix, MultiBufferSource buf, int lighting, int overlay)
	{
		model.applySystem(partial, entity.getAnimationSystem());
		matrix.translate(0.5, 0, 0.5);
		model.renderModel(data.apply(matrix, buf.getBuffer(RenderType.entitySolid(texture)), lighting, overlay));
	}
}