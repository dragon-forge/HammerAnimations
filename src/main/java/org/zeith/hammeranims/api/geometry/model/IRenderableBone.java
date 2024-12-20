package org.zeith.hammeranims.api.geometry.model;

import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.core.client.render.IVertexOutput;
import org.zeith.hammeranims.core.utils.PoseStack;

public interface IRenderableBone
		extends IBone
{
	@Override
	@Nullable
	IRenderableBone getParent();
	
	void render(PoseStack poseStackIn, IVertexOutput bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
	
	void applyBoneTransforms(PoseStack matrixStackIn);
	
	default void applyTransformTree(PoseStack pose)
	{
		// Firstly, apply parent's transforms recursively
		IRenderableBone par = getParent();
		if(par != null) par.applyTransformTree(pose);
		
		// Then apply current bone transforms
		applyBoneTransforms(pose);
	}
	
	void renderCubes(boolean b);
	
	void renderHookAfterCubes(boolean b);
	
	void renderChildren(boolean b);
}