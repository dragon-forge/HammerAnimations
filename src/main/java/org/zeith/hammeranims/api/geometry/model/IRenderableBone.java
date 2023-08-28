package org.zeith.hammeranims.api.geometry.model;

import net.minecraftforge.fml.relauncher.*;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.utils.PoseStack;

public interface IRenderableBone
		extends IBone
{
	@SideOnly(Side.CLIENT)
	void render(PoseStack poseStackIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
	
	@SideOnly(Side.CLIENT)
	void translateAndRotate(PoseStack matrixStackIn);
}