package org.zeith.hammeranims.api.geometry.model;

import net.minecraftforge.fml.relauncher.*;
import org.zeith.hammeranims.api.geometry.data.FaceUV;
import org.zeith.hammeranims.api.utils.IFaceUVPredicate;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;
import org.zeith.hammeranims.core.utils.PoseStack;

import javax.annotation.Nullable;
import java.util.function.*;

public interface IRenderableBone
		extends IBone
{
	@Override
	@Nullable
	IRenderableBone getParent();
	
	@SideOnly(Side.CLIENT)
	void render(PoseStack poseStackIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
	
	@SideOnly(Side.CLIENT)
	void applyBoneTransforms(PoseStack matrixStackIn);
	
	@SideOnly(Side.CLIENT)
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