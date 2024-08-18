package org.zeith.hammeranims.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.utils.IPoseEntry;
import org.zeith.hammeranims.joml.Vector3f;

import java.util.*;

public class ModelBoneF
		extends ModelPart
		implements IRenderableBone
{
	protected ModelBoneF parent;
	
	public IRenderableHook renderHook = IRenderableHook.NOTHING;
	
	public final String boxName;
	private final Vector3f scale = new Vector3f(1, 1, 1);
	public Vector3f offset = new Vector3f();
	private final Vector3f rotation; // in radians
	public Vector3f startRotationRadians;
	private final Map<String, ModelBoneF> children;
	public List<ModelCubeF> cubes;
	
	public boolean renderCubes = true;
	public boolean renderHookAfterCubes = true;
	public boolean renderChildren = true;
	
	public ModelBoneF(String name, Vector3f startRotRadians, List<ModelCubeF> cubes, Map<String, ModelBoneF> children, boolean neverRender)
	{
		super(Collections.emptyList(), Collections.emptyMap());
		this.boxName = name;
		this.startRotationRadians = startRotRadians;
		this.rotation = new Vector3f(startRotRadians);
		this.visible = !neverRender;
		this.children = Collections.unmodifiableMap(children);
		this.cubes = cubes;
		
		for(ModelBoneF ch : children.values())
			ch.parent = this;
	}
	
	@Override
	public IRenderableBone getParent()
	{
		return parent;
	}
	
	@Override
	public void render(PoseStack poseStackIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		if(visible)
		{
			poseStackIn.pushPose();
			
			this.applyBoneTransforms(poseStackIn);
			
			this.renderCubes(IPoseEntry.read(poseStackIn.last()), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			
			for(ModelBoneF part : this.children.values())
				part.render(poseStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			
			poseStackIn.popPose();
		}
	}
	
	@Override
	public void translateAndRotate(PoseStack pPoseStack)
	{
		applyBoneTransforms(pPoseStack);
	}
	
	@Override
	public void applyBoneTransforms(PoseStack matrixStackIn)
	{
		matrixStackIn.translate(-offset.x() / 16F, -offset.y() / 16F, offset.z() / 16F);
		matrixStackIn.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
		
		if(rotation.z() != 0.0F)
			matrixStackIn.mulPose(com.mojang.math.Vector3f.ZP.rotation(rotation.z()));
		
		if(rotation.y() != 0.0F)
			matrixStackIn.mulPose(com.mojang.math.Vector3f.YP.rotation(rotation.y()));
		
		if(rotation.x() != 0.0F)
			matrixStackIn.mulPose(com.mojang.math.Vector3f.XP.rotation(rotation.x()));
		
		if(this.scale.x() != 1.0F || this.scale.y() != 1.0F || this.scale.z() != 1.0F)
			matrixStackIn.scale(scale.x(), scale.y(), scale.z());
	}
	
	@Override
	public void renderCubes(boolean b)
	{
		this.renderCubes = b;
	}
	
	@Override
	public void renderHookAfterCubes(boolean b)
	{
		this.renderHookAfterCubes = b;
	}
	
	@Override
	public void renderChildren(boolean b)
	{
		this.renderChildren = b;
	}
	
	private void renderCubes(IPoseEntry matrixEntryIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		if(renderCubes)
			for(ModelCubeF cube : cubes)
				cube.render(matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if(renderHookAfterCubes)
			renderHook.render(matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
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
	
	@Override
	public void reset()
	{
		renderCubes = true;
		renderHookAfterCubes = true;
		renderChildren = true;
		rotation.set(startRotationRadians.x, startRotationRadians.y, startRotationRadians.z);
		offset.set(0, 0, 0);
		scale.set(1, 1, 1);
	}
}