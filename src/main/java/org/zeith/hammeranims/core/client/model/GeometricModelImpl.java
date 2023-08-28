package org.zeith.hammeranims.core.client.model;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.api.geometry.constrains.*;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;

import java.util.*;

public class GeometricModelImpl
		implements IGeometricModel
{
	protected final ModelBoneF root;
	protected final IGeometryConstraints constraints;
	protected final Map<String, ModelBoneF> bones = new HashMap<>();
	protected final Map<String, IBoneConstraints> boneConstraints = new HashMap<>();
	
	public GeometricModelImpl(GeometryDataImpl root)
	{
		this.root = root.bakeRoot();
		this.constraints = root.getContainer().getConstraints();
		registerBone(this.root);
	}
	
	protected void registerBone(ModelBoneF part)
	{
		bones.put(part.boxName, part);
		boneConstraints.put(part.boxName, constraints.getConstraints(part.boxName));
		part.getChildren().values().forEach(this::registerBone);
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
	
	public boolean hasBone(String bone)
	{
		return bones.containsKey(bone);
	}
	
	@Override
	public void resetPose()
	{
		for(ModelBoneF s : bones.values())
			s.reset();
	}
	
	GeometryPose emptyPose = new GeometryPose(this::hasBone);
	
	@Override
	public GeometryPose emptyPose()
	{
		emptyPose.reset();
		return emptyPose;
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
			
			Vec3 translate = add.translation,
					rotate = add.rotation.scale(Mth.DEG_TO_RAD),
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
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderModel(RenderData data)
	{
		root.render(data.pose, IVertexRenderer.wrap(data.buffer), data.lighting, data.overlay, data.red, data.green, data.blue, data.alpha);
	}
	
	@Override
	public void dispose()
	{
	
	}
}