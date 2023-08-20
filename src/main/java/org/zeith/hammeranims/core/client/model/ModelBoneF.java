package org.zeith.hammeranims.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import org.zeith.hammeranims.api.geometry.model.IBone;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.joml.*;

import java.util.*;

public class ModelBoneF
		extends ModelPart
		implements IBone
{
	public final String boxName;
	private final Vector3f scale = new Vector3f(1, 1, 1);
	public Vector3f offset = new Vector3f();
	private final Vector3f rotation; // in radians
	public Vector3f startRotationRadians;
	private final Map<String, ModelBoneF> children;
	public List<ModelCubeF> cubes;
	
	public ModelBoneF(String name, Vector3f startRotRadians, List<ModelCubeF> cubes, Map<String, ModelBoneF> children, boolean neverRender)
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
		if(visible)
		{
			poseStackIn.pushPose();
			
			this.translateAndRotate(poseStackIn);
			
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
	
	@Override
	public String getName()
	{
		return boxName;
	}
	
	@Override
	public Vector3f getTranslation()
	{
		return offset;
	}
	
	@Override
	public Vector3f getRotation()
	{
		return rotation;
	}
	
	@Override
	public Vector3f getScale()
	{
		return scale;
	}
	
	@Override
	public Map<String, ModelBoneF> getChildren()
	{
		return children;
	}
	
	public void reset()
	{
		rotation.set(startRotationRadians.x, startRotationRadians.y, startRotationRadians.z);
		offset.set(0, 0, 0);
		scale.set(1, 1, 1);
	}
}