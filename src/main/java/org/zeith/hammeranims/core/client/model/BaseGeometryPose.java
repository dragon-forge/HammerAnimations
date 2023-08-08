package org.zeith.hammeranims.core.client.model;

import net.minecraft.util.math.Vec3d;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.geometry.model.*;

import java.util.function.Predicate;

public class BaseGeometryPose
		extends GeometryPose
{
	public BaseGeometryPose(Predicate<String> availableBones)
	{
		super(availableBones);
	}
	
	public void register(ModelBoneF bone)
	{
		boneTransforms.put(bone.boneName, new GeometryTransforms(
				new Vec3d(bone.offsetX, bone.offsetY, bone.offsetZ),
				new Vec3d(bone.rotateAngleX, bone.rotateAngleY, bone.rotateAngleZ),
				new Vec3d(bone.scaleX, bone.scaleY, bone.scaleZ)
		));
	}
	
	@Override
	public void apply(IAnimationData animation, BlendMode mode, float weight, Query query)
	{
	}
}