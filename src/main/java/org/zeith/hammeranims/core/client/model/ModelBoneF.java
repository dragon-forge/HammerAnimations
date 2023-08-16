package org.zeith.hammeranims.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.joml.*;

import java.util.*;

public class ModelBoneF
		extends ModelPart
{
	public final String boxName;
	private final Vector3f scale = new Vector3f(1, 1, 1);
	public Vector3f offset = new Vector3f();
	private final Vector3f rotation; // in radians
	public Vector3f startRotationRadians;
	private final Map<String, ModelBoneF> children;
	public List<ModelCubeF> cubes;
	
	private PoseStack.Pose lastTransform = new PoseStack().last();
	private boolean transformValid;
	
	public ModelBoneF(String name, int textureWidth, int textureHeight, Vector3f startRotRadians, List<ModelCubeF> cubes, Map<String, ModelBoneF> children, boolean neverRender)
	{
		super(Collections.emptyList(), Collections.emptyMap());
		this.boxName = name;
		this.startRotationRadians = startRotRadians;
		this.rotation = new Vector3f(startRotRadians);
		this.visible = !neverRender;
		this.children = Collections.unmodifiableMap(children);
		this.cubes = cubes;
	}
	
	public void renderCubes(PoseStack poseStackIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		transformValid = true;
		
		if(visible)
		{
			poseStackIn.pushPose();
			
			this.translateAndRotate(poseStackIn);
			
			poseStackIn.scale(scale.x(), scale.y(), scale.z());
			
			lastTransform = poseStackIn.last();
			
			this.renderCubes(poseStackIn.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			
			for(ModelBoneF part : this.children.values())
				part.renderCubes(poseStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			
			poseStackIn.popPose();
		}
	}
	
	public void translateAndRotate(PoseStack matrixStackIn)
	{
		matrixStackIn.translate(-offset.x() / 16F, -offset.y() / 16F, offset.z() / 16F);
		matrixStackIn.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
		
		if(this.rotation.x() != 0.0F || this.rotation.y() != 0.0F || this.rotation.z() != 0.0F)
			matrixStackIn.mulPose(new Quaternionf().rotateZYX(rotation.z(), rotation.y(), rotation.x()));
		
		if(this.scale.x() != 1.0F || this.scale.y() != 1.0F || this.scale.z() != 1.0F)
			matrixStackIn.scale(scale.x(), scale.y(), scale.z());
	}
	
	private void renderCubes(PoseStack.Pose matrixEntryIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		for(ModelCubeF cube : cubes)
			cube.render(matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	public void applyTransform(PoseStack stack)
	{
		if(!transformValid || !visible) return;
		
		PoseStack.Pose last = stack.last();
		last.pose().set(lastTransform.pose());
		last.normal().set(lastTransform.normal());
	}
	
	public Vector3f getTranslation()
	{
		return offset;
	}
	
	public Vector3f getRotation()
	{
		return rotation;
	}
	
	public Vector3f getScale()
	{
		return scale;
	}
	
	public Map<String, ModelBoneF> getChildren()
	{
		return children;
	}
	
	public void reset()
	{
		transformValid = false;
		rotation.set(startRotationRadians.x, startRotationRadians.y, startRotationRadians.z);
		offset.set(0, 0, 0);
		scale.set(1, 1, 1);
	}
}