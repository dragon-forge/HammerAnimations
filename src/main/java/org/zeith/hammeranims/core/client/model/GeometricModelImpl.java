package org.zeith.hammeranims.core.client.model;

import com.google.common.collect.Lists;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.utils.math.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.interp.BlendMode;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;

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
		for(GeometryDataImpl.BoneConfig bone : bonesBase.values())
		{
			ModelBoneF b = new ModelBoneF(bone.name);
			bones.put(bone.name, b);
			
			Vec3d pivot = bone.pivot;
			b.setRotationPoint((float) pivot.x, (float) pivot.y, (float) pivot.z);
			
			b.setTextureSize(textureWidth, textureHeight);
			for(GeometryDataImpl.CubeConfig cube : bone.cubes)
			{
				Vec3d cubePos = cube.origin.add(pivot.scale(-1));
				b.addBox(
						(float) cubePos.x, (float) cubePos.y, (float) cubePos.z,
						(float) cube.size.x, (float) cube.size.y, (float) cube.size.z,
						cube.u, cube.v,
						cube.inflate, true
				);
			}
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
			
			Vec3d vec = add.translation;
			bone.offsetX = vec.x;
			bone.offsetY = vec.y;
			bone.offsetZ = vec.z;
			
			vec = add.rotation;
			bone.rotateAngleX = (float) vec.x;
			bone.rotateAngleY = (float) vec.y;
			bone.rotateAngleZ = (float) vec.z;
			
			vec = add.scale;
			bone.scaleX = vec.x;
			bone.scaleY = vec.y;
			bone.scaleZ = vec.z;
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
			
			Vec3d translate = Vec3d.ZERO, rotate = Vec3d.ZERO, scale = base.scale;
			
			if(add != null)
			{
				translate = add.translation;
				rotate = add.rotation;
				scale = BlendMode.mult(scale, add.scale);
			}
			
			Vec3d vec = base.translation.add(translate);
			bone.offsetX = vec.x;
			bone.offsetY = vec.y;
			bone.offsetZ = vec.z;
			
			vec = base.rotation.add(rotate);
			bone.rotateAngleX = (float) (vec.x * MathHelper.torad);
			bone.rotateAngleY = (float) (vec.y * MathHelper.torad);
			bone.rotateAngleZ = (float) (vec.z * MathHelper.torad);
			
			bone.scaleX = scale.x;
			bone.scaleY = scale.y;
			bone.scaleZ = scale.z;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderModel(RenderData data)
	{
		UtilsFX.bindTexture(data.texture);
		GlStateManager.scale(0.0625f, 0.0625f, 0.0625f);
		for(ModelBoneF bone : rootBones)
			bone.render(data);
	}
	
	@Override
	public void dispose()
	{
		for(ModelBoneF bone : bones.values())
			bone.dispose();
	}
}