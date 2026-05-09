package org.zeith.hammeranims.core.client.model;

import org.zeith.hammeranims.standalone.utils.MathHelper;
import lombok.val;
import org.zeith.hammeranims.standalone.mc.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.constrains.*;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.IVertexOutput;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.utils.PoseStack;

import java.util.*;

public class GeometricModelImpl
		implements IGeometricModel
{
	protected final ModelBoneF root;
	protected final IGeometryContainer container;
	protected final IGeometryConstraints constraints;
	protected final Map<String, ModelBoneF> bones = new HashMap<>();
	protected final Map<String, IBoneConstraints> boneConstraints = new HashMap<>();
	protected final Map<String, ModelBoneF> locatorSources = new HashMap<>();
	
	public GeometricModelImpl(GeometryDataImpl root)
	{
		this.container = root.getContainer();
		this.constraints = container.getConstraints();
		this.root = root.bakeRoot();
		registerBone(this.root);
	}
	
	protected void registerBone(ModelBoneF part)
	{
		bones.put(part.name, part);
		boneConstraints.put(part.name, constraints.getConstraints(part.name));
		part.getChildren().values().forEach(this::registerBone);
		for(val loc : part.getLocators().entrySet())
			locatorSources.put(loc.getKey(), part);
	}
	
	@Override
	public boolean hasBone(String bone)
	{
		return bones.containsKey(bone);
	}
	
	@Override
	public Set<String> getBoneNames()
	{
		return bones.keySet();
	}
	
	@Override
	public IRenderableBone getRoot()
	{
		return root;
	}
	
	@Override
	public Collection<? extends IRenderableBone> getBones()
	{
		return bones.values();
	}
	
	@Nullable
	@Override
	public IRenderableBone getBone(String bone)
	{
		return bones.get(bone);
	}
	
	@Override
	public void resetPose()
	{
		for(ModelBoneF s : bones.values())
			s.reset();
	}
	
	@Override
	public GeometryPose emptyPose()
	{
		return new GeometryPose(this::hasBone);
	}
	
	@Override
	public void applyPose(GeometryPose pose)
	{
		Map<String, GeometryTransforms> poseBones = pose.getBoneTransforms();
		
		for(String boneKey : bones.keySet())
		{
			ModelBoneF bone = bones.get(boneKey);
			if(bone == null) continue;
			bone.reset();
			
			GeometryTransforms add = poseBones.get(boneKey);
			if(add == null) continue;
			add.applyConstraints(boneConstraints.get(boneKey));
			
			Vec3d translate = add.translation,
					rotate = add.rotation.scale(MathHelper.torad),
					scale = add.scale;
			
			bone.offset.add(
					(float) translate.x,
					(float) -translate.y,
					(float) translate.z
			);
			
			bone.getRotation().sub(
					(float) rotate.x,
					(float) rotate.y,
					(float) -rotate.z
			);
			
			bone.getScale().mul(
					(float) scale.x,
					(float) scale.y,
					(float) scale.z
			);
			
			if(add.forceVertexType != null) bone.forceVertexType = add.forceVertexType;
			bone.renderCubes = !add.skipGeometry;
		}
	}
	
	@Override
	public void renderModel(RenderData data)
	{
		PoseStack pose = data.pose;
		IVertexOutput renderer = data.renderer;
		root.render(pose,
				renderer,
				data.combinedLightIn, data.combinedOverlayIn,
				data.red, data.green, data.blue, data.alpha
		);
		pose.reset();
	}
	
	@Override
	public void dispose()
	{
	}
}