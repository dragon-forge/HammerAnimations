package org.zeith.hammeranims.api.animsys;

import lombok.val;
import org.jetbrains.annotations.*;
import org.joml.*;
import org.zeith.hammeranims.api.animation.interp.Query;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.IPositionalModel;
import org.zeith.hammeranims.standalone.mc.math.Vec3d;
import org.zeith.hammeranims.standalone.utils.MathHelper;

public interface IAnimatedObject
{
	default float getAnimatedObjectScale()
	{
		return 1F;
	}
	
	default float getAnimatedObjectWidth()
	{
		return 1F;
	}
	
	default float getAnimatedObjectHeight()
	{
		return 1F;
	}
	
	default float getAnimatedObjectDepth()
	{
		return getAnimatedObjectWidth();
	}
	
	default float getAnimationObjectVolume()
	{
		return 1F;
	}
	
	void setupSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
	
	Vec3d getAnimatedObjectPosition();
	
	default @NotNull Query createQuery()
	{
		return new Query();
	}
	
	default IGeometryContainer getObjectModel()
	{
		return getAnimationSystem().getGeometry();
	}
	
	default float getBaseAnimatedYRot(float partialTicks)
	{
		return 0F;
	}
	
	@Nullable
	default Matrix4d getBaseAnimatedMatrix(float partialTicks)
	{
		val pos = getAnimatedObjectPosition();
		if(pos == null) return null;
		return new Matrix4d()
				.translate(pos.x, pos.y, pos.z)
				.rotateY(getBaseAnimatedYRot(partialTicks) * MathHelper.TO_RAD_F);
	}
	
	@Nullable
	default Matrix4d getAnimatedLocatorMatrix(String locator, float partialTicks)
	{
		val mat = getBaseAnimatedMatrix(partialTicks);
		if(mat == null || locator == null || locator.isEmpty()) return mat;
		IPositionalModel posMod = getObjectModel().getPositionalModel();
		posMod.applySystem(partialTicks, getAnimationSystem());
		return posMod.applyLocatorTransforms(mat, locator) ? mat : null;
	}
	
	@Nullable
	default Matrix4d getAnimatedBoneMatrix(String bone, float partialTicks)
	{
		val mat = getBaseAnimatedMatrix(partialTicks);
		if(mat == null || bone == null || bone.isEmpty()) return mat;
		IPositionalModel posMod = getObjectModel().getPositionalModel();
		posMod.applySystem(partialTicks, getAnimationSystem());
		return posMod.applyBoneTransforms(mat, bone) ? mat : null;
	}
	
	@Nullable
	default Vec3d getAnimatedLocatorPosition(String locator, float partialTicks)
	{
		val mat = getAnimatedLocatorMatrix(locator, partialTicks);
		if(mat != null)
		{
			Vector3d vpos = mat.transformPosition(new Vector3d(0));
			return new Vec3d(vpos.x, vpos.y, vpos.z);
		}
		return null;
	}
}