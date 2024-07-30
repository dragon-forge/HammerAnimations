package org.zeith.hammeranims.api.geometry.model;

import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.joml.Matrix4d;
import org.zeith.hammeranims.joml.Matrix4f;

import javax.annotation.*;
import java.util.*;

public interface IPositionalModel
		extends IGenericModel
{
	IPositionalModel EMPTY = new IPositionalModel()
	{
		final GeometryPose emptyPose = new GeometryPose(this::hasBone);
		
		@Override
		public void resetPose()
		{
		}
		
		@Override
		public GeometryPose emptyPose()
		{
			emptyPose.reset();
			return emptyPose;
		}
		
		@Override
		public void applyPose(GeometryPose pose)
		{
		}
		
		@Override
		public IBone getRoot()
		{
			return null;
		}
		
		@Override
		public Set<String> getBoneNames()
		{
			return Collections.emptySet();
		}
		
		@Override
		public Collection<IBone> getBones()
		{
			return Collections.emptySet();
		}
		
		@Nullable
		@Override
		public IBone getBone(String bone)
		{
			return null;
		}
		
		@Override
		public boolean hasBone(String bone)
		{
			return false;
		}
		
		@Override
		public boolean applyBoneTransforms(@Nonnull Matrix4f base, String bone)
		{
			return false;
		}
		
		@Override
		public boolean applyBoneTransforms(@NotNull Matrix4d base, String bone)
		{
			return false;
		}
		
		@Override
		public boolean applyLocatorTransforms(@NotNull Matrix4f base, String locator)
		{
			return false;
		}
		
		@Override
		public boolean applyLocatorTransforms(@NotNull Matrix4d base, String locator)
		{
			return false;
		}
		
		@Override
		public String toString()
		{
			return "IPositionalModel.EMPTY";
		}
	};
	
	/**
	 * Applies bone transforms to the given matrix.
	 * If there is no bone, no transforms will happen.
	 *
	 * @param base
	 * 		The matrix to be transformed.
	 * @param bone
	 * 		The bone to calculate transforms of.
	 *
	 * @return true when the bone is found.
	 */
	boolean applyBoneTransforms(@Nonnull Matrix4f base, String bone);
	
	/**
	 * Applies bone transforms to the given matrix.
	 * If there is no bone, no transforms will happen.
	 *
	 * @param base
	 * 		The matrix to be transformed.
	 * @param bone
	 * 		The bone to calculate transforms of.
	 *
	 * @return true when the bone is found.
	 */
	boolean applyBoneTransforms(@Nonnull Matrix4d base, String bone);
	
	/**
	 * Applies locator transforms to the given matrix.
	 * If there is no locator, no transforms will happen.
	 *
	 * @param base
	 * 		The matrix to be transformed.
	 * @param locator
	 * 		The locator to calculate transforms of.
	 *
	 * @return true when the locator is found.
	 */
	boolean applyLocatorTransforms(@Nonnull Matrix4f base, String locator);
	
	/**
	 * Applies locator transforms to the given matrix.
	 * If there is no locator, no transforms will happen.
	 *
	 * @param base
	 * 		The matrix to be transformed.
	 * @param locator
	 * 		The locator to calculate transforms of.
	 *
	 * @return true when the locator is found.
	 */
	boolean applyLocatorTransforms(@Nonnull Matrix4d base, String locator);
}