package org.zeith.hammeranims.core.client.model;

import org.joml.*;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.IVertexOutput;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryLocator;
import org.zeith.hammeranims.core.utils.PoseStack;

import java.util.*;

public class ModelBoneF
		implements IRenderableBone
{
	protected ModelBoneF parent;
	
	public IRenderableHook renderHook = IRenderableHook.NOTHING;
	
	public float textureWidth;
	public float textureHeight;
	public boolean isHidden;
	public final String name;
	
	private final Vector3f mcOffset = new Vector3f();
	private final Vector3f scale = new Vector3f(1, 1, 1);
	public Vector3f offset = new Vector3f();
	private final Vector3f rotation; // in radians
	public Vector3f startRotationRadians;
	private final Map<String, ModelBoneF> children;
	private final Map<String, GeometryLocator> locators;
	public List<ModelCubeF> cubes;
	
	private PoseStack.Entry lastTransform = new PoseStack().last();
	private boolean transformValid;
	
	public boolean renderCubes = true;
	public boolean renderHookAfterCubes = true;
	public boolean renderChildren = true;
	
	public ModelBoneF(String name, int textureWidth, int textureHeight, Vector3f startRotRadians, List<ModelCubeF> cubes, Map<String, ModelBoneF> children, Map<String, GeometryLocator> locators, boolean neverRender)
	{
		this.name = name;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.startRotationRadians = startRotRadians;
		this.rotation = new Vector3f(startRotRadians);
		this.isHidden = neverRender;
		this.children = children;
		this.locators = locators;
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
	public void render(PoseStack poseStackIn, IVertexOutput bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		transformValid = true;
		if(this.isHidden) return;
		
		poseStackIn.pushPose();
		
		this.applyBoneTransforms(poseStackIn);
		
		lastTransform = poseStackIn.last();
		
		this.renderCubes(poseStackIn.last(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if(renderChildren)
			for(ModelBoneF part : this.children.values())
				part.render(poseStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		
		poseStackIn.popPose();
	}
	
	@Override
	public void applyBoneTransforms(PoseStack pose)
	{
		var combined = new Vector3f(mcOffset)
				.add(-offset.x, -offset.y, offset.z)
				.mul(1 / 16F);
		pose.last().getPose().translate(combined);
		
		if(this.rotation.x() != 0.0F || this.rotation.y() != 0.0F || this.rotation.z() != 0.0F)
			pose.mulPose(new Quaternionf().rotateZYX(rotation.z(), rotation.y(), rotation.x()));
		
		if(this.scale.x() != 1.0F || this.scale.y() != 1.0F || this.scale.z() != 1.0F)
			pose.scale(scale.x(), scale.y(), scale.z());
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
	
	public void renderCubes(PoseStack.Entry matrixEntryIn, IVertexOutput bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		if(renderCubes)
			for(ModelCubeF cube : cubes)
				cube.render(matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if(renderHookAfterCubes)
			renderHook.render(matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	public void applyTransform(PoseStack stack)
	{
		if(isHidden) return;
		if(!transformValid) return;
		
		PoseStack.Entry last = stack.last();
		last.getPose().set(lastTransform.getPose());
		last.getNormal().set(lastTransform.getNormal());
	}
	
	@Override
	public String getName()
	{
		return name;
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
	public Map<String, GeometryLocator> getLocators()
	{
		return locators;
	}
	
	@Override
	public void reset()
	{
		renderCubes = true;
		renderHookAfterCubes = true;
		renderChildren = true;
		transformValid = false;
		rotation.set(startRotationRadians.x, startRotationRadians.y, startRotationRadians.z);
		offset.set(0, 0, 0);
		scale.set(1, 1, 1);
	}
	
	public void setPos(float x, float y, float z)
	{
		mcOffset.set(x, y, z);
	}
}