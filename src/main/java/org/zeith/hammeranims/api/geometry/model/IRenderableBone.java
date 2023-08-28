package org.zeith.hammeranims.api.geometry.model;

import net.minecraftforge.fml.relauncher.*;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.utils.PoseStack;

import javax.annotation.Nullable;

public interface IRenderableBone
		extends IBone
{
	@Override
	@Nullable
	IRenderableBone getParent();
	
	@SideOnly(Side.CLIENT)
	void render(PoseStack poseStackIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
	
	@SideOnly(Side.CLIENT)
	void translateAndRotate(PoseStack matrixStackIn);
	
	@SideOnly(Side.CLIENT)
	default void applyTransformTree(PoseStack pose)
	{
		// Firstly, apply parent's transforms recursively
		IRenderableBone par = getParent();
		if(par != null) par.applyTransformTree(pose);
		
		// Then apply current bone transforms
		translateAndRotate(pose);
	}
}