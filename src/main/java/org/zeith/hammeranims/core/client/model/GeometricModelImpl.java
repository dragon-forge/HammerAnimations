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
		for(GeometryDataImpl.BoneConfig cfg : bonesBase.values())
		{
			ModelBoneF b = new ModelBoneF(cfg.name);
			bones.put(cfg.name, b);
			
			Vec3d pivot = cfg.pivot.scale(1 / 16F);
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
				Vec3d cubePos = cube.origin.subtract(bone.pivot);

//				HammerAnimations.LOG.info("Add box into {}: @ {} Origin{} Offset{}", bone.name, cubePos, cube.origin, new Vec3d(b.offsetX, b.offsetY, b.offsetZ));
				
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
			
			Vec3d vec = add.translation;
			bone.offsetX = vec.x / 16;
			bone.offsetY = vec.y / 16;
			bone.offsetZ = vec.z / 16;
			
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
				rotate = BlendMode.mult(add.rotation, new Vec3d(-1, -1, -1));
				scale = BlendMode.mult(scale, add.scale);
			}
			
			
			Vec3d vec = base.translation.add(translate);
			bone.offsetX = vec.x / 16;
			bone.offsetY = vec.y / 16;
			bone.offsetZ = vec.z / 16;
			
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
		GlStateManager.scale(-1, 1, 1);
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