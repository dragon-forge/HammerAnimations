package org.zeith.hammeranims.api.geometry.model;

import com.mojang.blaze3d.matrix.MatrixStack;
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
	void render(MatrixStack poseStackIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
	
	@OnlyIn(Dist.CLIENT)
	void transform(MatrixStack matrixStackIn);
	
	@OnlyIn(Dist.CLIENT)
	default void applyTransformTree(MatrixStack pose)
	{
		// Firstly, apply parent's transforms recursively
		IRenderableBone par = getParent();
		if(par != null) par.applyTransformTree(pose);
		
		// Then apply current bone transforms
		transform(pose);
	}
}