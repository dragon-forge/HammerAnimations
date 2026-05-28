package org.zeith.hammeranims.core.client.model;

import net.minecraft.client.model.*;
import org.zeith.hammeranims.api.geometry.data.FaceUV;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.api.utils.IFaceUVPredicate;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryLocator;
import org.zeith.hammeranims.core.utils.PoseStack;
import org.zeith.hammeranims.joml.*;

import java.util.*;
import java.util.function.*;

public class ModelBoneF
		extends ModelRenderer
		implements IRenderableBone
{
	protected ModelBoneF parent;
	
	public IRenderableHook renderHook = IRenderableHook.NOTHING;
	
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
	
	// one-time vertex type
	public VertexType forceVertexType;
	
	// fallback from one-time to this vertex type
	public VertexType defaultVertexType;
	
	public ModelBoneF(ModelBase model, String name, int textureWidth, int textureHeight, Vector3f startRotRadians, List<ModelCubeF> cubes, Map<String, ModelBoneF> children, Map<String, GeometryLocator> locators, boolean neverRender)
	{
		super(model, name);
		this.setTextureSize(textureWidth, textureHeight);
		this.startRotationRadians = startRotRadians;
		this.rotation = new Vector3f(startRotRadians);
		this.isHidden = neverRender;
		this.children = Collections.unmodifiableMap(children);
		this.locators = Collections.unmodifiableMap(locators);
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
	public void applyBoneTransforms(PoseStack matrixStackIn)
	{
		matrixStackIn.translate(-offset.x() / 16F, -offset.y() / 16F, offset.z() / 16F);
		matrixStackIn.translate(this.offsetX / 16.0F, this.offsetY / 16.0F, this.offsetZ / 16.0F);
		
		if(this.rotation.x() != 0.0F || this.rotation.y() != 0.0F || this.rotation.z() != 0.0F)
			matrixStackIn.mulPose(new Quaternionf().rotateZYX(rotation.z(), rotation.y(), rotation.x()));
		
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
	
	@Override
	public boolean anyUVMatch(IFaceUVPredicate filter)
	{
		for(int i = 0, len = cubes.size(); i < len; i++)
		{
			TexturedQuadF[] quads = cubes.get(i).getQuads();
			for(int j = 0, len2 = quads.length; j < len2; j++)
				if(filter.test(i, j, quads[j].uv))
					return true;
		}
		return false;
	}
	
	@Override
	public boolean allUVMatch(IFaceUVPredicate filter)
	{
		for(int i = 0, len = cubes.size(); i < len; i++)
		{
			TexturedQuadF[] quads = cubes.get(i).getQuads();
			for(int j = 0, len2 = quads.length; j < len2; j++)
				if(!filter.test(i, j, quads[j].uv))
					return false;
		}
		return true;
	}
	
	@Override
	public <STATE> STATE visitUVs(STATE state, BiFunction<STATE, FaceUV, STATE> walker, Predicate<STATE> isDone)
	{
		for(ModelCubeF cube : cubes)
		{
			for(TexturedQuadF quad : cube.getQuads())
			{
				state = walker.apply(state, quad.uv);
				if(isDone.test(state)) return state;
			}
		}
		return state;
	}
	
	@Override
	public void setDefaultVertexType(VertexType defaultVertexType)
	{
		this.defaultVertexType = defaultVertexType;
	}
	
	@Override
	public void setForcedVertexType(VertexType forceVertexType)
	{
		this.forceVertexType = forceVertexType;
	}
	
	public void renderCubes(PoseStack.Entry matrixEntryIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		if(renderCubes)
		{
			VertexType typeThisTime = forceVertexType != null ? forceVertexType : defaultVertexType;
			for(ModelCubeF cube : cubes)
				cube.render(matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha, typeThisTime);
		}
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
		forceVertexType = null;
		rotation.set(startRotationRadians.x, startRotationRadians.y, startRotationRadians.z);
		offset.set(0, 0, 0);
		scale.set(1, 1, 1);
	}
	
	public void setPos(float x, float y, float z)
	{
		offsetX = x;
		offsetY = y;
		offsetZ = z;
	}
}