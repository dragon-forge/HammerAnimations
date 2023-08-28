package org.zeith.hammeranims.api.geometry.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.*;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;

public interface IRenderableBone
		extends IBone
{
	@OnlyIn(Dist.CLIENT)
	void render(PoseStack poseStackIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
	
	@OnlyIn(Dist.CLIENT)
	void translateAndRotate(PoseStack matrixStackIn);
}