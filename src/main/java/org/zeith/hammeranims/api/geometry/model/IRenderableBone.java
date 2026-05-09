package org.zeith.hammeranims.api.geometry.model;

import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.core.client.render.IVertexOutput;
import org.zeith.hammeranims.api.geometry.data.FaceUV;
import org.zeith.hammeranims.api.utils.IFaceUVPredicate;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;
import org.zeith.hammeranims.core.utils.PoseStack;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;

import java.util.function.Predicate;
import java.util.function.*;

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
	
	boolean anyUVMatch(IFaceUVPredicate filter);
	boolean allUVMatch(IFaceUVPredicate filter);
	<STATE> STATE visitUVs(STATE state, BiFunction<STATE, FaceUV, STATE> walker, Predicate<STATE> isDone);
	
	void setDefaultVertexType(VertexType defaultVertexType);
	void setForcedVertexType(VertexType forceVertexType);
	
	void renderCubes(boolean b);
	
	void renderHookAfterCubes(boolean b);
	
	void renderChildren(boolean b);
}