package org.zeith.hammeranims.api.geometry.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.zeith.hammeranims.api.geometry.data.FaceUV;
import org.zeith.hammeranims.api.utils.IFaceUVPredicate;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;

import javax.annotation.Nullable;
import java.util.function.*;

public interface IRenderableBone
		extends IBone
{
	@Override
	@Nullable
	IRenderableBone getParent();
	
	@OnlyIn(Dist.CLIENT)
	void render(PoseStack poseStackIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
	
	@OnlyIn(Dist.CLIENT)
	void applyBoneTransforms(PoseStack matrixStackIn);
	
	@OnlyIn(Dist.CLIENT)
	default void applyTransformTree(PoseStack pose)
	{
		// Firstly, apply parent's transforms recursively
		IRenderableBone par = getParent();
		if(par != null) par.applyTransformTree(pose);
		
		// Then apply current bone transforms
		applyBoneTransforms(pose);
	}
	
	// Can be used to
	boolean anyUVMatch(IFaceUVPredicate filter);
	boolean allUVMatch(IFaceUVPredicate filter);
	<STATE> STATE visitUVs(STATE state, BiFunction<STATE, FaceUV, STATE> walker, Predicate<STATE> isDone);
	
	void setDefaultVertexType(VertexType defaultVertexType);
	
	void renderCubes(boolean b);
	
	void renderHookAfterCubes(boolean b);
	
	void renderChildren(boolean b);
}