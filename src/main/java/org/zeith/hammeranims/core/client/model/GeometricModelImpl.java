package org.zeith.hammeranims.core.client.model;

import com.google.common.collect.Lists;
import com.mojang.math.Matrix4f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.interp.BlendMode;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.mixins.Matrix4fAccessor;
import org.zeith.hammerlib.util.java.Cast;

import java.util.*;

public class GeometricModelImpl
		implements IGeometricModel
{
	public int textureWidth;
	public int textureHeight;
	
	protected List<ModelBoneF> rootBones = Lists.newArrayList();
	protected Map<String, ModelBoneF> bones = new HashMap<>();
	
	protected final BaseGeometryPose basePose = new BaseGeometryPose(this::hasBone);
	
	public GeometricModelImpl(GeometryDataImpl data)
	{
		textureWidth = data.getTextureWidth();
		textureHeight = data.getTextureHeight();
		
		Map<String, GeometryDataImpl.BoneConfig> bonesBase = data.bonesView;
		
		// Setup all bones
		for(GeometryDataImpl.BoneConfig cfg : bonesBase.values())
		{
			ModelBoneF b = new ModelBoneF(cfg.name);
			bones.put(cfg.name, b);
			
			Vec3 pivot = cfg.pivot;
			b.setRotationPoint((float) pivot.x, (float) pivot.y, (float) pivot.z);
		}
		
		// Resolve nesting and register bones
		for(ModelBoneF b : bones.values())
		{
			GeometryDataImpl.BoneConfig cfg = bonesBase.get(b.boneName);
			String parent = cfg.getParent();
			if(parent != null && !parent.isEmpty())
			{
				ModelBoneF pBone = bones.get(parent);
				if(pBone == null)
				{
					HammerAnimations.LOG.warn("[{}]: Unresolved parent bone ({}) for bone {}. The bone {} will be skipped.",
							data.getContainer().getRegistryKey(), parent,
							b.boneName, b.boneName
					);
					continue;
				}
				
				pBone.addChild(b);
				b.parent = pBone;
			}
			
			b.register(this);
		}
		
		for(ModelBoneF rt : rootBones)
			rt.resolveOffsets();
		
		// Setup all bones
		for(GeometryDataImpl.BoneConfig bone : bonesBase.values())
		{
			ModelBoneF b = bones.get(bone.name);
			
			b.setTextureSize(textureWidth, textureHeight);
			for(GeometryDataImpl.CubeConfig cube : bone.cubes)
			{
				Vec3 cubePos = cube.origin.subtract(bone.pivot);

//				HammerAnimations.LOG.info("Add box into {}: @ {} Origin{} Offset{}", bone.name, cubePos, cube.origin, new Vec3(b.offsetX, b.offsetY, b.offsetZ));
				
				b.addBox(
						(float) cubePos.x, (float) cubePos.y, (float) cubePos.z,
						(float) cube.size.x, (float) cube.size.y, (float) cube.size.z,
						cube.uvs, cube.inflate, cube.flipX
				);
			}
		}
	}
	
	public boolean hasBone(String bone)
	{
		return bones.containsKey(bone);
	}
	
	@Override
	public void resetPose()
	{
		for(Map.Entry<String, GeometryTransforms> entry : basePose.getBoneTransforms().entrySet())
		{
			ModelBoneF bone = bones.get(entry.getKey());
			if(bone == null) continue;
			
			GeometryTransforms add = entry.getValue();
			
			Vec3 vec = add.translation;
			bone.offsetX = vec.x;
			bone.offsetY = vec.y;
			bone.offsetZ = vec.z;
			
			vec = add.rotation;
			bone.rotateAngleX = (float) vec.x;
			bone.rotateAngleY = (float) vec.y;
			bone.rotateAngleZ = (float) vec.z;
			
			vec = add.scale;
			bone.scaleX = (float) vec.x;
			bone.scaleY = (float) vec.y;
			bone.scaleZ = (float) vec.z;
		}
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
		Map<String, GeometryTransforms> bonesBase = basePose.getBoneTransforms();
		Map<String, GeometryTransforms> poseBones = pose.getBoneTransforms();
		
		for(String boneKey : bonesBase.keySet())
		{
			ModelBoneF bone = bones.get(boneKey);
			if(bone == null) continue;
			
			GeometryTransforms base = bonesBase.get(boneKey);
			GeometryTransforms add = poseBones.get(boneKey);
			
			Vec3 translate = Vec3.ZERO, rotate = Vec3.ZERO, scale = base.scale;
			
			if(add != null)
			{
				translate = add.translation;
				rotate = BlendMode.mult(add.rotation, new Vec3(-1, 1, 1));
				scale = BlendMode.mult(scale, add.scale);
			}
			
			Vec3 vec = base.translation.add(translate);
			bone.offsetX = vec.x;
			bone.offsetY = vec.y;
			bone.offsetZ = vec.z;
			
			vec = base.rotation.add(rotate);
			bone.rotateAngleX = (float) (vec.x * Mth.DEG_TO_RAD);
			bone.rotateAngleY = (float) (vec.y * Mth.DEG_TO_RAD);
			bone.rotateAngleZ = (float) (vec.z * Mth.DEG_TO_RAD);
			
			bone.scaleX = (float) scale.x;
			bone.scaleY = (float) scale.y;
			bone.scaleZ = (float) scale.z;
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderModel(RenderData data)
	{
		var pose = data.pose;
		pose.pushPose();
		pose.scale(0.0625f, 0.0625f, 0.0625f);
		
		Matrix4f mat = new Matrix4f();
		mat.setIdentity();
		Cast.cast(mat, Matrix4fAccessor.class).setM00(-1);
		pose.mulPoseMatrix(mat);
		
		for(ModelBoneF bone : rootBones)
			bone.render(data);
		
		pose.popPose();
	}
	
	@Override
	public void dispose()
	{
		for(ModelBoneF bone : bones.values())
			bone.dispose();
	}
}