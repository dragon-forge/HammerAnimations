package org.zeith.hammeranims.api.geometry.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.*;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;

import javax.annotation.Nullable;

public interface IRenderableBone
		extends IBone
{
	@Override
	@Nullable
	IRenderableBone getParent();
	
	@OnlyIn(Dist.CLIENT)
	void render(PoseStack poseStackIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
	
	@OnlyIn(Dist.CLIENT)
	void transform(PoseStack matrixStackIn);
	
	@OnlyIn(Dist.CLIENT)
	default void applyTransformTree(PoseStack pose)
	{
		// Firstly, apply parent's transforms recursively
		var par = getParent();
		if(par != null) par.applyTransformTree(pose);
		
		// Then apply current bone transforms
		transform(pose);
	}
}